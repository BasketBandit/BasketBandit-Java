package basketbandit.core.modules.developer.commands;

import basketbandit.core.Database;
import basketbandit.core.modules.Command;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CommandDatabaseSetup extends Command {

    public CommandDatabaseSetup() {
        super("dbsetup", "basketbandit.core.modules.developer.ModuleDeveloper", null);
    }

    public CommandDatabaseSetup(MessageReceivedEvent e) {
        super("dbsetup", "basketbandit.core.modules.developer.ModuleDeveloper", null);
        executeCommand(e);
    }

    /**
     * Executes command using MessageReceivedEvent e.
     * @param e; MessageReceivedEvent.
     * @return boolean; if the command executed correctly.
     */
    protected boolean executeCommand(MessageReceivedEvent e) {
        Database database = new Database();
        return database.setupDatabase();
    }
}