package me.wallhacks.spark.gui.dvdpanels;

public class GuiSlider extends GuiPanelBase {

    public GuiSlider(double min,double max) {
        super(0, 0, 0, 5);

        this.min = min;
        this.max = max;
    }

    double max;
    double min;

    double value;

    int sliderHeight = 2;
    int handleSize = 4;





    public double getValue() {
        return value;
    }
    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public void renderContent(int MouseX, int MouseY, float deltaTime) {
        super.renderContent(MouseX, MouseY, deltaTime);



        double valueClamp = Math.max(min,Math.min(max,value));

        double fill = (1.0 / (max-min)) * (valueClamp-min);

        int x = (int)(width*fill);

        int y = height/2  - sliderHeight/2;
        drawRect(posX,posY+y,posX+x,posY+y+sliderHeight, guiSettings.getGuiFilledBackgroundSliderColor().getRGB());
        drawRect(posX+x,posY+y,posX+width,posY+y+sliderHeight, guiSettings.getGuiBackgroundSliderColor().getRGB());


        y = height/2 - handleSize/2;

        drawRect(posX+x-handleSize/2,posY+y,posX+x+handleSize/2,posY+y+handleSize, guiSettings.getGuiHandelSliderColor().getRGB());



    }

    @Override
    public void onClick(int MouseButton, int MouseX, int MouseY) {
        super.onClickDown(MouseButton, MouseX, MouseY);

        int getRelativePos = MouseX - posX;

        double fill = (1.0/width)*getRelativePos;

        fill = Math.max(0,Math.min(1,fill));

        setValue((min+(fill*(max-min))));
    }
}
