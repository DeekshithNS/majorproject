import org.arl.fjage.*
import org.arl.unet.*
import org.arl.unet.bb.*
import org.arl.unet.sim.channels.*

///////////////////////////////////////////////////////////////////////////////
// display documentation

println '''
2-node network
--------------

Node A: tcp://localhost:1101, http://localhost:8081/
Node B: tcp://localhost:1102, http://localhost:8082/
Node C: tcp://localhost:1103, http://localhost:8083/
'''

///////////////////////////////////////////////////////////////////////////////
// simulator configuration

//platform = RealTimePlatform   // use real-time mode

channel.model = BasicAcousticChannel
// run the simulation forever
simulate {
  def legNode = node 'A', address:1, location: [ 0.km, 0.km, -15.m], web: 8081, api: 1101, stack: "$home/etc/setup"

  legNode.startup = {

    def phy = agentForService(org.arl.unet.Services.PHYSICAL)
    int count
    add new TickerBehavior(5000,{

      phy<< new DatagramReq(to:3, data:'1234')
       count ++ 
       log.info "Count:" +count

      })

  }

  def attack = node 'B', address : 2, location: [ 0.5.km, 0.km, -15.m], web: 8082, api: 1102, stack: "$home/etc/setup"

  attack.startup = {

  			def bb = agentForService(org.arl.unet.Services.BASEBAND)
        def phy = agentForService(org.arl.unet.Services.PHYSICAL)
        subscribe topic(phy)
        phy.maxPowerLevel = 100
       phy[2].powerLevel = 80

  			add new TickerBehavior(10000,{

  					/*	float freq = 12000
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
  node 'C', address : 3, location: [ 1.km, 0.km, -15.m], web: 8083, api: 1103, stack: {
    container.add 'Floodmsga' , new Floodmsga()
    container.add  'uwlink' , new org.arl.unet.link.ReliableLink()
  }
}
