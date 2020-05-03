import org.arl.fjage.*
import org.arl.unet.sim.channels.*
import org.arl.unet.*
import org.arl.unet.bb.*
///////////////////////////////////////////////////////////////////////////////
// display documentation

println '''
2-node network
--------------

Node A: tcp://localhost:1101, http://localhost:8081/
Node B: tcp://localhost:1102, http://localhost:8086/
Node C: tcp://localhost:1103, http://localhost:8083/
Node C: tcp://localhost:1103, http://localhost:8082/
Node C: tcp://localhost:1103, http://localhost:8084/
'''

///////////////////////////////////////////////////////////////////////////////
// simulator configuration

//channel.model = BasicAcousticChannel


channel.model = BasicAcousticChannel
//platform = RealTimePlatform   // use real-time mode

// run the simulation forever
simulate {
  def n1= node 'A',address:1, location: [ 0.km, 0.km, -15.m], web: 8081, api: 1101, stack: "$home/etc/setup"
  n1.startup = {
  	    def phy = agentForService(org.arl.unet.Services.PHYSICAL)
  		int count;
  		add new TickerBehavior(10000,{
  			phy<< new DatagramReq(to:6, data:'blala')
        

  			 count ++;

  			log.info "Count:" +count
  			});
  }
 def attack1= node 'B', address :2,location: [ 0.8.km, 0.km, -15.m], web: 8082, api: 1102, stack: {
  container.add 'mac', new MyMac();
  //container.shell.addInitrc "${script.parent}/atnode.groovy";
}
attack1.startup = {

        def bb = agentForService(org.arl.unet.Services.BASEBAND)
        def phy = agentForService(org.arl.unet.Services.PHYSICAL)
        subscribe topic(phy)
        phy.maxPowerLevel = 100
     
       

        add new TickerBehavior(10000,{

            /*  float freq = 12000
              float duration = 1000e-3
              int fd = 24000
              int fc = 24000

              int num = duration*fd
              def sig = []
              (0..num-1).each {t->
                double a = 2*Math.PI*(freq-fc)*t/fd
                sig << (int)Math.cos(a)
                sig << (int)Math.sin(a)
              }*/

              sig = [1,0]*12000

              bb << new TxBasebandSignalReq(signal: sig)

              println "signal sent"


          })


  }

  

  
 node 'C', address:3,location: [ 0.8.km, 0.8.km ,-15.m], web: 8083, api: 1103, stack:"$home/etc/setup"



 def attack2 = node 'D', address :4,location: [ 1.6.km, 0.km, -15.m], web: 8084, api: 1104, stack: {
  container.add 'mac', new MyMac();
  //container.shell.addInitrc "${script.parent}/atnode.groovy";
 }
  attack2.startup = {

        def bb = agentForService(org.arl.unet.Services.BASEBAND)
        def phy = agentForService(org.arl.unet.Services.PHYSICAL)
        subscribe topic(phy)
        phy.maxPowerLevel = 100
       
       //phy.signalPowerlevel = 90

        add new TickerBehavior(10000,{

            /*  float freq = 12000
              float duration = 1000e-3
              int fd = 24000
              int fc = 24000

              int num = duration*fd
              def sig = []
              (0..num-1).each {t->
                double a = 2*Math.PI*(freq-fc)*t/fd
                sig << (int)Math.cos(a)
                sig << (int)Math.sin(a)
              }*/

              sig = [1,0]*12000

              bb << new TxBasebandSignalReq(signal: sig)

              println "signal sent"


          })


  }


   node 'E', address :5,location: [ 1.6.km, 1.6.km, -15.m], web: 8085, api: 1105, stack: "$home/etc/setup"

   node 'F', address :6,location: [ 2.4.km, 0.km, -15.m], web: 8086, api: 1106, stack:{
  container.add 'Floodmsga' , new Floodmsga()
    container.add  'uwlink' , new org.arl.unet.link.ReliableLink()
  }

  node 'G', address :7,location: [ 0.8.km, -0.8.km, -15.m], web: 8087, api: 1107, stack: "$home/etc/setup"

  node 'H', address :8,location: [ 1.6.km, -1.6.km, -15.m], web: 8088, api: 1108, stack: "$home/etc/setup"
 
}
