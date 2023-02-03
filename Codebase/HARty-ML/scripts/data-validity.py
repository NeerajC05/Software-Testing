import os
import sys

#Users are considered valid if they have only 1 instance of each file type in their directory. 

sensor_type = str(sys.argv[1])
src_path = os.path.join(r'../processed-data/')
file_types = ["_Sitting bent backward_", "_Sitting bent forward_","_Sitting_","_Standing_", "_Walking at normal speed_", "_Climbing stairs_", "_Descending stairs_", "_Desk work_", "_Movement_", "_Lying down on back_", "_Lying down on stomach_", "_Lying down left_", "_Lying down right_", "_Running_"]

uun_list = [s for s in os.listdir(src_path) if s[0]== 's']
unvalid_uun_list = []

for uun in uun_list:
    tmp_file_types = file_types.copy()
    folder_path = os.path.join(src_path, uun)
    files = [s for s in os.listdir(folder_path)]

    for file in files:
        for file_type in tmp_file_types:
            if sensor_type in file and file_type in file:
                tmp_file_types.remove(file_type)

    if len(tmp_file_types) != 0:
        unvalid_uun_list.append(uun)

if len(unvalid_uun_list) != 0:
    print("Following users are unvalid : " + str(unvalid_uun_list))
else:
    print("All users are valid.")