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


##Prototype
Events from my calendar taken from my phone. For example there is a event from 9 to 12 o'clock, as well as from 12 to 1 o'clock. With 3D printed model colors won't mix so much as there are separators on clock face.
![Prototype Final](../project_images/prot_fin_3.jpg?raw=true "Prototype Final")

"Rainbow" effect as a demo how it looks like with whole face light up.
![Prototype Final](../project_images/prot_fin_2.jpg?raw=true "Prototype Final")

Narrow angle photo to show LEDs, normally they are hidden from user (also there is a glass in front of clock face).
![Prototype Final](../project_images/prot_fin_1.jpg?raw=true "Prototype Final")

Sample event on clock face. Colors are randomly chosen from a palate that can be changed, but they can be as well taken from Google Calendar.
![Prototype Final](../project_images/prot_fin_4.jpg?raw=true "Prototype Final")