package me.wallhacks.spark.gui.clickGui.panels.mainScreen.setting.settings;

import me.wallhacks.spark.gui.clickGui.panels.mainScreen.setting.GuiSettingPanel;
import me.wallhacks.spark.gui.panels.GuiPanelInputField;
import me.wallhacks.spark.util.GuiUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

import org.lwjgl.input.Mouse;
import me.wallhacks.spark.systems.setting.settings.ColorSetting;

import java.awt.*;

public class GuiColorSettingPanel extends GuiSettingPanel<ColorSetting> {
    ColorSlider red;
    ColorSlider green;
    ColorSlider blue;
    AlphaSlider alpha;
    HueSlider hue;
    ColorPicker picker;
    
    public GuiColorSettingPanel(ColorSetting setting) {
        super(setting);
        red = new ColorSlider("R", posX + 94, posY+4 + 12 , 70, 10);
        green = new ColorSlider("G", posX + 94, posY+4 + 24, 70, 10);
        blue = new ColorSlider("B", posX + 94, posY+4 + 36, 70, 10);
        alpha = new AlphaSlider("A", posX + 94, posY+4 + 48, 70, 10);
        hue = new HueSlider(posX + 72, posY+4 + 12, 10, 46);
        picker = new ColorPicker(posX, posY+4 + 12, 68, 46);
    }

    boolean isExtended = false;

    GuiPanelInputField guiPanelInputField = new GuiPanelInputField(0,0,0,0,0);


    final static ResourceLocation settingIcon = new ResourceLocation("textures/icons/arrowicon.png");



    @Override
    public void renderContent(int mouseX, int mouseY, float deltaTime) {
        super.renderContent(mouseX, mouseY, deltaTime);
        int xoffset = fontManager.drawString(getSetting().getName(), posX, posY+4, guiSettings.getContrastColor().getRGB());


        int size = 14;
        int y = posY+4+ fontManager.getTextHeight()/2-size/2;
        int x = posX + width - size;


		if(guiSettings.getArrowMode() == 2) {
			Gui.drawRect(x-size, y,x+size,y+size, guiSettings.getGuiSettingFieldColor().getRGB());
			Gui.drawRect(x+2 - size,y+2,x-2,y+size-2 ,getSetting().getRGB());

			if(guiSettings.getArrowMode() != 0)
				GuiUtil.drawCompleteImageRotated(x+4,y + 4, 6, 6, isExtended ? 90 : 0,settingIcon, guiSettings.getContrastColor());

		}
		else {
			Gui.drawRect(x,y,x+size,y+size, guiSettings.getGuiSettingFieldColor().getRGB());
			Gui.drawRect(x+1,y+1,x+size-1,y+size-1,getSetting().getRGB());

			if(guiSettings.getArrowMode() != 0)
				GuiUtil.drawCompleteImageRotated(xoffset+4,posY+4+fontManager.getTextHeight()/2-4/2,4,4,isExtended ? 90 : 0,settingIcon, guiSettings.getContrastColor());

		}

        if(isExtended)
            this.drawPicker(getSetting(),  
            		mouseX, mouseY);
        height = isExtended ? 85 : fontManager.getTextHeight()+6;
    }



    public void drawPicker(ColorSetting subColor, int mouseX, int mouseY) {
    	red.updatePositionAndSize(posX + 94, posY+4 + 12, posX+width-(posX + 94), 10);
    	green.updatePositionAndSize(posX + 94, posY+4 + 24, posX+width-(posX + 94), 10);
    	blue.updatePositionAndSize(posX + 94, posY+4 + 36, posX+width-(posX + 94), 10);
    	alpha.updatePositionAndSize(posX + 94, posY+4 + 48, posX+width-(posX + 94), 10);
    	hue.updatePositionAndSize(posX + 72, posY+4 + 12, 10, 46);
    	picker.updatePositionAndSize(posX, posY+4 + 12, 68, 46);
        int r = getSetting().getColor().getRed();
        int g = getSetting().getColor().getGreen();
        int b = getSetting().getColor().getBlue();
        int a = getSetting().getColor().getAlpha();
        float[] hsb = Color.RGBtoHSB(r, g, b, null);
        if(!Mouse.isButtonDown(0)) {
            red.mouseReleased();
            blue.mouseReleased();
            green.mouseReleased();
            alpha.mouseReleased();
            hue.mouseReleased();
            picker.mouseReleased();
        }

        if (hue.picking) {
            hsb[0] = hue.pick(mouseY, hsb[0]);
            getSetting().setColor(new Color(Color.HSBtoRGB(hsb[0], hsb[1], hsb[2])));
        }
        
        if (picker.picking) {
            hsb[1] = picker.pickSaturation(mouseX, hsb[1]);
            hsb[2] = picker.pickBrigthness(mouseY, hsb[2]);
            getSetting().setColor(new Color(Color.HSBtoRGB(hsb[0], hsb[1], hsb[2])));
        }

        if (red.picking || blue.picking || green.picking || alpha.picking) {
            int r1 = red.pick(mouseX, r);
            int g1 = green.pick(mouseX, g);
            int b1 = blue.pick(mouseX, b);
            int a1 = alpha.pick(mouseX, 255-a);
            getSetting().setColor(new Color(r1 / 255f, g1 / 255f, b1 / 255f, 1 - a1 / 255f));
        }

        guiPanelInputField.setPositionAndSize(posX,posY+4 + 61,65,12);
        guiPanelInputField.renderContent(mouseX,mouseY,0);

        if(guiPanelInputField.isFocused())
            try {
                String s = guiPanelInputField.getText();

                Color c = Color.decode(s);
                if(!getSetting().equals(c))
                    getSetting().setColor(c);

            }
            catch (NumberFormatException e)
            {

            }
        else
            guiPanelInputField.setText(String.format("#%06x", getSetting().getColor().getRGB() & 0xFFFFFF));


        fontManager.drawString("Rainbow", posX + Math.min(90,width-65), posY+4 + 64, guiSettings.getContrastColor().getRGB());

        String s = getSetting().getRainbow().getName();

        int length = fontManager.getTextWidth(s) + 4;


        Gui.drawRect(posX + width - length, posY+4 + 61, posX + width, posY+4 + 73, guiSettings.getGuiSettingFieldColor().getRGB());

        fontManager.drawString(s, posX + width - length + 2, posY+4 + 64, guiSettings.getContrastColor().getRGB());
        
        picker.updatePickerLocation(hsb[1], hsb[2]);
        picker.drawColorPicker(r, b, g);
        
        hue.updateSliderLocation(Color.RGBtoHSB(r, g, b, null)[0]);
        hue.drawSlider();
        
        if(getSetting().isAllowChangeAlpha()) {
        	alpha.updateSliderLocation(255-a);
            alpha.drawSlider(r / 255f, g / 255f, b / 255f, a);
        }
        red.updateSliderLocation(r);
        green.updateSliderLocation(g);
        blue.updateSliderLocation(b);
        red.drawSlider(0, 255, g, g, b, b);
        green.drawSlider(r, r, 0, 255, b, b);
        blue.drawSlider(r, r, g, g, 0, 255);
    }

    @Override
    public void onClickDown(int MouseButton, int mouseX, int mouseY) {
        int size = 11;
        int y = posY+4+ fontManager.getTextHeight()/2-size/2;
        int x = posX + width - size;



        if (mouseOver(posX, y,posX+width,y+size, mouseX, mouseY))
            isExtended = !isExtended;
        if(isExtended) {
            String s = getSetting().getRainbow().getName();
            int length = fontManager.getTextWidth(s) + 4;
            if (mouseOver(posX + width - length, posY+4 + 61, posX + width, posY+4 + 73, mouseX, mouseY) && MouseButton == 0) {
                getSetting().setRainbow(getSetting().getRainbow().next());
            }
        }
        if(MouseButton == 0) {
        	if(!red.picking && !blue.picking && !green.picking && !alpha.picking && !hue.picking && !picker.picking) {
        		red.mouseClicked(mouseX, mouseY);
            	blue.mouseClicked(mouseX, mouseY);
            	green.mouseClicked(mouseX, mouseY);
            	alpha.mouseClicked(mouseX, mouseY);
            	hue.mouseClicked(mouseX, mouseY);
            	picker.mouseClicked(mouseX, mouseY);
	        }
        }
    }

    public static boolean mouseOver(int minX, int minY, int maxX, int maxY, int mX, int mY) {
        return mX >= minX && mY >= minY && mX <= maxX && mY <= maxY;
    }
    
    public abstract class Slider<T> {
    	String displayString;
    	public boolean picking;
    	int x;
    	int y;
    	int width;
    	int height;
    	int sliderOffset;
    	
		public Slider(String diplayString, int x, int y, int width, int height) {
			this.displayString = diplayString;
			this.picking = false;
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
			this.sliderOffset = 0;
		}
    	
		public abstract void updateSliderLocation(T color);
    	
    	public abstract T pick(int mouseX, T original);
		
		public void mouseReleased() {
			picking = false;
		}
		
    	public void mouseClicked(int mouseX, int mouseY) {
    		if(mouseOver(mouseX, mouseY)) {
    			picking = true;
    		} else {  
    			picking = false;
    		}
    	}
    	
    	public void updatePositionAndSize(int x, int y, int width, int height) {
    		this.x = x;
    		this.y = y;
    		this.width = width;
    		this.height = height;
    	}
    	
    	public boolean mouseOver(int mouseX, int mouseY) {
            return mouseX >= x && mouseY >= y && mouseX <= x + width && mouseY <= y + height;
        }

    }
    
    public class RGBASlider extends Slider<Integer> {
    	
    	public RGBASlider(String diplayString, int x, int y, int width, int height) {
			super(diplayString, x, y, width, height);
		}

		public void updateSliderLocation(Integer color) {
    		sliderOffset = (int) (width * color / 255f);
    	}
		
		public Integer pick(int mouseX, Integer original) {
    		if(picking) {
	    		int mX = mouseX - x;
	            float percent = mX / (float) width;
	            percent = MathHelper.clamp(percent, 0, 1);
	            return (int) (255 * percent);
    		}
    		return original;
    	}
    }
    
    public class ColorSlider extends RGBASlider {

		public ColorSlider(String diplayString, int x, int y, int width, int height) {
			super(diplayString, x, y, width, height);
		}

		public void drawSlider(int r1, int r2, int g1, int g2, int b1, int b2) {
			fontManager.drawString(displayString, x - 9, y + 3, guiSettings.getContrastColor().getRGB());
            GuiUtil.drawGradientRect(x, y, x + width, y + height, new Color(r1, g1, b1).getRGB(),
            													new Color(r1, g1, b1).getRGB(), 
											            		new Color(r2, g2, b2).getRGB(), 
											            		new Color(r2, g2, b2).getRGB(), 0);
            Gui.drawRect(x + sliderOffset - 1, y, x + sliderOffset + 1, y + height, new Color(0x252525).getRGB());
		}
    }
    
    public class AlphaSlider extends RGBASlider {

		public AlphaSlider(String diplayString, int x, int y, int width, int height) {
			super(diplayString, x, y, width, height);
		}
    	
		public void drawSlider(float r, float g, float b, int alpha) {
			boolean left = true;
	        int checkerBoardSquareSize = height / 2;
	        fontManager.drawString("A", x - 9, y + 3, guiSettings.getContrastColor().getRGB());
	        for (int squareIndex = -checkerBoardSquareSize; squareIndex < width; squareIndex += checkerBoardSquareSize) {
	            if (!left) {
					int minX = x + squareIndex;
					int maxX = Math.min(x + width, x + squareIndex + checkerBoardSquareSize);
	                Gui.drawRect(minX, y, maxX, y + height, new Color(0x2A2A2A).getRGB());
	                Gui.drawRect(minX, y + checkerBoardSquareSize, maxX, y + height, new Color(0x595959).getRGB());

	                if (squareIndex < width - checkerBoardSquareSize) {
	                    minX = x + squareIndex + checkerBoardSquareSize;
	                    maxX = Math.min(x + width, x + squareIndex + checkerBoardSquareSize * 2);
	                    Gui.drawRect(minX, y, maxX, y + height, new Color(0x595959).getRGB());
	                    Gui.drawRect(minX, y + checkerBoardSquareSize, maxX, y + height, new Color(0x2A2A2A).getRGB());
	                }
	            }
	            left = !left;
	        }

	        GuiUtil.drawLeftGradientRect(x, y, x + width, y + height, new Color(r, g, b, 1).getRGB(), 0);
	        Gui.drawRect(x + sliderOffset - 1, y, x + sliderOffset + 1, y + height, new Color(0x252525).getRGB());
		}
    }
    
    public class HueSlider extends Slider<Float> {

		public HueSlider(int x, int y, int width, int height) {
			super("", x, y, width, height);
		}
    	
		@Override
		public void updateSliderLocation(Float hue) {
			sliderOffset = (int) (height * hue);
		}

		@Override
		public Float pick(int mouseY, Float original) {
			if(picking) {
	    		int mY = mouseY - y;
	            float hue = mY / (float) height;
	            hue = MathHelper.clamp(hue, 0f, 0.99722222221f);
	            return hue;
    		}
    		return original;
		}


		public void drawSlider() {
			int step = 0;
	        Gui.drawRect(x, y, x + width, y + 4, new Color(0xFFFF0000).getRGB());
			y+=4;
	        for (int colorIndex = 0; colorIndex < 6; colorIndex++) {
	            int previousStep = Color.HSBtoRGB((float) step / 6, 1.0f, 1.0f);
	            int nextStep = Color.HSBtoRGB((float) (step + 1) / 6, 1.0f, 1.0f);
	            drawGradientRect(x, y + step * (height / 6), x + width, y + (step + 1) * (height / 6), previousStep, nextStep);
	            step++;
	        }
			y-=4;
	        Gui.drawRect(x, y + sliderOffset - 1, x + width, y + sliderOffset + 1, new Color(0x252525).getRGB());
		}
    }
    
    public class ColorPicker {
    	int x;
    	int y;
    	int pickerX;
    	int pickerY;
    	int width;
    	int height;
    	boolean picking;
    	
		public ColorPicker(int x, int y, int width, int height) {
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
			this.pickerX = 0;
			this.pickerY = 0;
			picking = false;
		}
		
		public void drawColorPicker(int r, int b, int g) {
			float selectedRed = r / 255.0f;
		    float selectedGreen = g / 255.0f;
		    float selectedBlue = b / 255.0f;
	
		    GuiUtil.drawPickerBase(x, y, width, height, selectedRed, selectedGreen, selectedBlue, 255);
		    Gui.drawRect(x + pickerX - 2, y + pickerY - 2, x + pickerX + 2, y + pickerY + 2, new Color(0x252525).getRGB());
		}
		
		public float pickSaturation(int mouseX, float original) {
			if(picking) {
	    		int mX = mouseX - x;
	            float saturation = mX / (float) width;
	            saturation = MathHelper.clamp(saturation, 0f, 0.99f);
	            return saturation;
    		}
    		return original;
		}
		
		public float pickBrigthness(int mouseY, float original) {
			if(picking) {
	    		int mY = mouseY - y;
	            float brightness = mY / (float) height;
	            brightness = MathHelper.clamp(brightness, 0f, 0.99f);
	            return 1-brightness;
    		}
    		return original;
		}
		
		public void updatePickerLocation(float saturation, float brightness) {
			pickerX = (int) (width * saturation);
			pickerY = (int) (height * (1-brightness));
		}
		
		public void mouseReleased() {
			picking = false;
		}
		
    	public void mouseClicked(int mouseX, int mouseY) {
    		if(mouseOver(mouseX, mouseY)) {
    			picking = true;
    		} else {  
    			picking = false;
    		}
    	}
    	
    	public void updatePositionAndSize(int x, int y, int width, int height) {
    		this.x = x;
    		this.y = y;
    		this.width = width;
    		this.height = height;
    	}
    	
    	public boolean mouseOver(int mouseX, int mouseY) {
            return mouseX >= x && mouseY >= y && mouseX <= x + width && mouseY <= y + height;
        }
    }
}
