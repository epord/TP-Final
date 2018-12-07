public class AnimationBuilder {

    private StringBuilder sb = new StringBuilder();

    public AnimationBuilder(Avenue avenue) {
        sb.append(avenue.getRoadWidth() + " " + avenue.getRoadLength() + " " + avenue.getCarsCount());
        sb.append("\n");
    }

    public void addCurrentFrame(Avenue avenue) {
        sb.append(avenue.rasterize());
    }

    public String getString() {
        return sb.toString();
    }
}