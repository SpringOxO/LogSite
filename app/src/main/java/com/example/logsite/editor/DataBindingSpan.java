package com.example.logsite.editor;

public interface DataBindingSpan<T> {
    CharSequence spannedText ();
    T bindingData ();
}
