publicTransports
================
fewLoopsSOTA(int budget, int idNodeSource, int idNodeDestination, Graph g) execute the SOTA resolution with the few loops technique and use basic convolution
zeroDelayOptOrderingSOTA do the same with the zero delay technique.
Note that the zero delay doesn't give a correct answer and can only be used to get the expected runtime.

The data files are processed in python to transform the stop_time in line itinerary
Then, in Java, we adapte those data to the structure used and create a new file for faster further computations.

The update rules can be changed in SOTA.SOTA update(int idNode, int startDiscOfTheUpdate, int endDiscOfTheUpdate)

The results are strange and it seems that the data are not correctly loaded or innacurate. Nevertheless, it could be used to get the computation times.
