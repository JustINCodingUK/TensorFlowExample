# This code was run in a Linux-based environment. Run the code starting with '!' in bash or cmd

# !pip install -q --use-deprecated=legacy-resolver tflite-model-maker
# Code Cell 1
import numpy as np
import PIL
import PIL.Image
import pathlib

from absl import logging
from pathlib import Path

from tensorflow.keras import layers
from tensorflow import keras
from tensorflow.keras.models import Sequential


from tflite_model_maker.config import QuantizationConfig
from tflite_model_maker.config import ExportFormat
from tflite_model_maker import model_spec
from tflite_model_maker import object_detector

import tensorflow as tf
assert tf.__version__.startswith('2')

tf.get_logger().setLevel('ERROR')
logging.set_verbosity(logging.ERROR)

data = 'https://github.com/JustINCodingUK/TensorFlowExample/blob/main/data.tar.gz?raw=true'
data_dir = tf.keras.utils.get_file('/content/data.tar.gz', origin=data, archive_format='tar')

# !tar -xzvf "/content/data.tar.gz" -C "/content/"

# Code cell 2

import os
working_dir = '/content/d/TensorFlowExaple/TensorFlowExample/archive/'
''' 
Testing the alternative method

images = 0

for x in os.scandir('/content/d/TensorFlowExaple/TensorFlowExample/archive/apple/'):
  images+=1

for x in os.scandir('/content/d/TensorFlowExaple/TensorFlowExample/archive/banana/'):
  images+=1

for x in os.scandir('/content/d/TensorFlowExaple/TensorFlowExample/archive/mixed/'):
  images+=1

for x in os.scandir('/content/d/TensorFlowExaple/TensorFlowExample/archive/orange/'):
  images+=1

print(images)

'''
def prepend(list, str):
    str += '{0}'
    list = [str.format(i) for i in list]
    return(list)
 
appledir = list(os.listdir('/content/d/TensorFlowExaple/TensorFlowExample/archive/apple/'))
apples = prepend(appledir, '/content/d/TensorFlowExaple/TensorFlowExample/archive/apple/')
print(apples)

PIL.Image.open(apples[3])

# Code cell 3

batch_size = 32
i_height= 180
i_width=180

train_data = tf.keras.utils.image_dataset_from_directory(
    working_dir,
    validation_split=0.2,
    subset='training',
    seed=123,
    image_size=(i_height, i_width),
    batch_size=batch_size
)

validation_ds = tf.keras.utils.image_dataset_from_directory(
    working_dir,
    validation_split=0.2,
    subset='validation',
    seed=123,
    image_size=(i_height, i_width),
    batch_size=batch_size
)

classes = train_data.class_names
print(classes)

import matplotlib.pyplot as plt

plt.figure(figsize=(10, 10))
for images, labels in train_data.take(4):
  for i in range(9):
    ax = plt.subplot(3,3,i+1)
    plt.imshow(images[i].numpy().astype("uint8"))
    plt.title(classes[labels[i]])
    plt.axis("off")

for image_batch, labels_batch in train_data:
  print(image_batch.shape)
  print(labels_batch.shape)
  break

AUTOTUNE = tf.data.AUTOTUNE

train_ds = train_data.cache().shuffle(1000).prefetch(buffer_size=AUTOTUNE)
val_ds = validation_ds.cache().prefetch(buffer_size=AUTOTUNE)

normalization_layer = layers.Rescaling(1./255)

normalized_ds = train_ds.map(lambda x, y: (normalization_layer(x), y))
image_batch, labels_batch = next(iter(normalized_ds))
first_image = image_batch[0]
print(np.min(first_image), np.max(first_image))

# Code cell 4

num_classes = len(classes)

model = Sequential([
  layers.Rescaling(1./255, input_shape=(i_height, i_width, 3)),
  layers.Conv2D(16, 3, padding='same', activation='relu'),
  layers.MaxPooling2D(),
  layers.Conv2D(32, 3, padding='same', activation='relu'),
  layers.MaxPooling2D(),
  layers.Conv2D(64, 3, padding='same', activation='relu'),
  layers.MaxPooling2D(),
  layers.Flatten(),
  layers.Dense(128, activation='relu'),
  layers.Dense(num_classes)
])

model.compile(optimizer='adam',
              loss=tf.keras.losses.SparseCategoricalCrossentropy(from_logits=True),
              metrics=['accuracy'])

model.summary()

epochs=10
history = model.fit(
  train_data,
  validation_data=validation_ds,
  epochs=epochs
)

# Code cell 5

acc = history.history['accuracy']
val_acc = history.history['val_accuracy']

loss = history.history['loss']
val_loss = history.history['val_loss']

epochs_range = range(epochs)

plt.figure(figsize=(8, 8))
plt.subplot(1, 2, 1)
plt.plot(epochs_range, acc, label='Training Accuracy', color='r')
plt.plot(epochs_range, val_acc, label='Validation Accuracy', color='g')
plt.legend(loc='lower right')
plt.title('Training and Validation Accuracy')

plt.subplot(1, 2, 2)
plt.plot(epochs_range, loss, label='Training Loss', color='r')
plt.plot(epochs_range, val_loss, label='Validation Loss', color='g')
plt.legend(loc='upper right')
plt.title('Training and Validation Loss')
plt.show()

# Code cell 6

data_augmentation = keras.Sequential(
  [
    layers.RandomFlip("horizontal",
                      input_shape=(i_height,
                                  i_width,
                                  3)),
    layers.RandomRotation(0.1),
    layers.RandomZoom(0.1),
  ]
)

plt.figure(figsize=(10, 10))
for images, _ in train_ds.take(1):
  for i in range(9):
    augmented_images = data_augmentation(images)
    ax = plt.subplot(3, 3, i + 1)
    plt.imshow(augmented_images[0].numpy().astype("uint8"))
    plt.axis("off")

  model = Sequential([
    data_augmentation,
    layers.Rescaling(1./255),
    layers.Conv2D(16, 3, padding='same', activation='relu'),
    layers.MaxPooling2D(),
    layers.Conv2D(32, 3, padding='same', activation='relu'),
    layers.MaxPooling2D(),
    layers.Conv2D(64, 3, padding='same', activation='relu'),
    layers.MaxPooling2D(),
    layers.Dropout(0.2),
    layers.Flatten(),
    layers.Dense(128, activation='relu'),
    layers.Dense(num_classes)
])
  
model.compile(optimizer='adam',
              loss=tf.keras.losses.SparseCategoricalCrossentropy(from_logits=True),
              metrics=['accuracy'])

# Code cell 7

epochs = 15
history = model.fit(
  train_ds,
  validation_data=val_ds,
  epochs=epochs
)

# Code cell 8

acc = history.history['accuracy']
val_acc = history.history['val_accuracy']

loss = history.history['loss']
val_loss = history.history['val_loss']

epochs_range = range(epochs)

plt.figure(figsize=(8, 8))
plt.subplot(1, 2, 1)
plt.plot(epochs_range, acc, label='Training Accuracy')
plt.plot(epochs_range, val_acc, label='Validation Accuracy')
plt.legend(loc='lower right')
plt.title('Training and Validation Accuracy')

plt.subplot(1, 2, 2)
plt.plot(epochs_range, loss, label='Training Loss')
plt.plot(epochs_range, val_loss, label='Validation Loss')
plt.legend(loc='upper right')
plt.title('Training and Validation Loss')
plt.show()

# Code cell 9

data_url = "https://github.com/JustINCodingUK/TensorFlowExample/raw/main/sample_images/apple.jpg"
data_path = tf.keras.utils.get_file('ss', origin=data_url)

img = tf.keras.utils.load_img(
    data_path, target_size=(i_height, i_width)
)
img_array = tf.keras.utils.img_to_array(img)
img_array = tf.expand_dims(img_array, 0)  

predictions = model.predict(img_array)
score = tf.nn.softmax(predictions[0])

print(
    "This image most likely belongs to {} with a {:.2f} percent confidence."
    .format(classes[np.argmax(score)], 100 * np.max(score))
)

# Code cell 10
model.save('/contents/Fruits_classifier.h5', save_format='h5') # Saving in .h5 due to bug in tensorflow

# !tflite_convert \
#  --keras_model_file=/contents/Fruits_classifier.h5 \
#  --output_file=/contents/model.tflite
