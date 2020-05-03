import {
  Gateway,
  Services,
  MessageClass
} from '/js/unet.js';

var gw = new Gateway();
var nodeaddr = '';
var nodename = '';
var themeConfig;

MessageClass('org.arl.unet.ParameterReq');
MessageClass('org.arl.unet.ParameterRsp');
MessageClass('org.arl.unet.ParamChangeNtf');
MessageClass('org.arl.unet.nodeinfo.NodeInfo');

var themeConfigs = {
  'unetstack': {
    logo: '/img/unet.png',
    favicon : '/img/unet.ico',
    footer: '© UnetStack',
    showTempAndVoltage : false,
    showFWUpdateTab : false
  },
  'subnero': {
    logo: 'modem/img/subnero.png',
    favicon : 'modem/img/subnero.ico',
    footer: '© Subnero Pte Ltd',
    showTempAndVoltage : true,
    showFWUpdateTab : false
  }
};

function getTempAndVolatge(phy){
  //Get Temperature
  phy.get('thermal').then(thermal => {
    /*To fetch and display the temperature from first domain available*/
    var temps = JSON.stringify(thermal.data.info);
    var tempp = JSON.parse(temps);
    var first = Object.keys(tempp)[0];
    var t = parseFloat(tempp[first].temp);
    if (t > 0) document.getElementById('temperature').innerHTML = t.toFixed(1) + ' °C';
  }).catch(ex => {
    console.warn('Could not get temperature: ' + ex);
  });

  //Get Voltage
  phy.get('voltage').then(voltage => {
    var v = parseFloat(voltage);
    if (v > 0) document.getElementById('voltage').innerHTML = v.toFixed(1) + ' V';
  }).catch(ex => {
    console.warn('Could not get voltage' + ex);
  });
}

function getModemModel(phy) {
  //Get Model No
  phy.get('model').then(modelno => {
     if (modelno) document.getElementById('modelno').innerHTML = modelno;
  }).catch(ex => {
     console.warn('Could not get model number: ' + ex);
  });
}

function getSerialNumber(phy){
  //Get Serial no
  phy.get('serial').then(serial => {
     document.getElementById('slabel').innerHTML = 'Serial number :';
     document.getElementById('sno').innerHTML = serial;
     /*Removing Scheduler tab for Jeda based modems*/
     if (serial.includes('-k1')) {
       var schTab = document.getElementById('linkscheduler');
       if (schTab) schTab.parentNode.removeChild(schTab);
     }
  }).catch(ex => {
     console.warn('Could not get serial number ' + ex);
  });
}

function getVersion(phy){
  //Get Version
  phy.get('version').then(version => {
    document.getElementById('vlabel').innerHTML = 'Version :';
    document.getElementById('buildno').innerHTML = version;
  }).catch(ex => {
    console.warn('Could not get Software Version: ' + ex);
  });
}

function getNodeParam(node) {

  Promise.all([ node.get('address'), node.get('nodeName')]).then(val => {
    nodeaddr = val[0];
    nodename = val[1];
    updateNodeIdAndTitle();
  }).catch(() => {
    console.warn('Could not get Node Name/Address');
  });
}

function updateNodeIdAndTitle(){
  document.getElementById('nodeid').innerHTML = nodeaddr;
  document.title = 'Unet | ' + nodename + ' | ' + nodeaddr;
}

function insertFWUpdate(){
  var sidebar = document.getElementById('linkhelp');
  var parentDiv = sidebar.parentNode;

  // Adding Firmware update tab
  var newli = document.createElement('li');
  newli.id = 'linkupdate';

  var newTab = document.createElement('a');
  newTab.href = '/modem/fwupdate.html';
  newTab.innerText = 'Firmware update';

  var newIcon = document.createElement('i');
  newIcon.className = 'fa fa-upload';
  newTab.prepend(newIcon);
  newli.appendChild(newTab);
  parentDiv.insertBefore(newli, sidebar);
}

function updateTheme(themeConfig){

  var link = document.querySelector('link[rel*="icon"]');
  if(link != null) link.href= themeConfig.favicon;

  /*Inserting logo in sidebar*/
    var logo = document.getElementById('logo');
    logo.setAttribute('src', themeConfig.logo);

    /*Inserting footer link*/
    document.getElementById('footerLink').innerHTML = themeConfig.footer;

    /*Addition of Custom tabs to the side bar*/
    if (themeConfig.showFWUpdateTab) insertFWUpdate();
}

export function run() {
   gw.agentForService(Services.PHYSICAL).then(phy => {
    if (phy) {
      gw.subscribe(gw.topic(phy));
      phy.get('vendor').then(vendor => {
        themeConfig = themeConfigs[vendor.toLowerCase()];
        updateTheme(themeConfig);
        if (themeConfig.showTempAndVoltage) getTempAndVolatge(phy);
      });
      getModemModel(phy);
      getSerialNumber(phy);
      getVersion(phy);
      setInterval(getTempAndVolatge.bind(null, phy), 60000);
    }

    gw.agentForService(Services.NODE_INFO).then(node => {
        if (node) {
          gw.subscribe(gw.topic(node));
          getNodeParam(node);
        }
      });

    gw.addMessageListener((msg) => {
      if (msg instanceof ParamChangeNtf) {
        if (msg.paramValues['org.arl.unet.nodeinfo.NodeInfoParam.address']) {
          nodeaddr = msg.paramValues['org.arl.unet.nodeinfo.NodeInfoParam.address'];
        } else if (msg.paramValues['org.arl.unet.nodeinfo.NodeInfoParam.nodeName']) {
          nodename = msg.paramValues['org.arl.unet.nodeinfo.NodeInfoParam.nodeName'];
        }
        updateNodeIdAndTitle();
      }
    });
  });
}
