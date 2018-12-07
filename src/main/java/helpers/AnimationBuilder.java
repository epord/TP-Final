public class AnimationBuilder {

    private StringBuilder sb = new StringBuilder();

    public AnimationBuilder(City city) {
        sb.append(city.getCityWidth() + " " + city.getCityHeight() + " " + city.getCarsCount());
        sb.append("\n");
    }

    public void addCurrentFrame(City city) {
        sb.append(city.rasterize());
    }

    public String getString() {
        return sb.toString();
    }
}