package com.zyin.zyinhud.mods;

import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.math.RayTraceResult;

import com.zyin.zyinhud.util.Localization;

/**
 * The Distance Measurer calculates the distance from the player to whatever the player's
 * crosshairs is looking at.
 */
public class DistanceMeasurer extends ZyinHUDModBase
{
	/** Enables/Disables this Mod */
	public static boolean Enabled;

    /**
     * Toggles this Mod on or off
     * @return The state the Mod was changed to
     */
    public static boolean ToggleEnabled()
    {
    	return Enabled = !Enabled;
    }

	/** The current mode for this mod */
	public static Modes Mode;

	/** The enum for the different types of Modes this mod can have */
    public static enum Modes
    {
        OFF(Localization.get("distancemeasurer.mode.off")),
        SIMPLE(Localization.get("distancemeasurer.mode.simple")),
        COORDINATE(Localization.get("distancemeasurer.mode.complex"));

        private String friendlyName;

        private Modes(String friendlyName)
        {
        	this.friendlyName = friendlyName;
        }

        /**
         * Sets the next availble mode for this mod
         */
        public static Modes ToggleMode()
        {
        	return Mode = Mode.ordinal() < Modes.values().length - 1 ? Modes.values()[Mode.ordinal() + 1] : Modes.values()[0];
        }

        /**
         * Gets the mode based on its internal name as written in the enum declaration
         * @param modeName
         * @return
         */
        public static Modes GetMode(String modeName)
        {
        	try {return Modes.valueOf(modeName);}
        	catch (IllegalArgumentException e) {return values()[0];}
        }

        public String GetFriendlyName()
        {
        	return friendlyName;
        }
    }


    public static void RenderOntoHUD()
    {
        //if the player is in the world
        //and not looking at a menu
        //and F3 not pressed
        if (DistanceMeasurer.Enabled && Mode != Modes.OFF &&
                (mc.inGameHasFocus || (mc.currentScreen != null && (mc.currentScreen instanceof GuiChat))) &&
                !mc.gameSettings.showDebugInfo)
        {
        	String distanceString = CalculateDistanceString();

            ScaledResolution res = new ScaledResolution(mc);
            int width = res.getScaledWidth();
            int height = res.getScaledHeight();
            int distanceStringWidth = mc.fontRendererObj.getStringWidth(distanceString);

            mc.fontRendererObj.drawStringWithShadow(distanceString, width/2 - distanceStringWidth/2, height/2 - 10, 0xffffff);
        }
    }



    /**
     * Calculates the distance of the block the player is pointing at
     * @return the distance to a block if Distance Measurer is enabled, otherwise "".
     */
    protected static String CalculateDistanceString()
    {
    	RayTraceResult objectMouseOver = mc.thePlayer.rayTrace(300, 1);

        if (objectMouseOver != null && objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK)
        {
            if (Mode == Modes.SIMPLE)
            {
            	double playerX = mc.thePlayer.posX;
                double playerY = mc.thePlayer.posY + mc.thePlayer.getEyeHeight();
                double playerZ = mc.thePlayer.posZ;

                double blockX = objectMouseOver.hitVec.xCoord;
                double blockY = objectMouseOver.hitVec.yCoord;
                double blockZ = objectMouseOver.hitVec.zCoord;

                double deltaX;
                double deltaY;
                double deltaZ;

                if(playerX < blockX)
                	deltaX = blockX - playerX;
                else if(playerX > blockX + 0.5)
                	deltaX = playerX - blockX;
                else
                	deltaX = playerX - blockX;

                if(playerY < blockY)
                	deltaY = blockY - playerY;
                else if(playerY > blockY)
                	deltaY = playerY - blockY;
                else
                	deltaY = playerY - blockY;

                if(playerZ < blockZ)
                	deltaZ = blockZ - playerZ;
                else if(playerZ > blockZ)
                	deltaZ = playerZ - blockZ;
                else
                	deltaZ = playerZ - blockZ;

            	double farthestHorizontalDistance = Math.max(Math.abs(deltaX), Math.abs(deltaZ));
                double farthestDistance = Math.max(Math.abs(deltaY), farthestHorizontalDistance);

                return TextFormatting.GOLD + "[" + String.format("%1$,.1f", farthestDistance) + "]";
            }
            else if (Mode == Modes.COORDINATE)
            {
            	BlockPos pos = objectMouseOver.getBlockPos();

                return TextFormatting.GOLD + "[" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + "]";
            }
            else
            {
            	return TextFormatting.GOLD + "[???]";
            }
        }
        else
        {
        	return TextFormatting.GOLD + "["+Localization.get("distancemeasurer.far")+"]";
        }
    }
}
