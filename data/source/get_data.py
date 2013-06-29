import sys
import os
#call get_data.py network name like get_data.py SF
network = str(sys.argv[1])
bus_lignes = {}
	
#create dir if not exists
directory =network+"/transformed/"
if not os.path.exists(directory):
	os.makedirs(directory)

#create node file
if os.path.exists(directory+"stops.txt"):
	os.remove(directory+"stops.txt")
f=open(directory+"stops.txt", "w+")
s=open(network+"/stops.txt")
s.readline()
for line in s:
	f.write(line.split(",")[0]+";"+line.split(",")[3]+";"+line.split(",")[4]+";"+"\n")
s.close()
f.close()

#create lignes file
s=open(network+'/stop_times.txt')
s.readline()
for line in s.readlines():
	#line.split(',')[3] is the stop_id
	stop_i={line.split(',')[3]:{'time':line.split(',')[1],'position_in_bus_ligne':line.split(',')[4]}}
	temp=bus_lignes.get(line.split(',')[0],[])
	#only keep the first element for every stop-> don't work for a round trip
	if line.split(',')[3] in temp:
		previous_stop = temp[line.split(',')[3]]
		if line.split(',')[4] < previous_stop['position_in_bus_ligne']:
			temp[line.split(',')[3]]=stop_i
	else:
		temp.append(stop_i)
	bus_lignes[line.split(',')[0]]=temp



if os.path.exists(directory+"bus_lignes.txt"):	
	os.remove(directory+"bus_lignes.txt")
f = open(directory+"bus_lignes.txt", "w+")
i=0
for ligne_id in bus_lignes:
	f.write(str(i)+";")
	i+=1
	for stop in bus_lignes[ligne_id]:
		for stop_id in stop:
			f.write(stop_id+";"+stop[stop_id]['position_in_bus_ligne']+";"+stop[stop_id]['time']+";")
	f.write("\n")
f.close()