package edu.esu.kandy.slms.decorator;

public class FeaturedBookDecorator extends BookViewDecorator {

    public FeaturedBookDecorator(BookView inner) {
        super(inner);
    }

    @Override
    public String getDisplayTitle() {
        return "\uD83D\uDC4F Featured: " + super.getDisplayTitle();
    }
}
