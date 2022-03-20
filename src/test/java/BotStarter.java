import com.see.synology.Plugin;
import net.mamoe.mirai.console.plugin.PluginManager;
import net.mamoe.mirai.console.terminal.MiraiConsoleImplementationTerminal;
import net.mamoe.mirai.console.terminal.MiraiConsoleTerminalLoader;

public class BotStarter {
    public static void main(String[] args) {
        MiraiConsoleTerminalLoader.INSTANCE.startAsDaemon(new MiraiConsoleImplementationTerminal());
        PluginManager.INSTANCE.loadPlugin(Plugin.INSTANCE);
        PluginManager.INSTANCE.enablePlugin(Plugin.INSTANCE);
    }
}
