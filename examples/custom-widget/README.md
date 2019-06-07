This example shows how to add your own custom widget to the Palette.


## Prerequisites

* Successfully built yamcs-studio from source.


## Build

    mvn clean install
    
The jar file is generated under `target`.


## Deploy

For a quick result:

1. Drop the jar in the `plugins` folder of an existing Yamcs Studio installation.
2. Edit `Yamcs Studio.ini` changing `-Dorg.eclipse.update.reconcile=false` to `-Dorg.eclipse.update.reconcile=true`. 
3. Start or restart Yamcs Studio.

The more elaborate alternative is to make a custom RCP product and include your plugin(s) in that product at build time. There is no further documentation available for this approach, although you can take some inspiration from how the base Yamcs Studio product is built. 


## Test

In the Display Builder window, under `Monitors` you should see a new widget `My Widget`.
