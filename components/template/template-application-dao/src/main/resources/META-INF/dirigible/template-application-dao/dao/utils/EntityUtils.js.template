export const setDate = (object, property) => {
	if (object[property]) {
		object[property] = new Date(object[property]).getTime();
	}
}

export const setLocalDate = (object, property) => {
	if (object[property]) {
		object[property] = new Date(new Date(object[property]).setHours(-(new Date().getTimezoneOffset() / 60), 0, 0, 0)).toISOString();
	}
}

export const setBoolean = (object, property) => {
	if (object[property] !== undefined) {
		object[property] = object[property] ? true : false;
	}
}