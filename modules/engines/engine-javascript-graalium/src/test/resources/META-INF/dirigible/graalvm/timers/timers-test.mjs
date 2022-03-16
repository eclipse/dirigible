const intervalId = setInterval(() => {
    console.log("test");
}, 1000)

setTimeout(() => {
    clearInterval(intervalId);
}, 10000);
