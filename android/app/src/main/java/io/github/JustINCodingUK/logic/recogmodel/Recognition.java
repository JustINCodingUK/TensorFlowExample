package io.github.JustINCodingUK.logic.recogmodel;

public class Recognition {
    private String label;
    private int confidence;

    public Recognition(String label, float confidence){
        label = this.label;
        confidence = this.confidence;
    }

    public int getConfidence() {
        return confidence;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        label = this.label;
    }

    private String outputString = label + " / " + confidence;

    public String getOutputString() {
        return outputString;
    }

}
