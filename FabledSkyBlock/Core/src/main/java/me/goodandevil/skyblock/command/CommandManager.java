package me.goodandevil.skyblock.command;

import me.goodandevil.skyblock.SkyBlock;
import me.goodandevil.skyblock.config.FileManager;
import me.goodandevil.skyblock.config.FileManager.Config;
import me.goodandevil.skyblock.menus.ControlPanel;
import me.goodandevil.skyblock.message.MessageManager;
import me.goodandevil.skyblock.sound.SoundManager;
import me.goodandevil.skyblock.utils.ChatComponent;
import me.goodandevil.skyblock.utils.version.Sounds;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandManager implements CommandExecutor, TabCompleter {

	private final SkyBlock skyblock;
	private List<SubCommand> islandCommands;
	private List<SubCommand> adminCommands;

	public CommandManager(SkyBlock skyblock) {
		this.skyblock = skyblock;

		skyblock.getCommand("island").setExecutor(this);
		skyblock.getCommand("island").setTabCompleter(this);

		registerSubCommands();
	}

	public void registerSubCommands() {
		islandCommands = Arrays.asList(
				new me.goodandevil.skyblock.command.commands.island.AcceptCommand(),
				new me.goodandevil.skyblock.command.commands.island.BanCommand(),
				new me.goodandevil.skyblock.command.commands.island.BankCommand(),
				new me.goodandevil.skyblock.command.commands.island.BansCommand(),
				new me.goodandevil.skyblock.command.commands.island.BiomeCommand(),
				new me.goodandevil.skyblock.command.commands.island.BorderCommand(),
				new me.goodandevil.skyblock.command.commands.island.CancelCommand(),
				new me.goodandevil.skyblock.command.commands.island.ChatCommand(),
				new me.goodandevil.skyblock.command.commands.island.CloseCommand(),
				new me.goodandevil.skyblock.command.commands.island.ConfirmCommand(),
				new me.goodandevil.skyblock.command.commands.island.ControlPanelCommand(),
				new me.goodandevil.skyblock.command.commands.island.CoopCommand(),
				new me.goodandevil.skyblock.command.commands.island.CreateCommand(),
				new me.goodandevil.skyblock.command.commands.island.CurrentCommand(),
				new me.goodandevil.skyblock.command.commands.island.DeleteCommand(),
				new me.goodandevil.skyblock.command.commands.island.DemoteCommand(),
				new me.goodandevil.skyblock.command.commands.island.DenyCommand(),
				new me.goodandevil.skyblock.command.commands.island.InformationCommand(),
				new me.goodandevil.skyblock.command.commands.island.InviteCommand(),
				new me.goodandevil.skyblock.command.commands.island.KickAllCommand(),
				new me.goodandevil.skyblock.command.commands.island.KickCommand(),
				new me.goodandevil.skyblock.command.commands.island.LeaderboardCommand(),
				new me.goodandevil.skyblock.command.commands.island.LeaveCommand(),
				new me.goodandevil.skyblock.command.commands.island.LevelCommand(),
				new me.goodandevil.skyblock.command.commands.island.MembersCommand(),
				new me.goodandevil.skyblock.command.commands.island.OpenCommand(),
				new me.goodandevil.skyblock.command.commands.island.OwnerCommand(),
				new me.goodandevil.skyblock.command.commands.island.PromoteCommand(),
				new me.goodandevil.skyblock.command.commands.island.PublicCommand(),
				new me.goodandevil.skyblock.command.commands.island.SetSpawnCommand(),
				new me.goodandevil.skyblock.command.commands.island.SettingsCommand(),
				new me.goodandevil.skyblock.command.commands.island.TeleportCommand(),
				new me.goodandevil.skyblock.command.commands.island.UnbanCommand(),
				new me.goodandevil.skyblock.command.commands.island.UnlockCommand(),
				new me.goodandevil.skyblock.command.commands.island.UpgradeCommand(),
				new me.goodandevil.skyblock.command.commands.island.ValueCommand(),
				new me.goodandevil.skyblock.command.commands.island.VisitCommand(),
				new me.goodandevil.skyblock.command.commands.island.VisitorsCommand(),
				new me.goodandevil.skyblock.command.commands.island.VoteCommand(),
				new me.goodandevil.skyblock.command.commands.island.WeatherCommand()
		);

		adminCommands = Arrays.asList(
				new me.goodandevil.skyblock.command.commands.admin.AddUpgradeCommand(),
				new me.goodandevil.skyblock.command.commands.admin.CreateCommand(),
				new me.goodandevil.skyblock.command.commands.admin.DeleteCommand(),
				new me.goodandevil.skyblock.command.commands.admin.GeneratorCommand(),
				new me.goodandevil.skyblock.command.commands.admin.LevelCommand(),
				new me.goodandevil.skyblock.command.commands.admin.LevelScanCommand(),
				new me.goodandevil.skyblock.command.commands.admin.OwnerCommand(),
				new me.goodandevil.skyblock.command.commands.admin.RefreshHologramsCommand(),
				new me.goodandevil.skyblock.command.commands.admin.ReloadCommand(),
				new me.goodandevil.skyblock.command.commands.admin.RemoveHologramCommand(),
				new me.goodandevil.skyblock.command.commands.admin.RemoveUpgradeCommand(),
				new me.goodandevil.skyblock.command.commands.admin.SetBiomeCommand(),
				new me.goodandevil.skyblock.command.commands.admin.SetHologramCommand(),
				new me.goodandevil.skyblock.command.commands.admin.SetSizeCommand(),
				new me.goodandevil.skyblock.command.commands.admin.SetSpawnCommand(),
				new me.goodandevil.skyblock.command.commands.admin.SettingsCommand(),
				new me.goodandevil.skyblock.command.commands.admin.StructureCommand(),
				new me.goodandevil.skyblock.command.commands.admin.UpgradeCommand()
		);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
		if (command.getName().equalsIgnoreCase("island")) {
			MessageManager messageManager = skyblock.getMessageManager();
			SoundManager soundManager = skyblock.getSoundManager();
			FileManager fileManager = skyblock.getFileManager();

			Config config = fileManager.getConfig(new File(skyblock.getDataFolder(), "language.yml"));
			FileConfiguration configLoad = config.getFileConfiguration();

			Player player = null;

			if (sender instanceof Player) {
				player = (Player) sender;
			}

			if (args.length == 0) {
				if (player == null) {
					sendConsoleHelpCommands(sender);
				} else {
					if (skyblock.getIslandManager().getIsland(player) == null) {
						Bukkit.getServer().getScheduler().runTask(skyblock, () -> Bukkit.getServer().dispatchCommand(sender, "island create"));
					} else {
						boolean canUseControlPanel = player.hasPermission("fabledskyblock.*")
								|| player.hasPermission("fabledskyblock.island.*")
								|| player.hasPermission("fabledskyblock.island.controlpanel");

						if (!canUseControlPanel) {
							messageManager.sendMessage(player, configLoad.getString("Command.PermissionDenied.Island.Message"));
							soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
							return true;
						}

						ControlPanel.getInstance().open(player);
						soundManager.playSound(player, Sounds.CHEST_OPEN.bukkitSound(), 1.0F, 1.0F);
					}
				}

				return true;
			}

			SubCommand subCommand;
			boolean isAdmin;

			if (args[0].equalsIgnoreCase("help")) {
				if (player == null) {
					sendConsoleHelpCommands(sender);
				} else {
					boolean canUseHelp = player.hasPermission("fabledskyblock.*")
							|| player.hasPermission("fabledskyblock.island.*")
							|| player.hasPermission("fabledskyblock.island.help");

					if (!canUseHelp) {
						messageManager.sendMessage(player, configLoad.getString("Command.PermissionDenied.Island.Message"));
						soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
						return true;
					}

					int page = -1;

					if (!fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml"))
							.getFileConfiguration().getBoolean("Command.Help.List")) {
						page = 1;

						if (args.length == 2) {
							if (args[1].matches("[0-9]+")) {
								page = Integer.valueOf(args[1]);
							} else {
								messageManager.sendMessage(player,
										configLoad.getString("Command.Island.Help.Integer.Message"));
								soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);

								return true;
							}
						}
					}

					sendPlayerIslandHelpCommands(player, page);
				}

				return true;
			} else if (args[0].equalsIgnoreCase("admin")) {
				if (args.length == 1 || args[1].equalsIgnoreCase("help")) {
					if (player == null) {
						sendConsoleHelpCommands(sender);
					} else {
						boolean canUseHelp = player.hasPermission("fabledskyblock.*")
								|| player.hasPermission("fabledskyblock.admin.*")
								|| player.hasPermission("fabledskyblock.admin.help");

						if (!canUseHelp) {
							messageManager.sendMessage(player, configLoad.getString("Command.PermissionDenied.Admin.Message"));
							soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
							return true;
						}

						int page = -1;

						if (!fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml"))
								.getFileConfiguration().getBoolean("Command.Help.List")) {
							page = 1;

							if (args.length == 3) {
								if (args[2].matches("[0-9]+")) {
									page = Integer.valueOf(args[2]);
								} else {
									messageManager.sendMessage(player,
											configLoad.getString("Command.Island.Help.Integer.Message"));
									soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F,
											1.0F);

									return true;
								}
							}
						}

						sendPlayerAdminHelpCommands(player, page);
					}

					return true;
				}

				subCommand = getAdminSubCommand(args[1]);
				isAdmin = true;
			} else {
				subCommand = getIslandSubCommand(args[0]);
				isAdmin = false;
			}

			if (subCommand == null) {
				messageManager.sendMessage(sender, configLoad.getString("Command.Island.Argument.Unrecognised.Message"));
				soundManager.playSound(sender, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
				return true;
			}

			if (!subCommand.hasPermission(sender, isAdmin)) {
				messageManager.sendMessage(sender, configLoad.getString("Command.PermissionDenied." + (isAdmin ? "Admin" : "Island") + ".Message"));
				soundManager.playSound(sender, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
				return true;
			}

			List<String> arguments = new ArrayList<>(Arrays.asList(args));
			arguments.remove(args[0]);

			if (adminCommands.contains(subCommand)) {
				arguments.remove(args[1]);
			}

			if (sender instanceof Player) {
				subCommand.onCommandByPlayer(player, arguments.toArray(new String[0]));
			} else if (sender instanceof ConsoleCommandSender) {
				subCommand.onCommandByConsole((ConsoleCommandSender) sender,
						arguments.toArray(new String[0]));
			}
		}

		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
		if (!(sender instanceof Player)) {
			return null;
		}

		boolean isAdmin = sender.hasPermission("fabledskyblock.admin.*") || sender.hasPermission("fabledskyblock.*");

		if (command.getName().equalsIgnoreCase("island")) {
			List<String> commandAliases = new ArrayList<>();

			if (args.length == 1) {
				if (args[0] == null || args[0].isEmpty()) {
					commandAliases.add("admin");

					for (SubCommand subCommandList : islandCommands) {
						commandAliases.add(subCommandList.getName());
					}
				} else {
					if (isAdmin) {
						if ("admin".contains(args[0].toLowerCase())) {
							commandAliases.add("admin");
						}
					}

					for (SubCommand subCommandList : islandCommands) {
						if (subCommandList.getName().toLowerCase().contains(args[0].toLowerCase())) {
							commandAliases.add(subCommandList.getName());
						}
					}
				}
			} else if (args.length == 2) {
				if (isAdmin) {
					if (args[0].equalsIgnoreCase("admin")) {
						if (args[1] == null || args[1].isEmpty()) {
							for (SubCommand subCommandList : adminCommands) {
								commandAliases.add(subCommandList.getName());
							}
						} else {
							for (SubCommand subCommandList : adminCommands) {
								if (subCommandList.getName().toLowerCase().contains(args[1].toLowerCase())) {
									commandAliases.add(subCommandList.getName());
								}
							}
						}
					}
				}

				List<String> arguments = getIslandArguments(args[0], args[1]);

				if (arguments.size() != 0) {
					commandAliases.addAll(arguments);
				}
			} else if (args.length == 3) {
				if (isAdmin) {
					if (args[0].equalsIgnoreCase("admin")) {
						List<String> arguments = getAdminArguments(args[1], args[2]);

						if (arguments.size() != 0) {
							commandAliases.addAll(arguments);
						}
					}
				}
			}

			if (commandAliases.size() != 0) {
				return commandAliases;
			}
		}

		return null;
	}

	public List<String> getIslandArguments(String arg1, String arg2) {
		return this.getArguments(islandCommands, arg1, arg2);
	}

	public List<String> getAdminArguments(String arg1, String arg2) {
		return this.getArguments(adminCommands, arg1, arg2);
	}

	public List<String> getArguments(List<SubCommand> subCommands, String arg1, String arg2) {
		List<String> arguments = new ArrayList<>();

		for (SubCommand subCommandList : subCommands) {
			if (arg1.equalsIgnoreCase(subCommandList.getName())) {
				if (arg2 == null || arg2.isEmpty()) {
					arguments.addAll(Arrays.asList(subCommandList.getArguments()));
				} else {
					for (String argumentList : subCommandList.getArguments()) {
						if (argumentList.contains(arg2.toLowerCase())) {
							arguments.add(argumentList);

							break;
						}
					}
				}

				break;
			}
		}

		return arguments;
	}

	public void sendPlayerIslandHelpCommands(Player player, int page) {
		this.sendPlayerHelpCommands(player, islandCommands, page, false);
	}

	public void sendPlayerAdminHelpCommands(Player player, int page) {
		this.sendPlayerHelpCommands(player, adminCommands, page, true);
	}

	public void sendPlayerHelpCommands(Player player, List<SubCommand> subCommands, int page, boolean isAdmin) {
		FileManager fileManager = skyblock.getFileManager();

		Config config = fileManager.getConfig(new File(skyblock.getDataFolder(), "language.yml"));
		FileConfiguration configLoad = config.getFileConfiguration();

		int pageSize = 7;

		int nextEndIndex = subCommands.size() - page * pageSize, index = page * pageSize - pageSize,
				endIndex = index >= subCommands.size() ? subCommands.size() - 1 : index + pageSize;
		boolean showAlises = fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml"))
				.getFileConfiguration().getBoolean("Command.Help.Aliases.Enable");

		if (nextEndIndex <= -7) {
			skyblock.getMessageManager().sendMessage(player, configLoad.getString("Command.Island.Help.Page.Message"));
			skyblock.getSoundManager().playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);

			return;
		}

		String subCommandText = "";

		if (isAdmin) {
			subCommandText = "admin ";
		}

		for (String helpLines : configLoad.getStringList("Command.Island.Help.Lines")) {
			if (helpLines.contains("%type")) {
				helpLines = helpLines.replace("%type", "Admin");
			}

			if (helpLines.contains("%commands")) {
				String[] sections = helpLines.split("%commands");
				String prefix = "", suffix = "";

				if (sections.length >= 1) {
					prefix = ChatColor.translateAlternateColorCodes('&', sections[0]);
				}

				if (sections.length == 2) {
					suffix = ChatColor.translateAlternateColorCodes('&', sections[1]);
				}

				if (page == -1) {
					for (SubCommand subCommand : subCommands) {
						StringBuilder commandAliases = new StringBuilder();

						if (showAlises) {
							for (int i = 0; i < subCommand.getAliases().length; i++) {
								commandAliases.append("/").append(subCommand.getAliases()[i]);
							}
						}

						player.spigot()
								.sendMessage(new ChatComponent(
										prefix.replace("%info", subCommand.getInfo()) + "/island "
												+ subCommandText + subCommand.getName() + commandAliases
												+ suffix.replace("%info", subCommand.getInfo()),
										false, null, null,
										new HoverEvent(HoverEvent.Action.SHOW_TEXT,
												new ComponentBuilder(subCommand.getInfo()).create()))
										.getTextComponent());
					}
				} else {
					for (; index < endIndex; index++) {
						if (subCommands.size() > index) {
							SubCommand subCommandFromIndex = subCommands.get(index);
							StringBuilder commandAliases = new StringBuilder();

							if (showAlises) {
								for (int i = 0; i < subCommandFromIndex.getAliases().length; i++) {
									commandAliases.append("/").append(subCommandFromIndex.getAliases()[i]);
								}
							}

							player.spigot()
									.sendMessage(new ChatComponent(
											prefix.replace("%info", subCommandFromIndex.getInfo()) + "/island "
													+ subCommandText + subCommandFromIndex.getName() + commandAliases
													+ suffix.replace("%info", subCommandFromIndex.getInfo()),
											false, null, null,
											new HoverEvent(HoverEvent.Action.SHOW_TEXT,
													new ComponentBuilder(subCommandFromIndex.getInfo()).create()))
															.getTextComponent());
						}
					}
				}
			} else {
				skyblock.getMessageManager().sendMessage(player, helpLines);
			}
		}

		if (page != -1) {
			if (!(nextEndIndex == 0 || nextEndIndex < 0)) {
				if (page == 1) {
					player.spigot()
							.sendMessage(
									new ChatComponent(
											ChatColor.translateAlternateColorCodes(
													'&', configLoad.getString("Command.Island.Help.Word.Next")),
											false, null,
											new ClickEvent(ClickEvent.Action.RUN_COMMAND,
													"/island " + subCommandText + "help " + (page + 1)),
											null).getTextComponent());
				} else {
					player.spigot()
							.sendMessage(
									new ChatComponent(
											ChatColor.translateAlternateColorCodes('&',
													configLoad.getString("Command.Island.Help.Word.Previous")),
											false, null,
											new ClickEvent(ClickEvent.Action.RUN_COMMAND,
													"/island " + subCommandText + "help " + (page - 1)),
											null).addExtraChatComponent(
													new ChatComponent(" "
															+ ChatColor.translateAlternateColorCodes('&',
																	configLoad
																			.getString("Command.Island.Help.Word.Pipe"))
															+ " ", false, null, null, null))
													.addExtraChatComponent(new ChatComponent(
															ChatColor.translateAlternateColorCodes('&',
																	configLoad.getString(
																			"Command.Island.Help.Word.Next")),
															false, null,
															new ClickEvent(ClickEvent.Action.RUN_COMMAND,
																	"/island " + subCommandText + "help " + (page + 1)),
															null))
													.getTextComponent());
				}
			} else {
				if (page != 1) {
					player.spigot()
							.sendMessage(new ChatComponent(
									ChatColor.translateAlternateColorCodes(
											'&', configLoad.getString("Command.Island.Help.Word.Previous")),
									false, null,
									new ClickEvent(ClickEvent.Action.RUN_COMMAND,
											"/island " + subCommandText + "help " + (page - 1)),
									null).getTextComponent());
				}
			}
		}

		skyblock.getSoundManager().playSound(player, Sounds.ARROW_HIT.bukkitSound(), 1.0F, 1.0F);
	}

	public void sendConsoleHelpCommands(CommandSender sender) {
		sender.sendMessage("SkyBlock - Console Commands");

		String[] commands = { "delete", "owner", "reload", "removehologram", "setsize" };

		for (String commandList : commands) {
			SubCommand subCommand = this.getAdminSubCommand(commandList);
			sender.sendMessage("* /island admin " + subCommand.getName() + " - " + subCommand.getInfo());
		}
	}

	public SubCommand getIslandSubCommand(String cmdName) {
		return this.getSubCommand(islandCommands, cmdName);
	}

	public SubCommand getAdminSubCommand(String cmdName) {
		return this.getSubCommand(adminCommands, cmdName);
	}

	public SubCommand getSubCommand(List<SubCommand> subCommands, String cmdName) {
		for (SubCommand command : subCommands) {
			if (command.getName().equalsIgnoreCase(cmdName))
				return command;

			for (String argList : command.getAliases())
				if (argList.equalsIgnoreCase(cmdName))
					return command;
		}

		return null;
	}
}
