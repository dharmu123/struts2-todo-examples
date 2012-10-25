package eu.strutters.example.todo.interceptor;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.MethodFilterInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 * TransactionalInterceptor.
 *
 * @author Rene Gielen
 */
public class TransactionalInterceptor extends MethodFilterInterceptor {

    private static final Logger LOG = LoggerFactory.getLogger(TransactionalInterceptor.class);

    PlatformTransactionManager transactionManager;

    public void setTransactionManager( PlatformTransactionManager transactionManager ) {
        this.transactionManager = transactionManager;
    }

    protected String doIntercept( ActionInvocation actionInvocation ) throws Exception {
        if (transactionManager != null) {

            TransactionStatus status = createTransactionContext();

            try {
                // Invoke Action
                String result = actionInvocation.invoke();

				conditionallyMarkForRollback(status, result);

				endTransaction(status);
				return result;

            } catch ( Exception e ) {
				return rollbackAndThrow(status, e);


			}
        } else {

            // Transaction manager was not configured, proceed without transaction context
            if (LOG.isInfoEnabled()) {
            	LOG.info("[doIntercept]: No TransactionManager found, proceeding without setting up transactional context.");
            }
            return actionInvocation.invoke();
        }
    }

	private void conditionallyMarkForRollback( TransactionStatus status, String result ) {
		if (Action.INPUT.equals(result)|| Action.ERROR.equals(result)) {
			// Special treatment of INPUT/ERROR result, forcing rollback
			status.setRollbackOnly();

			if (LOG.isInfoEnabled()) {
				LOG.info("[doIntercept]: Setting current transaction to rollbackOnly due to INPUT action invocation result");
			}
		}
	}

	private void endTransaction( TransactionStatus status ) {
		if (!status.isCompleted()) {
			// Life's good, lets's commit
			transactionManager.commit(status);

			if (LOG.isDebugEnabled()) {
				LOG.debug("[doIntercept]: Transaction ended regularly.");
			}
		} else {
			// for some reason, the transaction was already completed
			if (LOG.isWarnEnabled()) {
				LOG.warn("[doIntercept]: Transaction already marked as completed, not performing commit");
			}
		}
	}

	private TransactionStatus createTransactionContext() {
		TransactionStatus status;DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setName("TransactionalInterceptor@" + def.hashCode());
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

		// Create Transaction Context
		status = transactionManager.getTransaction(def);
		if (LOG.isDebugEnabled()) {
			LOG.debug("[doIntercept]: Transaction context created.");
		}
		return status;
	}

	private String rollbackAndThrow( TransactionStatus status, Exception e ) throws Exception {
		// Bad things happened - an Exception popping up here is should force a rollback
		if (!status.isCompleted()) {

			transactionManager.rollback(status);

			if (LOG.isWarnEnabled()) {
				LOG.warn("[doIntercept]: Rolled back due to uncatched exception: " + e.getMessage());
			}
		} else {
			// for some reason, the transaction was already completed
			if (LOG.isWarnEnabled()) {
				LOG.warn("[doIntercept]: Transaction already marked as completed, not performing rollback indicated by uncatched Excption " + e.getMessage());
			}
		}
		// Finally, we'll be nicely rethrowing the exception
		throw e;
	}

}