package liftweb {
package lib {
  
  import net.liftweb.common.{Loggable,Full,Box,Empty,Failure}
  import net.liftweb.paypal.{PaypalIPN,PaypalPDT,PaypalTransactionStatus,PayPalInfo}
  import net.liftweb.http.DoRedirectResponse
  
  /**
   * The handler that implements the paypal integration from Lift. 
   * Note that you must register yourself on developer.paypal.com in order to 
   * get access to setup your paypal authentication tokens and use 
   * the IPN simulator.
   * 
   * Implementing both PDT and IPN is a belt and braces approach really; PDT is
   * generally considered to not be enough on its own, but can give you some immediate
   * information that you might otherwise have to wait for with IPN. Thus, implementing
   * them both lets us explore the integration nicely and get belt and braces.
   */
  object PaypalHandler extends PaypalIPN with PaypalPDT with Loggable {
    
    /**
     * Paypal PDT
     */
    val paypalAuthToken = "something"
    def pdtResponse = {
      case (info, resp) => info.paymentStatus match {
        case Full(PaypalTransactionStatus.CompletedPayment) => DoRedirectResponse.apply("/paypal/success")
        case _ => DoRedirectResponse.apply("/paypal/failure")
      }
    }
    
    /**
     * Paypal IPN
     */
    def actions = {
      case (PaypalTransactionStatus.CompletedPayment,info,_) => 
        logger.info("IPN completed")
      case (PaypalTransactionStatus.FailedPayment,info,_) => 
        logger.info("IPN failed")
      case (status, info, resp) =>
        logger.info("Got a PayPal IPN response of: " + status + ", with info: " + info)
    }
    
  }
  
}}