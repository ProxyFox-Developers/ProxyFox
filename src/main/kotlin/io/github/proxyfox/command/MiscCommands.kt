package io.github.proxyfox.command

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import dev.steyn.brigadierkt.argument
import dev.steyn.brigadierkt.literal
import io.github.proxyfox.importer.import
import io.github.proxyfox.printStep
import io.github.proxyfox.runAsync
import java.io.InputStreamReader
import java.net.URL

object MiscCommands {
    private fun getTimeString(ctx: CommandContext<CommandSource>): Int = runAsync {
        val time: Long = Math.floorDiv(System.currentTimeMillis(), 1000)
        ctx.source.message.channel.createMessage("It is currently <t:$time:f>")
    }
    private fun getHelp(ctx: CommandContext<CommandSource>): Int = runAsync {
        ctx.source.message.channel.createMessage("""To view commands for ProxyFox, visit <https://github.com/ProxyFox-developers/ProxyFox/blob/master/commands.md>
For quick setup:
- pf>system new name
- pf>member new John Doe
- pf>member "John Doe" proxy j:text""")
    }
    private fun getExplanation(ctx: CommandContext<CommandSource>): Int = runAsync {
        ctx.source.message.channel.createMessage("""ProxyFox is modern Discord bot designed to help systems communicate.
It uses discord's webhooks to generate "pseudo-users" which different members of the system can use. Someone will likely be willing to explain further if need be.""")
    }
    private fun getInvite(ctx: CommandContext<CommandSource>): Int = runAsync {
        ctx.source.message.channel.createMessage("Proxyfox invites are temporarily disabled, as we're transitioning to a new bot. Contact Octal#9139 if you need an invite. https://discord.gg/M2uBsJmRNT")
    }
    private fun getSource(ctx: CommandContext<CommandSource>): Int = runAsync {
        ctx.source.message.channel.createMessage("Source code for ProxyFox is available at <https://github.com/ProxyFox-developers/ProxyFox>!")
    }
    private fun enableServerProxy(ctx: CommandContext<CommandSource>): Int = runAsync {
        //TODO: not implemented
    }
    private fun disableServerProxy(ctx: CommandContext<CommandSource>): Int = runAsync {
        //TODO: not implemented
    }
    private fun enableAutoProxy(ctx: CommandContext<CommandSource>): Int = runAsync {
        //TODO: not implemented
    }
    private fun disableAutoProxy(ctx: CommandContext<CommandSource>): Int = runAsync {
        //TODO: not implemented
    }
    private fun changeProxyRole(ctx: CommandContext<CommandSource>): Int = runAsync {
        //TODO: not implemented
    }
    private fun removeProxyRole(ctx: CommandContext<CommandSource>): Int = runAsync {
        //TODO: not implemented
    }
    private suspend fun importSystemCommon(ctx: CommandContext<CommandSource>, url: String) {
        kotlin.runCatching {
            val link = URL(url)
            link.openStream().use {
                val importer = import(InputStreamReader(it))

            }
        }
    }
    private fun importSystem(ctx: CommandContext<CommandSource>): Int = runAsync {
        importSystemCommon(ctx,ctx.source.message.attachments.first().url)
    }
    private fun importSystemLinked(ctx: CommandContext<CommandSource>): Int = runAsync {
        importSystemCommon(ctx,StringArgumentType.getString(ctx, "link"))
    }

    private fun exportSystem(ctx: CommandContext<CommandSource>): Int = runAsync {
        //TODO: not implemented
    }

    suspend fun register() {
        printStep("Registering misc commands",2)
        command("time") {
            executes(MiscCommands::getTimeString)
        }
        commands(arrayOf("help","commands","?")) {
            executes(MiscCommands::getHelp)
        }
        commands(arrayOf("explain","why")) {
            executes(MiscCommands::getExplanation)
        }
        commands(arrayOf("invite","link")) {
            executes(MiscCommands::getInvite)
        }
        command("export") {
            executes(MiscCommands::exportSystem)
        }
        command("source") {
            executes(MiscCommands::getSource)
        }

        commands(arrayOf("proxy","serverproxy")) {
            literal("on") {
                executes(MiscCommands::enableServerProxy)
            }
            literal("off") {
                executes(MiscCommands::disableServerProxy)
            }
            executes(::noSubCommandError)
        }

        commands(arrayOf("autoproxy","ap")) {
            literal("on") {
                executes(MiscCommands::enableAutoProxy)
            }
            literal("off") {
                executes(MiscCommands::disableAutoProxy)
            }
            executes(::noSubCommandError)
        }

        command("role") {
            argument("role",StringArgumentType.greedyString()) {
                executes(MiscCommands::changeProxyRole)
            }
            literal("clear") {
                executes(MiscCommands::removeProxyRole)
            }
            executes(::noSubCommandError)
        }

        command("import") {
            argument("link",StringArgumentType.greedyString()) {
                executes(MiscCommands::importSystemLinked)
            }
            executes(MiscCommands::importSystem)
        }
    }
}