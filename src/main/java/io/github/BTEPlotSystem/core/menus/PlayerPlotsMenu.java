package github.BTEPlotSystem.core.menus;

import github.BTEPlotSystem.core.plots.Plot;
import github.BTEPlotSystem.core.plots.PlotManager;
import github.BTEPlotSystem.utils.Builder;
import github.BTEPlotSystem.utils.ItemBuilder;
import github.BTEPlotSystem.utils.LoreBuilder;
import github.BTEPlotSystem.utils.Utils;
import github.BTEPlotSystem.utils.enums.Category;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.ipvp.canvas.Menu;
import org.ipvp.canvas.mask.BinaryMask;
import org.ipvp.canvas.mask.Mask;
import org.ipvp.canvas.type.ChestMenu;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PlayerPlotsMenu {
    private final Menu menu;
    private final Player player;
    private final Builder builder;

    public PlayerPlotsMenu(Builder builder) throws SQLException {
        this.player = builder.getPlayer();
        this.builder = builder;
        menu = ChestMenu.builder(6).title(builder.getName() + "'s Plots").build();

        Mask mask = BinaryMask.builder(menu)
                .item(new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 7).setName(" ").build())
                .pattern("111101111")
                .pattern("000000000")
                .pattern("000000000")
                .pattern("000000000")
                .pattern("000000000")
                .pattern("111101111")
                .build();
        mask.apply(menu);

        setMenuItems();

        menu.open(player);
    }

    private void setMenuItems() throws SQLException {
        menu.getSlot(4)
                .setItem(new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3)
                        .setName("§bs§l" + builder.getName()).setLore(new LoreBuilder()
                                .description("§6Points: §7"+builder.getScore(),"§6Completed builds: §7"+builder.getCompletedBuilds())
                                .build())
                        .build());
        menu.getSlot(49)
                .setItem(new ItemBuilder(Material.BARRIER, 1)
                        .setName("§c§lCLOSE")
                        .setLore(new LoreBuilder()
                                .description("§7Close the review menu")
                                .build())
                        .build());
        menu.getSlot(49).setClickHandler((clickPlayer, clickInformation) -> {
            clickPlayer.closeInventory();
        });

        //Set all plot items
        List<Plot> plotList = PlotManager.getPlots(builder);
        int plotDisplayCount;
        if (plotList.size()>36){
            plotDisplayCount = 36;
        } else {
            plotDisplayCount = plotList.size();
        }

        for (int i = 0; i < plotDisplayCount; i++) {
            switch (plotList.get(i).getStatus()){
                case unfinished:
                    menu.getSlot(i+9)
                            .setItem(new ItemBuilder(Material.WOOL,1, (byte) 1)
                                    .setName("§l§6#"+ plotList.get(i).getID() + " | " + plotList.get(i).getCity().getName()).setLore(getDescription(plotList.get(i)))
                                    .build());
                    break;
                case unreviewed:
                    menu.getSlot(i+9)
                            .setItem(new ItemBuilder(Material.MAP,1)
                                    .setName("§l§6#"+ plotList.get(i).getID() + " | " + plotList.get(i).getCity().getName()).setLore(getDescription(plotList.get(i)))
                                    .build());
                    break;
                case complete:
                    menu.getSlot(i+9)
                            .setItem(new ItemBuilder(Material.WOOL,1, (byte) 13)
                                    .setName("§l§6#"+ plotList.get(i).getID() + " | " + plotList.get(i).getCity().getName()).setLore(getDescription(plotList.get(i)))
                                    .build());
                    break;
            }
            menu.getSlot(i+9).setClickHandler((clickPlayer, clickInformation) -> {
                try {
                    new PlotActionsMenu(plotList.get(clickInformation.getClickedSlot().getIndex()-9),clickPlayer);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                    clickPlayer.sendMessage(Utils.getErrorMessageFormat("SQL Error!"));
                }
            });
        }
    }

    private List<String> getDescription(Plot plot) throws SQLException {
        List<String> strings = new ArrayList<>();
        if (plot.isReviewed()){
            strings.add("§bAccuracy: §7"+ plot.getReview().getRating(Category.ACCURACY));
            strings.add("§bBlock Palette: §7"+ plot.getReview().getRating(Category.ACCURACY));
            strings.add("§bDetailing: §7"+ plot.getReview().getRating(Category.ACCURACY));
            strings.add("§bTechnique: §7"+ plot.getReview().getRating(Category.ACCURACY));
            strings.add("§7----------");
            strings.add("§7" + plot.getReview().getFeedback());
            strings.add("§7----------");
        }
        strings.add("§6Score: §7" + plot.getScore());
        strings.add("§6§lStatus: §7§l" + plot.getStatus().name().substring(0, 1).toUpperCase() + plot.getStatus().name().substring(1));
        return strings;
    }
}