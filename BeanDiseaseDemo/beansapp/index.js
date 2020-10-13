let model;
const webcam = new Webcam(document.getElementById('wc'));

async function loadModel() {
    const the_model = await tf.loadLayersModel('model.json');
    return the_model;
}

async function predict() {
    const predictedClass = tf.tidy(() => {
      const img = webcam.capture();
      const predictions = model.predict(img);
      return predictions;
    });
    const classId = (await predictedClass.data());
    predictionText = "Angular Leaf Spot: " + classId[0] + "\n";
    predictionText += "Bean Rust: " + classId[1] + "\n";
    predictionText += "Healthy: " + classId[2]
    document.getElementById("prediction").innerText = predictionText;
    predictedClass.dispose();
    await tf.nextFrame();
}

function doPrediction(){
	predict();
}

async function init(){
	await webcam.setup();
	model = await loadModel();
	tf.tidy(() => model.predict(webcam.capture()));
		
}

init();