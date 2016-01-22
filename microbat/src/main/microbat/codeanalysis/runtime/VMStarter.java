package microbat.codeanalysis.runtime;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import sav.strategies.vm.BootstrapPlugin;

import com.sun.jdi.VirtualMachine;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;
import com.sun.jdi.connect.LaunchingConnector;
import com.sun.jdi.connect.VMStartException;


@SuppressWarnings("restriction")
public class VMStarter {
	
	public VirtualMachine start(){
		VirtualMachine vm = null;
		LaunchingConnector connector = getCommandLineConnector();
		
		Map<String, Connector.Argument> arguments =
		           connectorArguments(connector, "com.Main");
        try {
        	vm = connector.launch(arguments);
        	return vm;
        } catch (IOException exc) {
            throw new Error("Unable to launch target VM: " + exc);
        } catch (IllegalConnectorArgumentsException exc) {
            throw new Error("Internal error: " + exc);
        } catch (VMStartException exc) {
            throw new Error("Target VM failed to initialize: " +
                            exc.getMessage());
        }
        
        
	}
	
	private LaunchingConnector getCommandLineConnector() {
		List<Connector> conns = BootstrapPlugin.virtualMachineManager().allConnectors();
		for (Connector conn : conns) {
			if (conn.name().equals("com.sun.jdi.CommandLineLaunch")){
				return (LaunchingConnector) conn;				
			}
		}
		throw new Error("No launching connector found");
	}
	
	/**
     * Return the launching connector's arguments.
     */
    private Map<String, Connector.Argument> connectorArguments(LaunchingConnector connector, String mainArgs) {
        Map<String, Connector.Argument> arguments = connector.defaultArguments();
        Connector.Argument mainArg =
                           (Connector.Argument)arguments.get("main");
        if (mainArg == null) {
            throw new Error("Bad launching connector");
        }
        mainArg.setValue(mainArgs);

        // We need a VM that supports watchpoints
        Connector.Argument optionArg =
            (Connector.Argument)arguments.get("options");

        
        if (optionArg == null) {
            throw new Error("Bad launching connector");
        }
        optionArg.setValue("-cp \"F:\\workspace\\runtime-debugging\\Test\\bin\"");
        
        return arguments;
    }
}
