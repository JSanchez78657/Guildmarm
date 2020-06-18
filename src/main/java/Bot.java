import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Bot {
    public static void main(String[] args) throws IOException, LoginException {
        //This might be necessary when exported to a JAR, the paths might switch up.
        //System.out.println(System.getProperty("user.dir"));
        List<String> keys = Files.readAllLines(Paths.get("src/main/info.txt"));
        String tok = keys.get(0);
        String owner = keys.get(1);
        String auth = keys.get(2);
        EventWaiter waiter = new EventWaiter();
        CommandClientBuilder client = new CommandClientBuilder();
        client.setPrefix("*");
        client.setActivity(Activity.watching("you suffer (*help)."));
        client.setOwnerId(owner);

        JDABuilder.createDefault(tok)
            .setStatus(OnlineStatus.ONLINE)
            .addEventListeners(waiter, client.build())
            .build();
    }
}
