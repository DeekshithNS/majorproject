def startupScript = new File(home, 'scripts/startup.groovy')
def savedStateScript = new File(home, 'scripts/saved-state.groovy')

// defaults
phy[1].modulation = 'fhbfsk'
if (phy[1].fmin == 19040) phy[1].fmin = 22080     // optimal band for 3S
try {
  phy[2].modulation = 'ofdm'
} catch (Exception ex) {
  phy[2].modulation = 'fhbfsk'
  if (phy[2].fmin == 19040) phy[2].fmin = 22080   // optimal band for 3S
}

// use startup script, or default to the following
if (startupScript.exists()) run startupScript

// set current state as baseline for saved states
agent('state') << new org.arl.unet.state.ClearStateReq()

// load any saved state
if (savedStateScript.exists()) run savedStateScript
