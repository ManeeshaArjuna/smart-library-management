package edu.esu.kandy.slms.decorator;

public class SpecialEditionBookDecorator extends BookViewDecorator {

    public SpecialEditionBookDecorator(BookView inner) {
        super(inner);
    }

    @Override
    public String getDisplayTitle() {
        return super.getDisplayTitle() + " (Special Edition)";
    }
}
