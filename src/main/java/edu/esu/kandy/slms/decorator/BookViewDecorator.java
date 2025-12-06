package edu.esu.kandy.slms.decorator;

public abstract class BookViewDecorator implements BookView {

    protected final BookView inner;

    protected BookViewDecorator(BookView inner) {
        this.inner = inner;
    }

    @Override
    public String getDisplayTitle() {
        return inner.getDisplayTitle();
    }
}
