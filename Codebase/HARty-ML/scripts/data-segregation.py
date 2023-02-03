import os
import shutil
import sys

sensor_type = str(sys.argv[1])
year_set = ["2022"]
file_types = ["_Sitting bent backward_", "_Sitting bent forward_","_Sitting_","_Standing_", "_Walking at normal speed_", "_Climbing stairs_", "_Descending stairs_", "_Desk work_", "_Movement_", "_Lying down on back_", "_Lying down on stomach_", "_Lying down left_", "_Lying down right_", "_Running_"]

src_path = r'../data/'
dst_path = os.path.join(r'../processed-data/')

for year in year_set:
    tmp_src_path = os.path.join(src_path, year)
    uun_list = [s for s in os.listdir(tmp_src_path) if s[0]== 's']

    for uun in uun_list:
        uun_path = os.path.join(dst_path, uun)
        try:
            os.makedirs(uun_path)
        except:
            print(uun + " skipped. User already exists.")
            continue
        
        tmp_file_types = file_types.copy()

        folder_path = os.path.join(tmp_src_path, uun)
        files = [s for s in os.listdir(folder_path) if sensor_type in s]

        for file in files:
            for file_type in tmp_file_types:
                if file_type in file:
                    file_src = os.path.join(tmp_src_path, uun, file)
                    file_dst = os.path.join(uun_path, file)
                    
                    shutil.copy(file_src, file_dst)
                    print("Copied : " + file)
                    tmp_file_types.remove(file_type)

print("Successfully moved all " + sensor_type + " files")

            
