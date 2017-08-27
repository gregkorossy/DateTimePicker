# Material DatePicker and TimePicker dialogs from the Android framework package

This project is ported from the [Android framework](https://android.googlesource.com/platform/frameworks/base/). In order to make the pickers material themed on older devices (pre-Lollipop), the appcompat library is used for base theming and vector drawable handling. The widgets _always_ use the custom layouts, this is a complete replacement for the framework pickers. This also means that they have the same look-and-feel on all API levels.

[ ![Download](https://api.bintray.com/packages/gericop/maven/com.takisoft.fix%3Adatetimepicker/images/download.svg) ](https://bintray.com/gericop/maven/com.takisoft.fix%3Adatetimepicker/_latestVersion)

## Setup
**Add** this single line to your gradle file:
```gradle
compile 'com.takisoft.fix:datetimepicker:1.0.0'
```
And **use** one of the `Theme.AppCompat` themes (_or a custom one based on that_) as your `Activity`'s theme.

Now you can use the following material themed picker dialogs:

- **`DatePickerDialog`** (from `com.takisoft.datetimepicker.DatePickerDialog`)
- **`TimePickerDialog`** (from `com.takisoft.datetimepicker.TimePickerDialog`)

Additionally you could _technically_ use `DatePicker` and `TimePicker` from the `com.takisoft.datetimepicker.widget` package inside a layout as an inline view, however, they do not support scrolling containers, so it's recommended to stick with the dialogs instead.

---

## Customization

You can override the following attributes in your theme (check out the default styles for details):

Attribute | Description | Default (dark) / (light)
-|-|-
`datePickerStyle` | The `DatePicker`'s style | `Widget.Material.DatePicker` / `Widget.Material.Light.DatePicker`
`datePickerDialogTheme` | The `DatePickerDialog`'s style | `ThemeOverlay.Material.Dialog.DatePicker`
`timePickerStyle` | The `TimePicker`'s style | `Widget.Material.TimePicker` / `Widget.Material.Light.TimePicker`
`timePickerDialogTheme` | The `TimePickerDialog`'s style | `ThemeOverlay.Material.Dialog.TimePicker`
`numberPickerStyle` | The `NumberPicker`'s style which is going to be used if there's not enough room on the screen for the normal pickers | `Widget.Material.NumberPicker` / `Widget.Material.Light.NumberPicker`
`calendarViewStyle` | The `CalendarView`'s style | `Widget.Material.CalendarView` / `Widget.Material.Light.CalendarView`

> Watch out **not to include** the `android:` prefix before the attribute names if you want to override them as it will not work. These attributes have been (re)defined for the project in order to be able to access the internal details, which means that they do not belong to the `android` namespace anymore.