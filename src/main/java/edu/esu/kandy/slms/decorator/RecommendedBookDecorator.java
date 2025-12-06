package edu.esu.kandy.slms.decorator;

public class RecommendedBookDecorator extends BookViewDecorator {

    public RecommendedBookDecorator(BookView inner) {
        super(inner);
    }

    @Override
    public String getDisplayTitle() {
        return "\uD83D\uDC4D Recommended: " + super.getDisplayTitle();
    }
}
