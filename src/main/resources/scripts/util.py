import ms.version

ms.version.addpkg('numpy', '1.14.2')
import sys
import numpy as np

def receive_point_list_from_stdin():
    data = sys.stdin.readline()
    data = data.strip().split(',')
    dataset = []
    for i, d in enumerate(data):
        dataset.append([i, float(d)])
    return np.array(dataset)

def send_bool_list_to_stdout(bools):
    print(','.join(np.char.mod('%d', bools)))
    sys.stdout.flush()

def send_double_list_to_stdout(doubles):
    print(','.join(np.char.mod('%f', doubles)))
    sys.stdout.flush()

def nomalize_train_evaluate_data(X_train, X_predict):
    mean = np.mean(X_train, axis=0)
    std = np.std(X_train, axis=0)
    if std[1] == 0:
        X_train_norm = X_train - mean
        X_train_norm[0] = X_train_norm[0] / std[0]
    else:
        X_train_norm = (X_train - mean) / std

    # change the time mean and std
    mean[0] = np.mean(X_predict, axis=0)[0]
    std[0] = np.std(X_predict, axis=0)[0]
    if std[1] == 0:
        X_predict_norm = (X_predict - mean)
        X_predict_norm[0] = X_predict_norm[0] / std[0]
    else:
        X_predict_norm = (X_predict - mean) / std

    return X_train_norm, X_predict_norm
