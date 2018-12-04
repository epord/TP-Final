public class AnimationBuilder {

    private StringBuilder sb = new StringBuilder();

    public AnimationBuilder(Grid grid) {
        sb.append(grid.getRoadWidth() + " " + grid.getRoadLength() + " " + grid.getCarsCount());
        sb.append("\n");
    }

    public void addCurrentFrame(Grid grid) {
        sb.append(grid.rasterize());
    }

    public String getString() {
        return sb.toString();
    }
}