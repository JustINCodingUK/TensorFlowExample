package io.github.JustINCoding.logic.recogmodel;

public class Recognition {
    public String label;
    public int confidence;

    public Recognition(String label, float confidence){
        label = this.label;
        confidence = this.confidence;
    }

    @Override
    public String toString(){
        return label+ "/" + Float.toString(confidence);
    }

    String outputString = String.format("%.1f%%", confidence*100.0f);
}
