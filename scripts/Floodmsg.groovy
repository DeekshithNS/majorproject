import org.arl.fjage.*
import org.arl.unet.*

class Floodmsg extends UnetAgent {

  @Override
  void startup() {
    // this method is called just after the stack is running
    // look up other agents and services here, as needed
    // subscribe to topics of interest to get notifications

      
      def phy = agentForService Services.PHYSICAL
      subscribe topic(phy)
  }

  void processMessage(Message msg) {
    // process other messages, such as notifications here
    // if a message is not interesting, it can be safely just ignored

 //if (msg instanceof DatagramNtf ){

      //int i = 10

      while(1){

    send new DatagramReq(recipient: msg.sender, to: Address.BROADCAST, protocol: Protocol.DATA)
      
}
 }

//}
}