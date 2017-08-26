# DatePicker and TimePicker from the Android framework package

This project is ported from the [Android framework](https://android.googlesource.com/platform/frameworks/base/). In order to make the pickers material themed on older devices (pre-Lollipop), the appcompat library is used for base theming and vector drawable handling. The widgets _always_ use the custom layouts, this is a complete replacement for the framework pickers. This also means that they have the same look-and-feel on all API levels.

[ ![Download](https://api.bintray.com/packages/gericop/maven/com.takisoft.fix%3Adatetimepicker/images/download.svg) ](https://bintray.com/gericop/maven/com.takisoft.fix%3Adatetimepicker/_latestVersion)

## Setup
**Add** this single line to your gradle file:
```gradle
compile 'com.takisoft.fix:datetimepicker:1.0.0'
```

Now you can use the following material themed picker dialogs:

- **`DatePickerDialog`** (from `com.takisoft.datetimepicker.DatePickerDialog`)
- **`TimePickerDialog`** (from `com.takisoft.datetimepicker.TimePickerDialog`)

Additionally you could _technically_ use `DatePicker` and `TimePicker` from the `com.takisoft.datetimepicker.widget` package, however, they do not support scrolling containers, so it's recommended to stick with the dialogs instead.
