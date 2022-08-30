package net.mattwamm.froststruck.registries;


import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.mattwamm.froststruck.Froststruck;
import net.mattwamm.froststruck.mixin.WeatherRenderMixin;
import net.mattwamm.froststruck.weather.Blizzard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;



// getString(ctx, "string")
import static com.mojang.brigadier.arguments.StringArgumentType.getString;
// word()
import static com.mojang.brigadier.arguments.StringArgumentType.word;
// literal("foo")
import static net.minecraft.server.command.CommandManager.literal;
// argument("bar", word())
import static net.minecraft.server.command.CommandManager.argument;
// Import everything
import static net.minecraft.server.command.CommandManager.*;



public class CommandRegistry {
    public static void Register(){

        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated, registrationEnvironment) -> {
            dispatcher.register(CommandManager.literal("blizzard")
                    .executes(CommandRegistry::startBlizzard)); // This refers to the "execute" method below.
        });
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            if (dedicated) {
                CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
                    if (environment.dedicated) {
                        dispatcher.register();
                    }
                });

            }
        });
    }

    private static int startBlizzard(CommandContext<ServerCommandSource> context) {
        Blizzard.setBlizzardGradient(1);
        Blizzard.blizzardActive = true;
        return 0;
    }


}
