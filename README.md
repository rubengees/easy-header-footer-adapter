# EasyHeaderFooterAdapter [![Release](https://jitpack.io/v/rubengees/EasyHeaderFooterAdapter.svg)](https://jitpack.io/#rubengees/EasyHeaderFooterAdapter) ![API](https://img.shields.io/badge/API-9%2B-blue.svg) [![CircleCI](https://circleci.com/gh/rubengees/EasyHeaderFooterAdapter.svg?style=shield)](https://circleci.com/gh/rubengees/EasyHeaderFooterAdapter)

Add a header and/or footer to your `RecyclerView` - the easy way.

![Sample](art/sample.gif)

You can download the latest sample app [here](https://github.com/rubengees/EasyHeaderFooterAdapter/releases/download/1.0.0/sample-release.apk).

### Features

- Completely hassle-free. You don't have to change your existing implementation.
- Support for `LinearLayoutManager`, `GridLayoutManager` and `StaggeredGridLayoutManager`.
- `LayoutParams` of your `View`s are honoured.
- The `SpanSizeLookup` of your `GridLayoutManager` continues to work without adjustments.
- Support for stable ids.

### Include in your Project

Add this to your root `build.gradle` (usually in the root of your project):

```groovy
repositories {
    maven { url "https://jitpack.io" }
}
```

And this to your module `build.gradle` (usually in the `app` directory):

```groovy
dependencies {
    compile 'com.github.rubengees:EasyHeaderFooterAdapter:1.0.0@aar'
}
```

If that doesn't work, look if there is a new version and the Readme was not updated yet.

### Usage

There is almost zero configuration if you already have an adapter.  
You wrap your adapter in the `EasyHeaderFooterAdapter` and set it to the `RecyclerView` like this:

```java
YourAdapter adapter = new YourAdapter();
EasyHeaderFooterAdapter easyHeaderFooterAdapter = 
                new EasyHeaderFooterAdapter(adapter);

//Always set the LayoutManager BEFORE the adapter.
recyclerView.setLayoutManager(new SomeLayoutManager());
recyclerView.setAdapter(easyHeaderFooterAdapter);
```

You can then set a `header` or `footer` through the `setHeader` and `setFooter` methods:

```java
headerFooterAdapter.setHeader(anyView);
headerFooterAdapter.setFooter(anotherView);
```

Those can be removed with the `removeHeader` and `removeFooter` methods:

```java
headerFooterAdapter.removeHeader();
headerFooterAdapter.removeFooter();
```

And that's it! Easy right?

There are a view things to look out for though.

##### Positions

If you are using the positions returned by the `ViewHolder`s `getAdapterPosition` method, you have to calculate the real position in your adapter. The method `getRealPosition` does exactly that:

```java
adapter.setCallback(new MainAdapter.MainAdapterCallback() {
    @Override
    public void onItemClick(int position) {
        int positionInYourDataSet = headerFooterAdapter.getRealPosition(position);

        // Do whatever you wanted to do with it.
    }
});
```

You can find more info on that in the [sample](sample/src/main/java/com/rubengees/easyheaderfooteradaptersample/MainActivity.java#L58).

##### ViewTypes and Ids

The following ViewTypes and IDs are used internally, so don't use them yourself:

```java
TYPE_HEADER = Integer.MIN_VALUE;
TYPE_FOOTER = Integer.MIN_VALUE + 1;
ID_HEADER = Long.MIN_VALUE;
ID_FOOTER = Long.MIN_VALUE + 1;
```

##### Changing the `LayoutManager` at runtime

As the `EasyHeaderFooterAdapter` needs to configure your `LayoutManager`, you have to reset the adapter to the `RecyclerView` if you want to change the `LayoutManager`:

```java
recycler.setLayoutManager(new YourNewLayoutManager());

// Remember, always set if after the LayoutManager
recycler.setAdapter(headerFooterAdapter);
```

### Further reading

The sample features almost all use cases. Have a look [here](sample/src/main/java/com/rubengees/easyheaderfooteradaptersample).  
You can find the JavaDoc [here](https://jitpack.io/com/github/rubengees/EasyHeaderFooterAdapter/1.0.0/javadoc/).

### Metrics

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/f4d420e243c145c88ceac22b86f819eb)](https://www.codacy.com/app/geesruben/EasyHeaderFooterAdapter?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=rubengees/EasyHeaderFooterAdapter&amp;utm_campaign=Badge_Grade)  

### Contributions and contributors

A guide for contribution can be found [here](.github/CONTRIBUTING.md).
