/**
 * Master Switch device handler.
 *
 * This device handler will allow things like webCoRE to have programmable switches
 * which act as master switches to a subset of devices.  For example, blinds and shades
 * can be controlled by a single switch, but if their status is ever updated directly
 * (or manually), this could throw the master switch out of sync such that it doesn't work.
 * This device handler lets the caller set only the value of the on/off switch to keep
 * everything in sync.
 
 * Problem example:
 *
 * I have automatic blinds and shades.  I want a master switch to raise/lower
 * them at the same time, so I make a virtual switch and use webCoRE to control
 * each device.  However, let's say my wife raises the blinds and shades with the remote
 * control.  Now, the blinds/shades are up, but the master switch says that they're off.
 * If I were to leave the house, which uses the master switch to turn off the blinds and
 * shades, they would remain up since there is no state change happening (the master switch
 * is already off).  If I were to try and keep them in sync, just setting the on/off button
 * on the master switch would fire events that would trigger unwanted actions.  Using
 * this device handler will allow me to keep the master switche's on/off value in sync
 * without triggering actions on the devices.
 *
 * Author: Darin Spivey, April 2018
 */

metadata {
	definition (name: "Master Switch", namespace: "darinspivey", author: "Darin Spivey") {
        capability "Switch"
        capability "Switch Level"
        capability "Actuator"
        capability "Sensor"
        
        command "switchLabel", ["string"]
    }

	simulator {}

	// UI tile definitions
	tiles(scale: 2) {
		multiAttributeTile(name: "switch", type: "lighting", width: 6, height: 4, canChangeIcon: true, canChangeBackground: true) {
			tileAttribute("device.switch", key: "PRIMARY_CONTROL") {
    			attributeState "off", label: '${name}', action: "switch.on", backgroundColor: "#ffffff",icon: "https://raw.githubusercontent.com/MichaelStruck/SmartThings/master/Other-SmartApps/AlexaHelper/AH-Off.png", nextState: "turningOn"
		      	attributeState "on", label: '${name}', action: "switch.off", backgroundColor: "#79b821",icon: "https://raw.githubusercontent.com/MichaelStruck/SmartThings/master/Other-SmartApps/AlexaHelper/AH-On.png",  nextState: "turningOff"
				attributeState "turningOff", label: '${name}', action: "switch.on",backgroundColor: "#ffffff", icon: "https://raw.githubusercontent.com/MichaelStruck/SmartThings/master/Other-SmartApps/AlexaHelper/AH-Off.png",  nextState: "turningOn"
		      	attributeState "turningOn", label: '${name}', action: "switch.off",backgroundColor: "#79b821", icon: "https://raw.githubusercontent.com/MichaelStruck/SmartThings/master/Other-SmartApps/AlexaHelper/AH-On.png", nextState: "turningOff"
        	}
        		tileAttribute("device.level", key: "SLIDER_CONTROL") {
            		attributeState "level", action:"switch level.setLevel"
        		}
        		tileAttribute("level", key: "SECONDARY_CONTROL") {
              		attributeState "level", label: 'Light dimmed to ${currentValue}%'
        		}    
		}
        valueTile("lValue", "device.level", inactiveLabel: true, height:2, width:2, decoration: "flat") {  
			state "levelValue", label:'${currentValue}%', unit:""
        }  
        standardTile("on", "device.switch", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "default", label:'on', action:"switch.on", icon:"st.switches.light.on"
		}
		standardTile("off", "device.switch", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "default", label:'off', action:"switch.off", icon:"st.switches.light.off"
		}
		main "switch"
		details(["switch","on","lValue","off"])

	}
}

def installed() {
}

def parse(String description) {
}

def switchLabel(onOrOff) {
  if (onOrOff != 'on' && onOrOff != 'off') return
  sendEvent(name: "switch", value: onOrOff, isStateChange: false)
  log.debug "Setting the switch label to $onOrOff"
}

def on() {
	sendEvent(name: "switch", value: "on", isStateChange: true)
}

def off() {
	sendEvent(name: "switch", value: "off", isStateChange: true)
}

def setLevel(val) {
    // The device will hang if given a value outside of the allowable range.
    if (val < 0) {
    	val = 0
    }
    
    if(val > 100) {
    	val = 100
    }
    
    if (val == 0) { 
    	sendEvent(name: "level", value:val, isStateChange: true)
    }
    else
    {
    	sendEvent(name: "level", value: val, isStateChange: true)
    	sendEvent(name: "switch.setLevel", value: val, isStateChange: true)
    }
}
