import org.arl.fjage.*
import org.arl.unet.*
import java.util.*
import org.arl.unet.phy.*
import org.arl.unet.sim.*
import org.arl.unet.sim.channels.*
import static org.arl.unet.Services.*
import static org.arl.unet.phy.Physical.*
import org.arl.unet.link.ReliableLink

class Floodmsga extends UnetAgent {


   int ackCount =1
    int sentCount=1
int rxstart
  int b
int txstart
int dataFailedCount
int packetDeliveryCount
int recievedPacketCount
float txthresh = 0.7;
float rxthresh = 0.7;
int msgfromone;
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
    if(msg)
      b++


//println sprintf(''+msg+'')
//println sprintf('%d',b)

    switch(msg) {
      case TxFrameNtf :

        sentCount = sentCount + 1

      break

      case RxFrameNtf:
        
        ackCount = ackCount + 1

        if(msg.sender == 'A'){
            msgfromone++
            println sprintf('from 1  %d',msgfromone);
        }

        
      break

      case  RxFrameStartNtf:

        rxstart++


        
        
      break
      case  TxFrameStartNtf:
        

          txstart ++ 
      break

      case DatagramDeliveryNtf:

          packetDeliveryCount++
        
      break

      case DatagramNtf:
          
          recievedPacketCount ++ 


      break
      case DatagramFailureNtf :
        
        dataFailedCount++
        
      break
    }


float txratio
float rxratio

txratio= txstart/sentCount

rxratio = rxstart/ackCount


if (msg instanceof  RxFrameNtf)
{
 //       rxstart++


        if(msg.from == 1){
            msgfromone++
            println sprintf('from 1  %d',msgfromone);
        }

}


if (txratio < txthresh){


// make the node to sleep for  100 seconds



}


if (rxratio < rxthresh){


// make the node to sleep for  100 seconds



}







println sprintf(' ack %d',ackCount)
//println sprintf('rxstart %d',rxstart)
//println sprintf('txstart %d',txstart)
//println sprintf('datantf %d',recievedPacketCount)
//println sprintf('datadel ntf %d',packetDeliveryCount)
}
}