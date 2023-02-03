import sys
import pandas as pd
import numpy as np
import matplotlib.pyplot as plt

import tensorflow as tf
from sklearn.metrics import classification_report
from sklearn.metrics import precision_recall_fscore_support
from sklearn.metrics import top_k_accuracy_score
from sklearn import metrics


def create_dataFrame(src_folder):
    base_df = pd.read_csv(src_folder)
    clean_dataFrame(base_df)
    return base_df

def clean_dataFrame(df):
    df.drop('notes', axis=1, inplace=True)
    df.drop('sensor_type', axis=1, inplace=True)
    df.drop('subject_id', axis=1, inplace=True)
    df.drop('activity_code', axis=1, inplace=True)
    df.dropna(inplace=True)
    
def get_sliding_windows(df):
    recording_id_list = df.recording_id.unique()
    sliding_windows = pd.DataFrame()
    window_size = 50 # 50 datapoints for the window size, which, at 25Hz, means 2 seconds
    step_size = 25 # this is 50% overlap

    window_number = 0 # start a counter at 0 to keep track of the window number

    for recording in recording_id_list:
        current_window = df.loc[df['recording_id'] == recording]
        large_enough_windows = [window for window in current_window.rolling(window=window_size, min_periods=window_size) if len(window) == window_size]
        overlapping_windows = large_enough_windows[::step_size] 

        for window in overlapping_windows:
            window.loc[:, 'window_id'] = window_number
            window_number += 1

        final_sliding_windows = pd.concat(overlapping_windows)
        sliding_windows = pd.concat([sliding_windows, final_sliding_windows])

    sliding_windows.reset_index(drop=True, inplace=True)
    return sliding_windows

def model_data(class_labels, df_sliding_windows):
    X = []
    y = []

    columns_of_interest = ['accel_x', 'accel_y', 'accel_z', 'gyro_x', 'gyro_y', 'gyro_z']

    for window_id, group in df_sliding_windows.groupby('window_id'):
        shape = group[columns_of_interest].values.shape
        X.append(group[columns_of_interest].values)
        y.append(class_labels[group["activity_type"].values[0]])
        
    X = np.asarray(X)
    y = np.asarray(pd.get_dummies(np.asarray(y)), dtype=np.float32)
    return (X,y)

def interpret(models_path, input_data):
    predicted_output = []
    predicted_labels = []
    interpreter = tf.lite.Interpreter(model_path=models_path)
    input_details = interpreter.get_input_details()
    output_details = interpreter.get_output_details()
    interpreter.allocate_tensors()

    for X in input_data:
        interpreter.set_tensor(input_details[0]['index'], np.array([X], dtype=np.float32))
        interpreter.invoke()
        output_data = interpreter.get_tensor(output_details[0]['index'])
        predicted_output.append(output_data)

    for x in predicted_output:
        predicted_labels.append(np.argmax(x, axis=1))

    return predicted_labels

def generate_report_and_confusion_matrix(y_true_labels, y_pred_labels):
    print("*" * 80)
    print("Classification report")
    print("*" * 80)
    print(classification_report(y_true_labels, y_pred_labels))
    confusion_matrix = metrics.confusion_matrix(y_true_labels, y_pred_labels)
    cm_display = metrics.ConfusionMatrixDisplay(confusion_matrix = confusion_matrix)
    cm_display.plot()
    plt.savefig('confusion_matrix.png', dpi=300)
    

if len(sys.argv) != 3:
    print("Please provide the model path and the test data path as arguments to this program.")
    quit()

models_path = sys.argv[1]
test_data_path = sys.argv[2]
model_evaluating = ""

if models_path == '../Models/essential-features.tflite':
    model_evaluating = 'essential'
elif models_path == '../Models/all-features.tflite':
    model_evaluating = 'all'
else:
    print("Please provide a correct model path")
    quit()

if model_evaluating == 'essential':
    predicted_output = []
    predicted_labels = []
    class_labels = {'Sitting/Standing' : 0, 'Walking at normal speed' : 1, 'Lying Down' : 2, 'Running' : 3}

    model_df = create_dataFrame(test_data_path)

    model_df.drop(model_df[model_df['activity_type'] == "Climbing stairs"].index, inplace = True)
    model_df.drop(model_df[model_df['activity_type'] == "Descending stairs"].index, inplace = True)
    model_df.drop(model_df[model_df['activity_type'] == "Movement"].index, inplace = True)

    model_df['activity_type'] = model_df['activity_type'].replace(['Sitting', 'Sitting bent forward', 'Sitting bent backward', 'Standing', 'Desk work'], 'Sitting/Standing')
    model_df['activity_type'] = model_df['activity_type'].replace(['Lying down right', 'Lying down left', 'Lying down on back', 'Lying down on stomach'], 'Lying Down')

    sliding_windows = get_sliding_windows(model_df)
    (input_data, labels) = model_data(class_labels, sliding_windows)

    y_pred_labels = interpret(models_path=models_path, input_data=input_data)
    y_true_labels = np.argmax(labels, axis=1)

    generate_report_and_confusion_matrix(y_true_labels, y_pred_labels)
    quit()

else:
    class_labels = {"Standing" : 0, "Movement" : 1, "Climbing stairs" : 2, "Descending stairs" : 3, "Desk work" : 4, "Lying down left" : 5, "Lying down on back" : 6, "Lying down on stomach" : 7, "Lying down right" : 8, "Running" : 9, "Sitting bent backward" : 10, "Sitting bent forward" : 11, "Sitting" : 12, "Walking at normal speed" : 13 }

    model_df = create_dataFrame(test_data_path)

    sliding_windows = get_sliding_windows(model_df)
    (input_data, labels) = model_data(class_labels, sliding_windows)

    y_pred_labels = interpret(models_path=models_path, input_data=input_data)
    y_true_labels = np.argmax(labels, axis=1)

    generate_report_and_confusion_matrix(y_true_labels, y_pred_labels)
    quit()