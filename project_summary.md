# 12Hours 

## Authors
- Michał Tajchert https://github.com/tajchert

## Description
Modify wall clock to make it highly useful and beautiful at the same time. Using RGB LED diodes build in a clock frame, highlight selected pie pieces of a clock face. Using that highlighting and all RGB colors you can visualize events from your calendar in next 12 hours, or show sunrise and sunset time by showing in corresponding place yellow (sunrise), blue (sunset) colors. There is a ton of potential uses and due to that controlling app (Android and iOS) will be able to use add-ons created by users to show own data on a clock, or just a fancy colorful animations.

## TL;DR
Using LEDs show on a clock face some data - like events from calendar in next 12 hours from now. Simple as that.

## Details
Data is provided by user phone, over Bluetooth (for now ver. 2.1 but in final version it would be 4.0).
Owner uses app (Android and iOS) to select data source, customize if needed and confirm. App would be modular so anybody can write his or her script to get some interesting data (for example sea tides), and show it on the clock.
Clock face would be 3D printed to achieve some awesome designs and make it even more beautiful.

## Links to External Libraries

Used to control RGB diodes strip:
[Adafruit-NeoPixel](https://github.com/adafruit/Adafruit_NeoPixel "Adafruit-NeoPixel")

