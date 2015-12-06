README

For GetThisOrThat Android Studio Project
App to share clothes pictures from stores and get feedback on whether to buy it or not

Issue:
attempt to add floating action bar to full screen activity did not work
floating action bar widget could not be resolved even in import statement

Solution:
in build.gradle(Module App) added line to dependencies:

	compile 'com.android.support:design:23.1.0'

following line did not work:

	compile 'com.getbase:floatingactionbutton:1.9.1'