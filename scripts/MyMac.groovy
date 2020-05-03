import org.arl.fjage.*
import org.arl.unet.*
import org.arl.unet.phy.*
import org.arl.unet.mac.*

class MySimplestMac extends UnetAgent {

  @Override
  void setup() {
    register Services.MAC
  }

  @Override
  Message processRequest(Message msg) {

  }

  // expose parameters defined by the MAC service, with just default values

  @Override
  List<Parameter> getParameterList() {
    return allOf(MacParam)                            // advertise the list of parameters
  }

  final boolean channelBusy = true                   // parameters are marked as 'final'
  final int reservationPayloadSize = 0                //  to ensure that they are read-only
  final int ackPayloadSize = 0
  final float maxReservationDuration = 0
  final Float recommendedReservationDuration = null

}
