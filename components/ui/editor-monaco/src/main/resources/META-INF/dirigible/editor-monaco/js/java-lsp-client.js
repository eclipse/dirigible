"use strict";
var JavaLspClientLib = (() => {
  var __create = Object.create;
  var __defProp = Object.defineProperty;
  var __getOwnPropDesc = Object.getOwnPropertyDescriptor;
  var __getOwnPropNames = Object.getOwnPropertyNames;
  var __getProtoOf = Object.getPrototypeOf;
  var __hasOwnProp = Object.prototype.hasOwnProperty;
  var __defNormalProp = (obj, key, value) => key in obj ? __defProp(obj, key, { enumerable: true, configurable: true, writable: true, value }) : obj[key] = value;
  var __commonJS = (cb, mod) => function __require() {
    return mod || (0, cb[__getOwnPropNames(cb)[0]])((mod = { exports: {} }).exports, mod), mod.exports;
  };
  var __export = (target, all) => {
    for (var name in all)
      __defProp(target, name, { get: all[name], enumerable: true });
  };
  var __copyProps = (to, from, except, desc) => {
    if (from && typeof from === "object" || typeof from === "function") {
      for (let key of __getOwnPropNames(from))
        if (!__hasOwnProp.call(to, key) && key !== except)
          __defProp(to, key, { get: () => from[key], enumerable: !(desc = __getOwnPropDesc(from, key)) || desc.enumerable });
    }
    return to;
  };
  var __toESM = (mod, isNodeMode, target) => (target = mod != null ? __create(__getProtoOf(mod)) : {}, __copyProps(
    // If the importer is in node compatibility mode or this is not an ESM
    // file that has been converted to a CommonJS file using a Babel-
    // compatible transform (i.e. "__esModule" has not been set), then set
    // "default" to the CommonJS "module.exports" for node compatibility.
    isNodeMode || !mod || !mod.__esModule ? __defProp(target, "default", { value: mod, enumerable: true }) : target,
    mod
  ));
  var __toCommonJS = (mod) => __copyProps(__defProp({}, "__esModule", { value: true }), mod);
  var __publicField = (obj, key, value) => __defNormalProp(obj, typeof key !== "symbol" ? key + "" : key, value);

  // node_modules/vscode-jsonrpc/lib/common/is.js
  var require_is = __commonJS({
    "node_modules/vscode-jsonrpc/lib/common/is.js"(exports) {
      "use strict";
      Object.defineProperty(exports, "__esModule", { value: true });
      exports.stringArray = exports.array = exports.func = exports.error = exports.number = exports.string = exports.boolean = void 0;
      function boolean(value) {
        return value === true || value === false;
      }
      exports.boolean = boolean;
      function string(value) {
        return typeof value === "string" || value instanceof String;
      }
      exports.string = string;
      function number(value) {
        return typeof value === "number" || value instanceof Number;
      }
      exports.number = number;
      function error(value) {
        return value instanceof Error;
      }
      exports.error = error;
      function func(value) {
        return typeof value === "function";
      }
      exports.func = func;
      function array(value) {
        return Array.isArray(value);
      }
      exports.array = array;
      function stringArray(value) {
        return array(value) && value.every((elem) => string(elem));
      }
      exports.stringArray = stringArray;
    }
  });

  // node_modules/vscode-jsonrpc/lib/common/messages.js
  var require_messages = __commonJS({
    "node_modules/vscode-jsonrpc/lib/common/messages.js"(exports) {
      "use strict";
      Object.defineProperty(exports, "__esModule", { value: true });
      exports.Message = exports.NotificationType9 = exports.NotificationType8 = exports.NotificationType7 = exports.NotificationType6 = exports.NotificationType5 = exports.NotificationType4 = exports.NotificationType3 = exports.NotificationType2 = exports.NotificationType1 = exports.NotificationType0 = exports.NotificationType = exports.RequestType9 = exports.RequestType8 = exports.RequestType7 = exports.RequestType6 = exports.RequestType5 = exports.RequestType4 = exports.RequestType3 = exports.RequestType2 = exports.RequestType1 = exports.RequestType = exports.RequestType0 = exports.AbstractMessageSignature = exports.ParameterStructures = exports.ResponseError = exports.ErrorCodes = void 0;
      var is = require_is();
      var ErrorCodes;
      (function(ErrorCodes2) {
        ErrorCodes2.ParseError = -32700;
        ErrorCodes2.InvalidRequest = -32600;
        ErrorCodes2.MethodNotFound = -32601;
        ErrorCodes2.InvalidParams = -32602;
        ErrorCodes2.InternalError = -32603;
        ErrorCodes2.jsonrpcReservedErrorRangeStart = -32099;
        ErrorCodes2.serverErrorStart = -32099;
        ErrorCodes2.MessageWriteError = -32099;
        ErrorCodes2.MessageReadError = -32098;
        ErrorCodes2.PendingResponseRejected = -32097;
        ErrorCodes2.ConnectionInactive = -32096;
        ErrorCodes2.ServerNotInitialized = -32002;
        ErrorCodes2.UnknownErrorCode = -32001;
        ErrorCodes2.jsonrpcReservedErrorRangeEnd = -32e3;
        ErrorCodes2.serverErrorEnd = -32e3;
      })(ErrorCodes || (exports.ErrorCodes = ErrorCodes = {}));
      var ResponseError = class _ResponseError extends Error {
        constructor(code, message, data) {
          super(message);
          this.code = is.number(code) ? code : ErrorCodes.UnknownErrorCode;
          this.data = data;
          Object.setPrototypeOf(this, _ResponseError.prototype);
        }
        toJson() {
          const result = {
            code: this.code,
            message: this.message
          };
          if (this.data !== void 0) {
            result.data = this.data;
          }
          return result;
        }
      };
      exports.ResponseError = ResponseError;
      var ParameterStructures = class _ParameterStructures {
        constructor(kind) {
          this.kind = kind;
        }
        static is(value) {
          return value === _ParameterStructures.auto || value === _ParameterStructures.byName || value === _ParameterStructures.byPosition;
        }
        toString() {
          return this.kind;
        }
      };
      exports.ParameterStructures = ParameterStructures;
      ParameterStructures.auto = new ParameterStructures("auto");
      ParameterStructures.byPosition = new ParameterStructures("byPosition");
      ParameterStructures.byName = new ParameterStructures("byName");
      var AbstractMessageSignature = class {
        constructor(method, numberOfParams) {
          this.method = method;
          this.numberOfParams = numberOfParams;
        }
        get parameterStructures() {
          return ParameterStructures.auto;
        }
      };
      exports.AbstractMessageSignature = AbstractMessageSignature;
      var RequestType0 = class extends AbstractMessageSignature {
        constructor(method) {
          super(method, 0);
        }
      };
      exports.RequestType0 = RequestType0;
      var RequestType = class extends AbstractMessageSignature {
        constructor(method, _parameterStructures = ParameterStructures.auto) {
          super(method, 1);
          this._parameterStructures = _parameterStructures;
        }
        get parameterStructures() {
          return this._parameterStructures;
        }
      };
      exports.RequestType = RequestType;
      var RequestType1 = class extends AbstractMessageSignature {
        constructor(method, _parameterStructures = ParameterStructures.auto) {
          super(method, 1);
          this._parameterStructures = _parameterStructures;
        }
        get parameterStructures() {
          return this._parameterStructures;
        }
      };
      exports.RequestType1 = RequestType1;
      var RequestType2 = class extends AbstractMessageSignature {
        constructor(method) {
          super(method, 2);
        }
      };
      exports.RequestType2 = RequestType2;
      var RequestType3 = class extends AbstractMessageSignature {
        constructor(method) {
          super(method, 3);
        }
      };
      exports.RequestType3 = RequestType3;
      var RequestType4 = class extends AbstractMessageSignature {
        constructor(method) {
          super(method, 4);
        }
      };
      exports.RequestType4 = RequestType4;
      var RequestType5 = class extends AbstractMessageSignature {
        constructor(method) {
          super(method, 5);
        }
      };
      exports.RequestType5 = RequestType5;
      var RequestType6 = class extends AbstractMessageSignature {
        constructor(method) {
          super(method, 6);
        }
      };
      exports.RequestType6 = RequestType6;
      var RequestType7 = class extends AbstractMessageSignature {
        constructor(method) {
          super(method, 7);
        }
      };
      exports.RequestType7 = RequestType7;
      var RequestType8 = class extends AbstractMessageSignature {
        constructor(method) {
          super(method, 8);
        }
      };
      exports.RequestType8 = RequestType8;
      var RequestType9 = class extends AbstractMessageSignature {
        constructor(method) {
          super(method, 9);
        }
      };
      exports.RequestType9 = RequestType9;
      var NotificationType = class extends AbstractMessageSignature {
        constructor(method, _parameterStructures = ParameterStructures.auto) {
          super(method, 1);
          this._parameterStructures = _parameterStructures;
        }
        get parameterStructures() {
          return this._parameterStructures;
        }
      };
      exports.NotificationType = NotificationType;
      var NotificationType0 = class extends AbstractMessageSignature {
        constructor(method) {
          super(method, 0);
        }
      };
      exports.NotificationType0 = NotificationType0;
      var NotificationType1 = class extends AbstractMessageSignature {
        constructor(method, _parameterStructures = ParameterStructures.auto) {
          super(method, 1);
          this._parameterStructures = _parameterStructures;
        }
        get parameterStructures() {
          return this._parameterStructures;
        }
      };
      exports.NotificationType1 = NotificationType1;
      var NotificationType2 = class extends AbstractMessageSignature {
        constructor(method) {
          super(method, 2);
        }
      };
      exports.NotificationType2 = NotificationType2;
      var NotificationType3 = class extends AbstractMessageSignature {
        constructor(method) {
          super(method, 3);
        }
      };
      exports.NotificationType3 = NotificationType3;
      var NotificationType4 = class extends AbstractMessageSignature {
        constructor(method) {
          super(method, 4);
        }
      };
      exports.NotificationType4 = NotificationType4;
      var NotificationType5 = class extends AbstractMessageSignature {
        constructor(method) {
          super(method, 5);
        }
      };
      exports.NotificationType5 = NotificationType5;
      var NotificationType6 = class extends AbstractMessageSignature {
        constructor(method) {
          super(method, 6);
        }
      };
      exports.NotificationType6 = NotificationType6;
      var NotificationType7 = class extends AbstractMessageSignature {
        constructor(method) {
          super(method, 7);
        }
      };
      exports.NotificationType7 = NotificationType7;
      var NotificationType8 = class extends AbstractMessageSignature {
        constructor(method) {
          super(method, 8);
        }
      };
      exports.NotificationType8 = NotificationType8;
      var NotificationType9 = class extends AbstractMessageSignature {
        constructor(method) {
          super(method, 9);
        }
      };
      exports.NotificationType9 = NotificationType9;
      var Message2;
      (function(Message3) {
        function isRequest(message) {
          const candidate = message;
          return candidate && is.string(candidate.method) && (is.string(candidate.id) || is.number(candidate.id));
        }
        Message3.isRequest = isRequest;
        function isNotification(message) {
          const candidate = message;
          return candidate && is.string(candidate.method) && message.id === void 0;
        }
        Message3.isNotification = isNotification;
        function isResponse(message) {
          const candidate = message;
          return candidate && (candidate.result !== void 0 || !!candidate.error) && (is.string(candidate.id) || is.number(candidate.id) || candidate.id === null);
        }
        Message3.isResponse = isResponse;
      })(Message2 || (exports.Message = Message2 = {}));
    }
  });

  // node_modules/vscode-jsonrpc/lib/common/linkedMap.js
  var require_linkedMap = __commonJS({
    "node_modules/vscode-jsonrpc/lib/common/linkedMap.js"(exports) {
      "use strict";
      var _a;
      Object.defineProperty(exports, "__esModule", { value: true });
      exports.LRUCache = exports.LinkedMap = exports.Touch = void 0;
      var Touch;
      (function(Touch2) {
        Touch2.None = 0;
        Touch2.First = 1;
        Touch2.AsOld = Touch2.First;
        Touch2.Last = 2;
        Touch2.AsNew = Touch2.Last;
      })(Touch || (exports.Touch = Touch = {}));
      var LinkedMap = class {
        constructor() {
          this[_a] = "LinkedMap";
          this._map = /* @__PURE__ */ new Map();
          this._head = void 0;
          this._tail = void 0;
          this._size = 0;
          this._state = 0;
        }
        clear() {
          this._map.clear();
          this._head = void 0;
          this._tail = void 0;
          this._size = 0;
          this._state++;
        }
        isEmpty() {
          return !this._head && !this._tail;
        }
        get size() {
          return this._size;
        }
        get first() {
          return this._head?.value;
        }
        get last() {
          return this._tail?.value;
        }
        has(key) {
          return this._map.has(key);
        }
        get(key, touch = Touch.None) {
          const item = this._map.get(key);
          if (!item) {
            return void 0;
          }
          if (touch !== Touch.None) {
            this.touch(item, touch);
          }
          return item.value;
        }
        set(key, value, touch = Touch.None) {
          let item = this._map.get(key);
          if (item) {
            item.value = value;
            if (touch !== Touch.None) {
              this.touch(item, touch);
            }
          } else {
            item = { key, value, next: void 0, previous: void 0 };
            switch (touch) {
              case Touch.None:
                this.addItemLast(item);
                break;
              case Touch.First:
                this.addItemFirst(item);
                break;
              case Touch.Last:
                this.addItemLast(item);
                break;
              default:
                this.addItemLast(item);
                break;
            }
            this._map.set(key, item);
            this._size++;
          }
          return this;
        }
        delete(key) {
          return !!this.remove(key);
        }
        remove(key) {
          const item = this._map.get(key);
          if (!item) {
            return void 0;
          }
          this._map.delete(key);
          this.removeItem(item);
          this._size--;
          return item.value;
        }
        shift() {
          if (!this._head && !this._tail) {
            return void 0;
          }
          if (!this._head || !this._tail) {
            throw new Error("Invalid list");
          }
          const item = this._head;
          this._map.delete(item.key);
          this.removeItem(item);
          this._size--;
          return item.value;
        }
        forEach(callbackfn, thisArg) {
          const state = this._state;
          let current = this._head;
          while (current) {
            if (thisArg) {
              callbackfn.bind(thisArg)(current.value, current.key, this);
            } else {
              callbackfn(current.value, current.key, this);
            }
            if (this._state !== state) {
              throw new Error(`LinkedMap got modified during iteration.`);
            }
            current = current.next;
          }
        }
        keys() {
          const state = this._state;
          let current = this._head;
          const iterator = {
            [Symbol.iterator]: () => {
              return iterator;
            },
            next: () => {
              if (this._state !== state) {
                throw new Error(`LinkedMap got modified during iteration.`);
              }
              if (current) {
                const result = { value: current.key, done: false };
                current = current.next;
                return result;
              } else {
                return { value: void 0, done: true };
              }
            }
          };
          return iterator;
        }
        values() {
          const state = this._state;
          let current = this._head;
          const iterator = {
            [Symbol.iterator]: () => {
              return iterator;
            },
            next: () => {
              if (this._state !== state) {
                throw new Error(`LinkedMap got modified during iteration.`);
              }
              if (current) {
                const result = { value: current.value, done: false };
                current = current.next;
                return result;
              } else {
                return { value: void 0, done: true };
              }
            }
          };
          return iterator;
        }
        entries() {
          const state = this._state;
          let current = this._head;
          const iterator = {
            [Symbol.iterator]: () => {
              return iterator;
            },
            next: () => {
              if (this._state !== state) {
                throw new Error(`LinkedMap got modified during iteration.`);
              }
              if (current) {
                const result = { value: [current.key, current.value], done: false };
                current = current.next;
                return result;
              } else {
                return { value: void 0, done: true };
              }
            }
          };
          return iterator;
        }
        [(_a = Symbol.toStringTag, Symbol.iterator)]() {
          return this.entries();
        }
        trimOld(newSize) {
          if (newSize >= this.size) {
            return;
          }
          if (newSize === 0) {
            this.clear();
            return;
          }
          let current = this._head;
          let currentSize = this.size;
          while (current && currentSize > newSize) {
            this._map.delete(current.key);
            current = current.next;
            currentSize--;
          }
          this._head = current;
          this._size = currentSize;
          if (current) {
            current.previous = void 0;
          }
          this._state++;
        }
        addItemFirst(item) {
          if (!this._head && !this._tail) {
            this._tail = item;
          } else if (!this._head) {
            throw new Error("Invalid list");
          } else {
            item.next = this._head;
            this._head.previous = item;
          }
          this._head = item;
          this._state++;
        }
        addItemLast(item) {
          if (!this._head && !this._tail) {
            this._head = item;
          } else if (!this._tail) {
            throw new Error("Invalid list");
          } else {
            item.previous = this._tail;
            this._tail.next = item;
          }
          this._tail = item;
          this._state++;
        }
        removeItem(item) {
          if (item === this._head && item === this._tail) {
            this._head = void 0;
            this._tail = void 0;
          } else if (item === this._head) {
            if (!item.next) {
              throw new Error("Invalid list");
            }
            item.next.previous = void 0;
            this._head = item.next;
          } else if (item === this._tail) {
            if (!item.previous) {
              throw new Error("Invalid list");
            }
            item.previous.next = void 0;
            this._tail = item.previous;
          } else {
            const next = item.next;
            const previous = item.previous;
            if (!next || !previous) {
              throw new Error("Invalid list");
            }
            next.previous = previous;
            previous.next = next;
          }
          item.next = void 0;
          item.previous = void 0;
          this._state++;
        }
        touch(item, touch) {
          if (!this._head || !this._tail) {
            throw new Error("Invalid list");
          }
          if (touch !== Touch.First && touch !== Touch.Last) {
            return;
          }
          if (touch === Touch.First) {
            if (item === this._head) {
              return;
            }
            const next = item.next;
            const previous = item.previous;
            if (item === this._tail) {
              previous.next = void 0;
              this._tail = previous;
            } else {
              next.previous = previous;
              previous.next = next;
            }
            item.previous = void 0;
            item.next = this._head;
            this._head.previous = item;
            this._head = item;
            this._state++;
          } else if (touch === Touch.Last) {
            if (item === this._tail) {
              return;
            }
            const next = item.next;
            const previous = item.previous;
            if (item === this._head) {
              next.previous = void 0;
              this._head = next;
            } else {
              next.previous = previous;
              previous.next = next;
            }
            item.next = void 0;
            item.previous = this._tail;
            this._tail.next = item;
            this._tail = item;
            this._state++;
          }
        }
        toJSON() {
          const data = [];
          this.forEach((value, key) => {
            data.push([key, value]);
          });
          return data;
        }
        fromJSON(data) {
          this.clear();
          for (const [key, value] of data) {
            this.set(key, value);
          }
        }
      };
      exports.LinkedMap = LinkedMap;
      var LRUCache = class extends LinkedMap {
        constructor(limit, ratio = 1) {
          super();
          this._limit = limit;
          this._ratio = Math.min(Math.max(0, ratio), 1);
        }
        get limit() {
          return this._limit;
        }
        set limit(limit) {
          this._limit = limit;
          this.checkTrim();
        }
        get ratio() {
          return this._ratio;
        }
        set ratio(ratio) {
          this._ratio = Math.min(Math.max(0, ratio), 1);
          this.checkTrim();
        }
        get(key, touch = Touch.AsNew) {
          return super.get(key, touch);
        }
        peek(key) {
          return super.get(key, Touch.None);
        }
        set(key, value) {
          super.set(key, value, Touch.Last);
          this.checkTrim();
          return this;
        }
        checkTrim() {
          if (this.size > this._limit) {
            this.trimOld(Math.round(this._limit * this._ratio));
          }
        }
      };
      exports.LRUCache = LRUCache;
    }
  });

  // node_modules/vscode-jsonrpc/lib/common/disposable.js
  var require_disposable = __commonJS({
    "node_modules/vscode-jsonrpc/lib/common/disposable.js"(exports) {
      "use strict";
      Object.defineProperty(exports, "__esModule", { value: true });
      exports.Disposable = void 0;
      var Disposable4;
      (function(Disposable5) {
        function create(func) {
          return {
            dispose: func
          };
        }
        Disposable5.create = create;
      })(Disposable4 || (exports.Disposable = Disposable4 = {}));
    }
  });

  // node_modules/vscode-jsonrpc/lib/common/ral.js
  var require_ral = __commonJS({
    "node_modules/vscode-jsonrpc/lib/common/ral.js"(exports) {
      "use strict";
      Object.defineProperty(exports, "__esModule", { value: true });
      var _ral;
      function RAL() {
        if (_ral === void 0) {
          throw new Error(`No runtime abstraction layer installed`);
        }
        return _ral;
      }
      (function(RAL2) {
        function install(ral) {
          if (ral === void 0) {
            throw new Error(`No runtime abstraction layer provided`);
          }
          _ral = ral;
        }
        RAL2.install = install;
      })(RAL || (RAL = {}));
      exports.default = RAL;
    }
  });

  // node_modules/vscode-jsonrpc/lib/common/events.js
  var require_events = __commonJS({
    "node_modules/vscode-jsonrpc/lib/common/events.js"(exports) {
      "use strict";
      Object.defineProperty(exports, "__esModule", { value: true });
      exports.Emitter = exports.Event = void 0;
      var ral_1 = require_ral();
      var Event;
      (function(Event2) {
        const _disposable = { dispose() {
        } };
        Event2.None = function() {
          return _disposable;
        };
      })(Event || (exports.Event = Event = {}));
      var CallbackList = class {
        add(callback, context = null, bucket) {
          if (!this._callbacks) {
            this._callbacks = [];
            this._contexts = [];
          }
          this._callbacks.push(callback);
          this._contexts.push(context);
          if (Array.isArray(bucket)) {
            bucket.push({ dispose: () => this.remove(callback, context) });
          }
        }
        remove(callback, context = null) {
          if (!this._callbacks) {
            return;
          }
          let foundCallbackWithDifferentContext = false;
          for (let i = 0, len = this._callbacks.length; i < len; i++) {
            if (this._callbacks[i] === callback) {
              if (this._contexts[i] === context) {
                this._callbacks.splice(i, 1);
                this._contexts.splice(i, 1);
                return;
              } else {
                foundCallbackWithDifferentContext = true;
              }
            }
          }
          if (foundCallbackWithDifferentContext) {
            throw new Error("When adding a listener with a context, you should remove it with the same context");
          }
        }
        invoke(...args) {
          if (!this._callbacks) {
            return [];
          }
          const ret = [], callbacks = this._callbacks.slice(0), contexts = this._contexts.slice(0);
          for (let i = 0, len = callbacks.length; i < len; i++) {
            try {
              ret.push(callbacks[i].apply(contexts[i], args));
            } catch (e) {
              (0, ral_1.default)().console.error(e);
            }
          }
          return ret;
        }
        isEmpty() {
          return !this._callbacks || this._callbacks.length === 0;
        }
        dispose() {
          this._callbacks = void 0;
          this._contexts = void 0;
        }
      };
      var Emitter = class _Emitter {
        constructor(_options) {
          this._options = _options;
        }
        /**
         * For the public to allow to subscribe
         * to events from this Emitter
         */
        get event() {
          if (!this._event) {
            this._event = (listener, thisArgs, disposables) => {
              if (!this._callbacks) {
                this._callbacks = new CallbackList();
              }
              if (this._options && this._options.onFirstListenerAdd && this._callbacks.isEmpty()) {
                this._options.onFirstListenerAdd(this);
              }
              this._callbacks.add(listener, thisArgs);
              const result = {
                dispose: () => {
                  if (!this._callbacks) {
                    return;
                  }
                  this._callbacks.remove(listener, thisArgs);
                  result.dispose = _Emitter._noop;
                  if (this._options && this._options.onLastListenerRemove && this._callbacks.isEmpty()) {
                    this._options.onLastListenerRemove(this);
                  }
                }
              };
              if (Array.isArray(disposables)) {
                disposables.push(result);
              }
              return result;
            };
          }
          return this._event;
        }
        /**
         * To be kept private to fire an event to
         * subscribers
         */
        fire(event) {
          if (this._callbacks) {
            this._callbacks.invoke.call(this._callbacks, event);
          }
        }
        dispose() {
          if (this._callbacks) {
            this._callbacks.dispose();
            this._callbacks = void 0;
          }
        }
      };
      exports.Emitter = Emitter;
      Emitter._noop = function() {
      };
    }
  });

  // node_modules/vscode-jsonrpc/lib/common/cancellation.js
  var require_cancellation = __commonJS({
    "node_modules/vscode-jsonrpc/lib/common/cancellation.js"(exports) {
      "use strict";
      Object.defineProperty(exports, "__esModule", { value: true });
      exports.CancellationTokenSource = exports.CancellationToken = void 0;
      var ral_1 = require_ral();
      var Is2 = require_is();
      var events_1 = require_events();
      var CancellationToken;
      (function(CancellationToken2) {
        CancellationToken2.None = Object.freeze({
          isCancellationRequested: false,
          onCancellationRequested: events_1.Event.None
        });
        CancellationToken2.Cancelled = Object.freeze({
          isCancellationRequested: true,
          onCancellationRequested: events_1.Event.None
        });
        function is(value) {
          const candidate = value;
          return candidate && (candidate === CancellationToken2.None || candidate === CancellationToken2.Cancelled || Is2.boolean(candidate.isCancellationRequested) && !!candidate.onCancellationRequested);
        }
        CancellationToken2.is = is;
      })(CancellationToken || (exports.CancellationToken = CancellationToken = {}));
      var shortcutEvent = Object.freeze(function(callback, context) {
        const handle = (0, ral_1.default)().timer.setTimeout(callback.bind(context), 0);
        return { dispose() {
          handle.dispose();
        } };
      });
      var MutableToken = class {
        constructor() {
          this._isCancelled = false;
        }
        cancel() {
          if (!this._isCancelled) {
            this._isCancelled = true;
            if (this._emitter) {
              this._emitter.fire(void 0);
              this.dispose();
            }
          }
        }
        get isCancellationRequested() {
          return this._isCancelled;
        }
        get onCancellationRequested() {
          if (this._isCancelled) {
            return shortcutEvent;
          }
          if (!this._emitter) {
            this._emitter = new events_1.Emitter();
          }
          return this._emitter.event;
        }
        dispose() {
          if (this._emitter) {
            this._emitter.dispose();
            this._emitter = void 0;
          }
        }
      };
      var CancellationTokenSource = class {
        get token() {
          if (!this._token) {
            this._token = new MutableToken();
          }
          return this._token;
        }
        cancel() {
          if (!this._token) {
            this._token = CancellationToken.Cancelled;
          } else {
            this._token.cancel();
          }
        }
        dispose() {
          if (!this._token) {
            this._token = CancellationToken.None;
          } else if (this._token instanceof MutableToken) {
            this._token.dispose();
          }
        }
      };
      exports.CancellationTokenSource = CancellationTokenSource;
    }
  });

  // node_modules/vscode-jsonrpc/lib/common/sharedArrayCancellation.js
  var require_sharedArrayCancellation = __commonJS({
    "node_modules/vscode-jsonrpc/lib/common/sharedArrayCancellation.js"(exports) {
      "use strict";
      Object.defineProperty(exports, "__esModule", { value: true });
      exports.SharedArrayReceiverStrategy = exports.SharedArraySenderStrategy = void 0;
      var cancellation_1 = require_cancellation();
      var CancellationState;
      (function(CancellationState2) {
        CancellationState2.Continue = 0;
        CancellationState2.Cancelled = 1;
      })(CancellationState || (CancellationState = {}));
      var SharedArraySenderStrategy = class {
        constructor() {
          this.buffers = /* @__PURE__ */ new Map();
        }
        enableCancellation(request) {
          if (request.id === null) {
            return;
          }
          const buffer = new SharedArrayBuffer(4);
          const data = new Int32Array(buffer, 0, 1);
          data[0] = CancellationState.Continue;
          this.buffers.set(request.id, buffer);
          request.$cancellationData = buffer;
        }
        async sendCancellation(_conn2, id) {
          const buffer = this.buffers.get(id);
          if (buffer === void 0) {
            return;
          }
          const data = new Int32Array(buffer, 0, 1);
          Atomics.store(data, 0, CancellationState.Cancelled);
        }
        cleanup(id) {
          this.buffers.delete(id);
        }
        dispose() {
          this.buffers.clear();
        }
      };
      exports.SharedArraySenderStrategy = SharedArraySenderStrategy;
      var SharedArrayBufferCancellationToken = class {
        constructor(buffer) {
          this.data = new Int32Array(buffer, 0, 1);
        }
        get isCancellationRequested() {
          return Atomics.load(this.data, 0) === CancellationState.Cancelled;
        }
        get onCancellationRequested() {
          throw new Error(`Cancellation over SharedArrayBuffer doesn't support cancellation events`);
        }
      };
      var SharedArrayBufferCancellationTokenSource = class {
        constructor(buffer) {
          this.token = new SharedArrayBufferCancellationToken(buffer);
        }
        cancel() {
        }
        dispose() {
        }
      };
      var SharedArrayReceiverStrategy = class {
        constructor() {
          this.kind = "request";
        }
        createCancellationTokenSource(request) {
          const buffer = request.$cancellationData;
          if (buffer === void 0) {
            return new cancellation_1.CancellationTokenSource();
          }
          return new SharedArrayBufferCancellationTokenSource(buffer);
        }
      };
      exports.SharedArrayReceiverStrategy = SharedArrayReceiverStrategy;
    }
  });

  // node_modules/vscode-jsonrpc/lib/common/semaphore.js
  var require_semaphore = __commonJS({
    "node_modules/vscode-jsonrpc/lib/common/semaphore.js"(exports) {
      "use strict";
      Object.defineProperty(exports, "__esModule", { value: true });
      exports.Semaphore = void 0;
      var ral_1 = require_ral();
      var Semaphore = class {
        constructor(capacity = 1) {
          if (capacity <= 0) {
            throw new Error("Capacity must be greater than 0");
          }
          this._capacity = capacity;
          this._active = 0;
          this._waiting = [];
        }
        lock(thunk) {
          return new Promise((resolve, reject) => {
            this._waiting.push({ thunk, resolve, reject });
            this.runNext();
          });
        }
        get active() {
          return this._active;
        }
        runNext() {
          if (this._waiting.length === 0 || this._active === this._capacity) {
            return;
          }
          (0, ral_1.default)().timer.setImmediate(() => this.doRunNext());
        }
        doRunNext() {
          if (this._waiting.length === 0 || this._active === this._capacity) {
            return;
          }
          const next = this._waiting.shift();
          this._active++;
          if (this._active > this._capacity) {
            throw new Error(`To many thunks active`);
          }
          try {
            const result = next.thunk();
            if (result instanceof Promise) {
              result.then((value) => {
                this._active--;
                next.resolve(value);
                this.runNext();
              }, (err) => {
                this._active--;
                next.reject(err);
                this.runNext();
              });
            } else {
              this._active--;
              next.resolve(result);
              this.runNext();
            }
          } catch (err) {
            this._active--;
            next.reject(err);
            this.runNext();
          }
        }
      };
      exports.Semaphore = Semaphore;
    }
  });

  // node_modules/vscode-jsonrpc/lib/common/messageReader.js
  var require_messageReader = __commonJS({
    "node_modules/vscode-jsonrpc/lib/common/messageReader.js"(exports) {
      "use strict";
      Object.defineProperty(exports, "__esModule", { value: true });
      exports.ReadableStreamMessageReader = exports.AbstractMessageReader = exports.MessageReader = void 0;
      var ral_1 = require_ral();
      var Is2 = require_is();
      var events_1 = require_events();
      var semaphore_1 = require_semaphore();
      var MessageReader2;
      (function(MessageReader3) {
        function is(value) {
          let candidate = value;
          return candidate && Is2.func(candidate.listen) && Is2.func(candidate.dispose) && Is2.func(candidate.onError) && Is2.func(candidate.onClose) && Is2.func(candidate.onPartialMessage);
        }
        MessageReader3.is = is;
      })(MessageReader2 || (exports.MessageReader = MessageReader2 = {}));
      var AbstractMessageReader2 = class {
        constructor() {
          this.errorEmitter = new events_1.Emitter();
          this.closeEmitter = new events_1.Emitter();
          this.partialMessageEmitter = new events_1.Emitter();
        }
        dispose() {
          this.errorEmitter.dispose();
          this.closeEmitter.dispose();
        }
        get onError() {
          return this.errorEmitter.event;
        }
        fireError(error) {
          this.errorEmitter.fire(this.asError(error));
        }
        get onClose() {
          return this.closeEmitter.event;
        }
        fireClose() {
          this.closeEmitter.fire(void 0);
        }
        get onPartialMessage() {
          return this.partialMessageEmitter.event;
        }
        firePartialMessage(info) {
          this.partialMessageEmitter.fire(info);
        }
        asError(error) {
          if (error instanceof Error) {
            return error;
          } else {
            return new Error(`Reader received error. Reason: ${Is2.string(error.message) ? error.message : "unknown"}`);
          }
        }
      };
      exports.AbstractMessageReader = AbstractMessageReader2;
      var ResolvedMessageReaderOptions;
      (function(ResolvedMessageReaderOptions2) {
        function fromOptions(options) {
          let charset;
          let result;
          let contentDecoder;
          const contentDecoders = /* @__PURE__ */ new Map();
          let contentTypeDecoder;
          const contentTypeDecoders = /* @__PURE__ */ new Map();
          if (options === void 0 || typeof options === "string") {
            charset = options ?? "utf-8";
          } else {
            charset = options.charset ?? "utf-8";
            if (options.contentDecoder !== void 0) {
              contentDecoder = options.contentDecoder;
              contentDecoders.set(contentDecoder.name, contentDecoder);
            }
            if (options.contentDecoders !== void 0) {
              for (const decoder of options.contentDecoders) {
                contentDecoders.set(decoder.name, decoder);
              }
            }
            if (options.contentTypeDecoder !== void 0) {
              contentTypeDecoder = options.contentTypeDecoder;
              contentTypeDecoders.set(contentTypeDecoder.name, contentTypeDecoder);
            }
            if (options.contentTypeDecoders !== void 0) {
              for (const decoder of options.contentTypeDecoders) {
                contentTypeDecoders.set(decoder.name, decoder);
              }
            }
          }
          if (contentTypeDecoder === void 0) {
            contentTypeDecoder = (0, ral_1.default)().applicationJson.decoder;
            contentTypeDecoders.set(contentTypeDecoder.name, contentTypeDecoder);
          }
          return { charset, contentDecoder, contentDecoders, contentTypeDecoder, contentTypeDecoders };
        }
        ResolvedMessageReaderOptions2.fromOptions = fromOptions;
      })(ResolvedMessageReaderOptions || (ResolvedMessageReaderOptions = {}));
      var ReadableStreamMessageReader = class extends AbstractMessageReader2 {
        constructor(readable, options) {
          super();
          this.readable = readable;
          this.options = ResolvedMessageReaderOptions.fromOptions(options);
          this.buffer = (0, ral_1.default)().messageBuffer.create(this.options.charset);
          this._partialMessageTimeout = 1e4;
          this.nextMessageLength = -1;
          this.messageToken = 0;
          this.readSemaphore = new semaphore_1.Semaphore(1);
        }
        set partialMessageTimeout(timeout) {
          this._partialMessageTimeout = timeout;
        }
        get partialMessageTimeout() {
          return this._partialMessageTimeout;
        }
        listen(callback) {
          this.nextMessageLength = -1;
          this.messageToken = 0;
          this.partialMessageTimer = void 0;
          this.callback = callback;
          const result = this.readable.onData((data) => {
            this.onData(data);
          });
          this.readable.onError((error) => this.fireError(error));
          this.readable.onClose(() => this.fireClose());
          return result;
        }
        onData(data) {
          try {
            this.buffer.append(data);
            while (true) {
              if (this.nextMessageLength === -1) {
                const headers = this.buffer.tryReadHeaders(true);
                if (!headers) {
                  return;
                }
                const contentLength = headers.get("content-length");
                if (!contentLength) {
                  this.fireError(new Error(`Header must provide a Content-Length property.
${JSON.stringify(Object.fromEntries(headers))}`));
                  return;
                }
                const length = parseInt(contentLength);
                if (isNaN(length)) {
                  this.fireError(new Error(`Content-Length value must be a number. Got ${contentLength}`));
                  return;
                }
                this.nextMessageLength = length;
              }
              const body = this.buffer.tryReadBody(this.nextMessageLength);
              if (body === void 0) {
                this.setPartialMessageTimer();
                return;
              }
              this.clearPartialMessageTimer();
              this.nextMessageLength = -1;
              this.readSemaphore.lock(async () => {
                const bytes = this.options.contentDecoder !== void 0 ? await this.options.contentDecoder.decode(body) : body;
                const message = await this.options.contentTypeDecoder.decode(bytes, this.options);
                this.callback(message);
              }).catch((error) => {
                this.fireError(error);
              });
            }
          } catch (error) {
            this.fireError(error);
          }
        }
        clearPartialMessageTimer() {
          if (this.partialMessageTimer) {
            this.partialMessageTimer.dispose();
            this.partialMessageTimer = void 0;
          }
        }
        setPartialMessageTimer() {
          this.clearPartialMessageTimer();
          if (this._partialMessageTimeout <= 0) {
            return;
          }
          this.partialMessageTimer = (0, ral_1.default)().timer.setTimeout((token, timeout) => {
            this.partialMessageTimer = void 0;
            if (token === this.messageToken) {
              this.firePartialMessage({ messageToken: token, waitingTime: timeout });
              this.setPartialMessageTimer();
            }
          }, this._partialMessageTimeout, this.messageToken, this._partialMessageTimeout);
        }
      };
      exports.ReadableStreamMessageReader = ReadableStreamMessageReader;
    }
  });

  // node_modules/vscode-jsonrpc/lib/common/messageWriter.js
  var require_messageWriter = __commonJS({
    "node_modules/vscode-jsonrpc/lib/common/messageWriter.js"(exports) {
      "use strict";
      Object.defineProperty(exports, "__esModule", { value: true });
      exports.WriteableStreamMessageWriter = exports.AbstractMessageWriter = exports.MessageWriter = void 0;
      var ral_1 = require_ral();
      var Is2 = require_is();
      var semaphore_1 = require_semaphore();
      var events_1 = require_events();
      var ContentLength = "Content-Length: ";
      var CRLF = "\r\n";
      var MessageWriter2;
      (function(MessageWriter3) {
        function is(value) {
          let candidate = value;
          return candidate && Is2.func(candidate.dispose) && Is2.func(candidate.onClose) && Is2.func(candidate.onError) && Is2.func(candidate.write);
        }
        MessageWriter3.is = is;
      })(MessageWriter2 || (exports.MessageWriter = MessageWriter2 = {}));
      var AbstractMessageWriter2 = class {
        constructor() {
          this.errorEmitter = new events_1.Emitter();
          this.closeEmitter = new events_1.Emitter();
        }
        dispose() {
          this.errorEmitter.dispose();
          this.closeEmitter.dispose();
        }
        get onError() {
          return this.errorEmitter.event;
        }
        fireError(error, message, count) {
          this.errorEmitter.fire([this.asError(error), message, count]);
        }
        get onClose() {
          return this.closeEmitter.event;
        }
        fireClose() {
          this.closeEmitter.fire(void 0);
        }
        asError(error) {
          if (error instanceof Error) {
            return error;
          } else {
            return new Error(`Writer received error. Reason: ${Is2.string(error.message) ? error.message : "unknown"}`);
          }
        }
      };
      exports.AbstractMessageWriter = AbstractMessageWriter2;
      var ResolvedMessageWriterOptions;
      (function(ResolvedMessageWriterOptions2) {
        function fromOptions(options) {
          if (options === void 0 || typeof options === "string") {
            return { charset: options ?? "utf-8", contentTypeEncoder: (0, ral_1.default)().applicationJson.encoder };
          } else {
            return { charset: options.charset ?? "utf-8", contentEncoder: options.contentEncoder, contentTypeEncoder: options.contentTypeEncoder ?? (0, ral_1.default)().applicationJson.encoder };
          }
        }
        ResolvedMessageWriterOptions2.fromOptions = fromOptions;
      })(ResolvedMessageWriterOptions || (ResolvedMessageWriterOptions = {}));
      var WriteableStreamMessageWriter = class extends AbstractMessageWriter2 {
        constructor(writable, options) {
          super();
          this.writable = writable;
          this.options = ResolvedMessageWriterOptions.fromOptions(options);
          this.errorCount = 0;
          this.writeSemaphore = new semaphore_1.Semaphore(1);
          this.writable.onError((error) => this.fireError(error));
          this.writable.onClose(() => this.fireClose());
        }
        async write(msg) {
          return this.writeSemaphore.lock(async () => {
            const payload = this.options.contentTypeEncoder.encode(msg, this.options).then((buffer) => {
              if (this.options.contentEncoder !== void 0) {
                return this.options.contentEncoder.encode(buffer);
              } else {
                return buffer;
              }
            });
            return payload.then((buffer) => {
              const headers = [];
              headers.push(ContentLength, buffer.byteLength.toString(), CRLF);
              headers.push(CRLF);
              return this.doWrite(msg, headers, buffer);
            }, (error) => {
              this.fireError(error);
              throw error;
            });
          });
        }
        async doWrite(msg, headers, data) {
          try {
            await this.writable.write(headers.join(""), "ascii");
            return this.writable.write(data);
          } catch (error) {
            this.handleError(error, msg);
            return Promise.reject(error);
          }
        }
        handleError(error, msg) {
          this.errorCount++;
          this.fireError(error, msg, this.errorCount);
        }
        end() {
          this.writable.end();
        }
      };
      exports.WriteableStreamMessageWriter = WriteableStreamMessageWriter;
    }
  });

  // node_modules/vscode-jsonrpc/lib/common/messageBuffer.js
  var require_messageBuffer = __commonJS({
    "node_modules/vscode-jsonrpc/lib/common/messageBuffer.js"(exports) {
      "use strict";
      Object.defineProperty(exports, "__esModule", { value: true });
      exports.AbstractMessageBuffer = void 0;
      var CR = 13;
      var LF = 10;
      var CRLF = "\r\n";
      var AbstractMessageBuffer = class {
        constructor(encoding = "utf-8") {
          this._encoding = encoding;
          this._chunks = [];
          this._totalLength = 0;
        }
        get encoding() {
          return this._encoding;
        }
        append(chunk) {
          const toAppend = typeof chunk === "string" ? this.fromString(chunk, this._encoding) : chunk;
          this._chunks.push(toAppend);
          this._totalLength += toAppend.byteLength;
        }
        tryReadHeaders(lowerCaseKeys = false) {
          if (this._chunks.length === 0) {
            return void 0;
          }
          let state = 0;
          let chunkIndex = 0;
          let offset = 0;
          let chunkBytesRead = 0;
          row: while (chunkIndex < this._chunks.length) {
            const chunk = this._chunks[chunkIndex];
            offset = 0;
            column: while (offset < chunk.length) {
              const value = chunk[offset];
              switch (value) {
                case CR:
                  switch (state) {
                    case 0:
                      state = 1;
                      break;
                    case 2:
                      state = 3;
                      break;
                    default:
                      state = 0;
                  }
                  break;
                case LF:
                  switch (state) {
                    case 1:
                      state = 2;
                      break;
                    case 3:
                      state = 4;
                      offset++;
                      break row;
                    default:
                      state = 0;
                  }
                  break;
                default:
                  state = 0;
              }
              offset++;
            }
            chunkBytesRead += chunk.byteLength;
            chunkIndex++;
          }
          if (state !== 4) {
            return void 0;
          }
          const buffer = this._read(chunkBytesRead + offset);
          const result = /* @__PURE__ */ new Map();
          const headers = this.toString(buffer, "ascii").split(CRLF);
          if (headers.length < 2) {
            return result;
          }
          for (let i = 0; i < headers.length - 2; i++) {
            const header = headers[i];
            const index = header.indexOf(":");
            if (index === -1) {
              throw new Error(`Message header must separate key and value using ':'
${header}`);
            }
            const key = header.substr(0, index);
            const value = header.substr(index + 1).trim();
            result.set(lowerCaseKeys ? key.toLowerCase() : key, value);
          }
          return result;
        }
        tryReadBody(length) {
          if (this._totalLength < length) {
            return void 0;
          }
          return this._read(length);
        }
        get numberOfBytes() {
          return this._totalLength;
        }
        _read(byteCount) {
          if (byteCount === 0) {
            return this.emptyBuffer();
          }
          if (byteCount > this._totalLength) {
            throw new Error(`Cannot read so many bytes!`);
          }
          if (this._chunks[0].byteLength === byteCount) {
            const chunk = this._chunks[0];
            this._chunks.shift();
            this._totalLength -= byteCount;
            return this.asNative(chunk);
          }
          if (this._chunks[0].byteLength > byteCount) {
            const chunk = this._chunks[0];
            const result2 = this.asNative(chunk, byteCount);
            this._chunks[0] = chunk.slice(byteCount);
            this._totalLength -= byteCount;
            return result2;
          }
          const result = this.allocNative(byteCount);
          let resultOffset = 0;
          let chunkIndex = 0;
          while (byteCount > 0) {
            const chunk = this._chunks[chunkIndex];
            if (chunk.byteLength > byteCount) {
              const chunkPart = chunk.slice(0, byteCount);
              result.set(chunkPart, resultOffset);
              resultOffset += byteCount;
              this._chunks[chunkIndex] = chunk.slice(byteCount);
              this._totalLength -= byteCount;
              byteCount -= byteCount;
            } else {
              result.set(chunk, resultOffset);
              resultOffset += chunk.byteLength;
              this._chunks.shift();
              this._totalLength -= chunk.byteLength;
              byteCount -= chunk.byteLength;
            }
          }
          return result;
        }
      };
      exports.AbstractMessageBuffer = AbstractMessageBuffer;
    }
  });

  // node_modules/vscode-jsonrpc/lib/common/connection.js
  var require_connection = __commonJS({
    "node_modules/vscode-jsonrpc/lib/common/connection.js"(exports) {
      "use strict";
      Object.defineProperty(exports, "__esModule", { value: true });
      exports.createMessageConnection = exports.ConnectionOptions = exports.MessageStrategy = exports.CancellationStrategy = exports.CancellationSenderStrategy = exports.CancellationReceiverStrategy = exports.RequestCancellationReceiverStrategy = exports.IdCancellationReceiverStrategy = exports.ConnectionStrategy = exports.ConnectionError = exports.ConnectionErrors = exports.LogTraceNotification = exports.SetTraceNotification = exports.TraceFormat = exports.TraceValues = exports.Trace = exports.NullLogger = exports.ProgressType = exports.ProgressToken = void 0;
      var ral_1 = require_ral();
      var Is2 = require_is();
      var messages_1 = require_messages();
      var linkedMap_1 = require_linkedMap();
      var events_1 = require_events();
      var cancellation_1 = require_cancellation();
      var CancelNotification;
      (function(CancelNotification2) {
        CancelNotification2.type = new messages_1.NotificationType("$/cancelRequest");
      })(CancelNotification || (CancelNotification = {}));
      var ProgressToken;
      (function(ProgressToken2) {
        function is(value) {
          return typeof value === "string" || typeof value === "number";
        }
        ProgressToken2.is = is;
      })(ProgressToken || (exports.ProgressToken = ProgressToken = {}));
      var ProgressNotification;
      (function(ProgressNotification2) {
        ProgressNotification2.type = new messages_1.NotificationType("$/progress");
      })(ProgressNotification || (ProgressNotification = {}));
      var ProgressType = class {
        constructor() {
        }
      };
      exports.ProgressType = ProgressType;
      var StarRequestHandler;
      (function(StarRequestHandler2) {
        function is(value) {
          return Is2.func(value);
        }
        StarRequestHandler2.is = is;
      })(StarRequestHandler || (StarRequestHandler = {}));
      exports.NullLogger = Object.freeze({
        error: () => {
        },
        warn: () => {
        },
        info: () => {
        },
        log: () => {
        }
      });
      var Trace;
      (function(Trace2) {
        Trace2[Trace2["Off"] = 0] = "Off";
        Trace2[Trace2["Messages"] = 1] = "Messages";
        Trace2[Trace2["Compact"] = 2] = "Compact";
        Trace2[Trace2["Verbose"] = 3] = "Verbose";
      })(Trace || (exports.Trace = Trace = {}));
      var TraceValues;
      (function(TraceValues2) {
        TraceValues2.Off = "off";
        TraceValues2.Messages = "messages";
        TraceValues2.Compact = "compact";
        TraceValues2.Verbose = "verbose";
      })(TraceValues || (exports.TraceValues = TraceValues = {}));
      (function(Trace2) {
        function fromString(value) {
          if (!Is2.string(value)) {
            return Trace2.Off;
          }
          value = value.toLowerCase();
          switch (value) {
            case "off":
              return Trace2.Off;
            case "messages":
              return Trace2.Messages;
            case "compact":
              return Trace2.Compact;
            case "verbose":
              return Trace2.Verbose;
            default:
              return Trace2.Off;
          }
        }
        Trace2.fromString = fromString;
        function toString(value) {
          switch (value) {
            case Trace2.Off:
              return "off";
            case Trace2.Messages:
              return "messages";
            case Trace2.Compact:
              return "compact";
            case Trace2.Verbose:
              return "verbose";
            default:
              return "off";
          }
        }
        Trace2.toString = toString;
      })(Trace || (exports.Trace = Trace = {}));
      var TraceFormat;
      (function(TraceFormat2) {
        TraceFormat2["Text"] = "text";
        TraceFormat2["JSON"] = "json";
      })(TraceFormat || (exports.TraceFormat = TraceFormat = {}));
      (function(TraceFormat2) {
        function fromString(value) {
          if (!Is2.string(value)) {
            return TraceFormat2.Text;
          }
          value = value.toLowerCase();
          if (value === "json") {
            return TraceFormat2.JSON;
          } else {
            return TraceFormat2.Text;
          }
        }
        TraceFormat2.fromString = fromString;
      })(TraceFormat || (exports.TraceFormat = TraceFormat = {}));
      var SetTraceNotification;
      (function(SetTraceNotification2) {
        SetTraceNotification2.type = new messages_1.NotificationType("$/setTrace");
      })(SetTraceNotification || (exports.SetTraceNotification = SetTraceNotification = {}));
      var LogTraceNotification;
      (function(LogTraceNotification2) {
        LogTraceNotification2.type = new messages_1.NotificationType("$/logTrace");
      })(LogTraceNotification || (exports.LogTraceNotification = LogTraceNotification = {}));
      var ConnectionErrors;
      (function(ConnectionErrors2) {
        ConnectionErrors2[ConnectionErrors2["Closed"] = 1] = "Closed";
        ConnectionErrors2[ConnectionErrors2["Disposed"] = 2] = "Disposed";
        ConnectionErrors2[ConnectionErrors2["AlreadyListening"] = 3] = "AlreadyListening";
      })(ConnectionErrors || (exports.ConnectionErrors = ConnectionErrors = {}));
      var ConnectionError = class _ConnectionError extends Error {
        constructor(code, message) {
          super(message);
          this.code = code;
          Object.setPrototypeOf(this, _ConnectionError.prototype);
        }
      };
      exports.ConnectionError = ConnectionError;
      var ConnectionStrategy;
      (function(ConnectionStrategy2) {
        function is(value) {
          const candidate = value;
          return candidate && Is2.func(candidate.cancelUndispatched);
        }
        ConnectionStrategy2.is = is;
      })(ConnectionStrategy || (exports.ConnectionStrategy = ConnectionStrategy = {}));
      var IdCancellationReceiverStrategy;
      (function(IdCancellationReceiverStrategy2) {
        function is(value) {
          const candidate = value;
          return candidate && (candidate.kind === void 0 || candidate.kind === "id") && Is2.func(candidate.createCancellationTokenSource) && (candidate.dispose === void 0 || Is2.func(candidate.dispose));
        }
        IdCancellationReceiverStrategy2.is = is;
      })(IdCancellationReceiverStrategy || (exports.IdCancellationReceiverStrategy = IdCancellationReceiverStrategy = {}));
      var RequestCancellationReceiverStrategy;
      (function(RequestCancellationReceiverStrategy2) {
        function is(value) {
          const candidate = value;
          return candidate && candidate.kind === "request" && Is2.func(candidate.createCancellationTokenSource) && (candidate.dispose === void 0 || Is2.func(candidate.dispose));
        }
        RequestCancellationReceiverStrategy2.is = is;
      })(RequestCancellationReceiverStrategy || (exports.RequestCancellationReceiverStrategy = RequestCancellationReceiverStrategy = {}));
      var CancellationReceiverStrategy;
      (function(CancellationReceiverStrategy2) {
        CancellationReceiverStrategy2.Message = Object.freeze({
          createCancellationTokenSource(_) {
            return new cancellation_1.CancellationTokenSource();
          }
        });
        function is(value) {
          return IdCancellationReceiverStrategy.is(value) || RequestCancellationReceiverStrategy.is(value);
        }
        CancellationReceiverStrategy2.is = is;
      })(CancellationReceiverStrategy || (exports.CancellationReceiverStrategy = CancellationReceiverStrategy = {}));
      var CancellationSenderStrategy;
      (function(CancellationSenderStrategy2) {
        CancellationSenderStrategy2.Message = Object.freeze({
          sendCancellation(conn, id) {
            return conn.sendNotification(CancelNotification.type, { id });
          },
          cleanup(_) {
          }
        });
        function is(value) {
          const candidate = value;
          return candidate && Is2.func(candidate.sendCancellation) && Is2.func(candidate.cleanup);
        }
        CancellationSenderStrategy2.is = is;
      })(CancellationSenderStrategy || (exports.CancellationSenderStrategy = CancellationSenderStrategy = {}));
      var CancellationStrategy;
      (function(CancellationStrategy2) {
        CancellationStrategy2.Message = Object.freeze({
          receiver: CancellationReceiverStrategy.Message,
          sender: CancellationSenderStrategy.Message
        });
        function is(value) {
          const candidate = value;
          return candidate && CancellationReceiverStrategy.is(candidate.receiver) && CancellationSenderStrategy.is(candidate.sender);
        }
        CancellationStrategy2.is = is;
      })(CancellationStrategy || (exports.CancellationStrategy = CancellationStrategy = {}));
      var MessageStrategy;
      (function(MessageStrategy2) {
        function is(value) {
          const candidate = value;
          return candidate && Is2.func(candidate.handleMessage);
        }
        MessageStrategy2.is = is;
      })(MessageStrategy || (exports.MessageStrategy = MessageStrategy = {}));
      var ConnectionOptions;
      (function(ConnectionOptions2) {
        function is(value) {
          const candidate = value;
          return candidate && (CancellationStrategy.is(candidate.cancellationStrategy) || ConnectionStrategy.is(candidate.connectionStrategy) || MessageStrategy.is(candidate.messageStrategy));
        }
        ConnectionOptions2.is = is;
      })(ConnectionOptions || (exports.ConnectionOptions = ConnectionOptions = {}));
      var ConnectionState;
      (function(ConnectionState2) {
        ConnectionState2[ConnectionState2["New"] = 1] = "New";
        ConnectionState2[ConnectionState2["Listening"] = 2] = "Listening";
        ConnectionState2[ConnectionState2["Closed"] = 3] = "Closed";
        ConnectionState2[ConnectionState2["Disposed"] = 4] = "Disposed";
      })(ConnectionState || (ConnectionState = {}));
      function createMessageConnection3(messageReader, messageWriter, _logger, options) {
        const logger = _logger !== void 0 ? _logger : exports.NullLogger;
        let sequenceNumber = 0;
        let notificationSequenceNumber = 0;
        let unknownResponseSequenceNumber = 0;
        const version = "2.0";
        let starRequestHandler = void 0;
        const requestHandlers = /* @__PURE__ */ new Map();
        let starNotificationHandler = void 0;
        const notificationHandlers = /* @__PURE__ */ new Map();
        const progressHandlers = /* @__PURE__ */ new Map();
        let timer;
        let messageQueue = new linkedMap_1.LinkedMap();
        let responsePromises = /* @__PURE__ */ new Map();
        let knownCanceledRequests = /* @__PURE__ */ new Set();
        let requestTokens = /* @__PURE__ */ new Map();
        let trace = Trace.Off;
        let traceFormat = TraceFormat.Text;
        let tracer;
        let state = ConnectionState.New;
        const errorEmitter = new events_1.Emitter();
        const closeEmitter = new events_1.Emitter();
        const unhandledNotificationEmitter = new events_1.Emitter();
        const unhandledProgressEmitter = new events_1.Emitter();
        const disposeEmitter = new events_1.Emitter();
        const cancellationStrategy = options && options.cancellationStrategy ? options.cancellationStrategy : CancellationStrategy.Message;
        function createRequestQueueKey(id) {
          if (id === null) {
            throw new Error(`Can't send requests with id null since the response can't be correlated.`);
          }
          return "req-" + id.toString();
        }
        function createResponseQueueKey(id) {
          if (id === null) {
            return "res-unknown-" + (++unknownResponseSequenceNumber).toString();
          } else {
            return "res-" + id.toString();
          }
        }
        function createNotificationQueueKey() {
          return "not-" + (++notificationSequenceNumber).toString();
        }
        function addMessageToQueue(queue, message) {
          if (messages_1.Message.isRequest(message)) {
            queue.set(createRequestQueueKey(message.id), message);
          } else if (messages_1.Message.isResponse(message)) {
            queue.set(createResponseQueueKey(message.id), message);
          } else {
            queue.set(createNotificationQueueKey(), message);
          }
        }
        function cancelUndispatched(_message) {
          return void 0;
        }
        function isListening() {
          return state === ConnectionState.Listening;
        }
        function isClosed() {
          return state === ConnectionState.Closed;
        }
        function isDisposed() {
          return state === ConnectionState.Disposed;
        }
        function closeHandler() {
          if (state === ConnectionState.New || state === ConnectionState.Listening) {
            state = ConnectionState.Closed;
            closeEmitter.fire(void 0);
          }
        }
        function readErrorHandler(error) {
          errorEmitter.fire([error, void 0, void 0]);
        }
        function writeErrorHandler(data) {
          errorEmitter.fire(data);
        }
        messageReader.onClose(closeHandler);
        messageReader.onError(readErrorHandler);
        messageWriter.onClose(closeHandler);
        messageWriter.onError(writeErrorHandler);
        function triggerMessageQueue() {
          if (timer || messageQueue.size === 0) {
            return;
          }
          timer = (0, ral_1.default)().timer.setImmediate(() => {
            timer = void 0;
            processMessageQueue();
          });
        }
        function handleMessage(message) {
          if (messages_1.Message.isRequest(message)) {
            handleRequest(message);
          } else if (messages_1.Message.isNotification(message)) {
            handleNotification(message);
          } else if (messages_1.Message.isResponse(message)) {
            handleResponse(message);
          } else {
            handleInvalidMessage(message);
          }
        }
        function processMessageQueue() {
          if (messageQueue.size === 0) {
            return;
          }
          const message = messageQueue.shift();
          try {
            const messageStrategy = options?.messageStrategy;
            if (MessageStrategy.is(messageStrategy)) {
              messageStrategy.handleMessage(message, handleMessage);
            } else {
              handleMessage(message);
            }
          } finally {
            triggerMessageQueue();
          }
        }
        const callback = (message) => {
          try {
            if (messages_1.Message.isNotification(message) && message.method === CancelNotification.type.method) {
              const cancelId = message.params.id;
              const key = createRequestQueueKey(cancelId);
              const toCancel = messageQueue.get(key);
              if (messages_1.Message.isRequest(toCancel)) {
                const strategy = options?.connectionStrategy;
                const response = strategy && strategy.cancelUndispatched ? strategy.cancelUndispatched(toCancel, cancelUndispatched) : cancelUndispatched(toCancel);
                if (response && (response.error !== void 0 || response.result !== void 0)) {
                  messageQueue.delete(key);
                  requestTokens.delete(cancelId);
                  response.id = toCancel.id;
                  traceSendingResponse(response, message.method, Date.now());
                  messageWriter.write(response).catch(() => logger.error(`Sending response for canceled message failed.`));
                  return;
                }
              }
              const cancellationToken = requestTokens.get(cancelId);
              if (cancellationToken !== void 0) {
                cancellationToken.cancel();
                traceReceivedNotification(message);
                return;
              } else {
                knownCanceledRequests.add(cancelId);
              }
            }
            addMessageToQueue(messageQueue, message);
          } finally {
            triggerMessageQueue();
          }
        };
        function handleRequest(requestMessage) {
          if (isDisposed()) {
            return;
          }
          function reply(resultOrError, method, startTime2) {
            const message = {
              jsonrpc: version,
              id: requestMessage.id
            };
            if (resultOrError instanceof messages_1.ResponseError) {
              message.error = resultOrError.toJson();
            } else {
              message.result = resultOrError === void 0 ? null : resultOrError;
            }
            traceSendingResponse(message, method, startTime2);
            messageWriter.write(message).catch(() => logger.error(`Sending response failed.`));
          }
          function replyError(error, method, startTime2) {
            const message = {
              jsonrpc: version,
              id: requestMessage.id,
              error: error.toJson()
            };
            traceSendingResponse(message, method, startTime2);
            messageWriter.write(message).catch(() => logger.error(`Sending response failed.`));
          }
          function replySuccess(result, method, startTime2) {
            if (result === void 0) {
              result = null;
            }
            const message = {
              jsonrpc: version,
              id: requestMessage.id,
              result
            };
            traceSendingResponse(message, method, startTime2);
            messageWriter.write(message).catch(() => logger.error(`Sending response failed.`));
          }
          traceReceivedRequest(requestMessage);
          const element = requestHandlers.get(requestMessage.method);
          let type;
          let requestHandler;
          if (element) {
            type = element.type;
            requestHandler = element.handler;
          }
          const startTime = Date.now();
          if (requestHandler || starRequestHandler) {
            const tokenKey = requestMessage.id ?? String(Date.now());
            const cancellationSource = IdCancellationReceiverStrategy.is(cancellationStrategy.receiver) ? cancellationStrategy.receiver.createCancellationTokenSource(tokenKey) : cancellationStrategy.receiver.createCancellationTokenSource(requestMessage);
            if (requestMessage.id !== null && knownCanceledRequests.has(requestMessage.id)) {
              cancellationSource.cancel();
            }
            if (requestMessage.id !== null) {
              requestTokens.set(tokenKey, cancellationSource);
            }
            try {
              let handlerResult;
              if (requestHandler) {
                if (requestMessage.params === void 0) {
                  if (type !== void 0 && type.numberOfParams !== 0) {
                    replyError(new messages_1.ResponseError(messages_1.ErrorCodes.InvalidParams, `Request ${requestMessage.method} defines ${type.numberOfParams} params but received none.`), requestMessage.method, startTime);
                    return;
                  }
                  handlerResult = requestHandler(cancellationSource.token);
                } else if (Array.isArray(requestMessage.params)) {
                  if (type !== void 0 && type.parameterStructures === messages_1.ParameterStructures.byName) {
                    replyError(new messages_1.ResponseError(messages_1.ErrorCodes.InvalidParams, `Request ${requestMessage.method} defines parameters by name but received parameters by position`), requestMessage.method, startTime);
                    return;
                  }
                  handlerResult = requestHandler(...requestMessage.params, cancellationSource.token);
                } else {
                  if (type !== void 0 && type.parameterStructures === messages_1.ParameterStructures.byPosition) {
                    replyError(new messages_1.ResponseError(messages_1.ErrorCodes.InvalidParams, `Request ${requestMessage.method} defines parameters by position but received parameters by name`), requestMessage.method, startTime);
                    return;
                  }
                  handlerResult = requestHandler(requestMessage.params, cancellationSource.token);
                }
              } else if (starRequestHandler) {
                handlerResult = starRequestHandler(requestMessage.method, requestMessage.params, cancellationSource.token);
              }
              const promise = handlerResult;
              if (!handlerResult) {
                requestTokens.delete(tokenKey);
                replySuccess(handlerResult, requestMessage.method, startTime);
              } else if (promise.then) {
                promise.then((resultOrError) => {
                  requestTokens.delete(tokenKey);
                  reply(resultOrError, requestMessage.method, startTime);
                }, (error) => {
                  requestTokens.delete(tokenKey);
                  if (error instanceof messages_1.ResponseError) {
                    replyError(error, requestMessage.method, startTime);
                  } else if (error && Is2.string(error.message)) {
                    replyError(new messages_1.ResponseError(messages_1.ErrorCodes.InternalError, `Request ${requestMessage.method} failed with message: ${error.message}`), requestMessage.method, startTime);
                  } else {
                    replyError(new messages_1.ResponseError(messages_1.ErrorCodes.InternalError, `Request ${requestMessage.method} failed unexpectedly without providing any details.`), requestMessage.method, startTime);
                  }
                });
              } else {
                requestTokens.delete(tokenKey);
                reply(handlerResult, requestMessage.method, startTime);
              }
            } catch (error) {
              requestTokens.delete(tokenKey);
              if (error instanceof messages_1.ResponseError) {
                reply(error, requestMessage.method, startTime);
              } else if (error && Is2.string(error.message)) {
                replyError(new messages_1.ResponseError(messages_1.ErrorCodes.InternalError, `Request ${requestMessage.method} failed with message: ${error.message}`), requestMessage.method, startTime);
              } else {
                replyError(new messages_1.ResponseError(messages_1.ErrorCodes.InternalError, `Request ${requestMessage.method} failed unexpectedly without providing any details.`), requestMessage.method, startTime);
              }
            }
          } else {
            replyError(new messages_1.ResponseError(messages_1.ErrorCodes.MethodNotFound, `Unhandled method ${requestMessage.method}`), requestMessage.method, startTime);
          }
        }
        function handleResponse(responseMessage) {
          if (isDisposed()) {
            return;
          }
          if (responseMessage.id === null) {
            if (responseMessage.error) {
              logger.error(`Received response message without id: Error is: 
${JSON.stringify(responseMessage.error, void 0, 4)}`);
            } else {
              logger.error(`Received response message without id. No further error information provided.`);
            }
          } else {
            const key = responseMessage.id;
            const responsePromise = responsePromises.get(key);
            traceReceivedResponse(responseMessage, responsePromise);
            if (responsePromise !== void 0) {
              responsePromises.delete(key);
              try {
                if (responseMessage.error) {
                  const error = responseMessage.error;
                  responsePromise.reject(new messages_1.ResponseError(error.code, error.message, error.data));
                } else if (responseMessage.result !== void 0) {
                  responsePromise.resolve(responseMessage.result);
                } else {
                  throw new Error("Should never happen.");
                }
              } catch (error) {
                if (error.message) {
                  logger.error(`Response handler '${responsePromise.method}' failed with message: ${error.message}`);
                } else {
                  logger.error(`Response handler '${responsePromise.method}' failed unexpectedly.`);
                }
              }
            }
          }
        }
        function handleNotification(message) {
          if (isDisposed()) {
            return;
          }
          let type = void 0;
          let notificationHandler;
          if (message.method === CancelNotification.type.method) {
            const cancelId = message.params.id;
            knownCanceledRequests.delete(cancelId);
            traceReceivedNotification(message);
            return;
          } else {
            const element = notificationHandlers.get(message.method);
            if (element) {
              notificationHandler = element.handler;
              type = element.type;
            }
          }
          if (notificationHandler || starNotificationHandler) {
            try {
              traceReceivedNotification(message);
              if (notificationHandler) {
                if (message.params === void 0) {
                  if (type !== void 0) {
                    if (type.numberOfParams !== 0 && type.parameterStructures !== messages_1.ParameterStructures.byName) {
                      logger.error(`Notification ${message.method} defines ${type.numberOfParams} params but received none.`);
                    }
                  }
                  notificationHandler();
                } else if (Array.isArray(message.params)) {
                  const params = message.params;
                  if (message.method === ProgressNotification.type.method && params.length === 2 && ProgressToken.is(params[0])) {
                    notificationHandler({ token: params[0], value: params[1] });
                  } else {
                    if (type !== void 0) {
                      if (type.parameterStructures === messages_1.ParameterStructures.byName) {
                        logger.error(`Notification ${message.method} defines parameters by name but received parameters by position`);
                      }
                      if (type.numberOfParams !== message.params.length) {
                        logger.error(`Notification ${message.method} defines ${type.numberOfParams} params but received ${params.length} arguments`);
                      }
                    }
                    notificationHandler(...params);
                  }
                } else {
                  if (type !== void 0 && type.parameterStructures === messages_1.ParameterStructures.byPosition) {
                    logger.error(`Notification ${message.method} defines parameters by position but received parameters by name`);
                  }
                  notificationHandler(message.params);
                }
              } else if (starNotificationHandler) {
                starNotificationHandler(message.method, message.params);
              }
            } catch (error) {
              if (error.message) {
                logger.error(`Notification handler '${message.method}' failed with message: ${error.message}`);
              } else {
                logger.error(`Notification handler '${message.method}' failed unexpectedly.`);
              }
            }
          } else {
            unhandledNotificationEmitter.fire(message);
          }
        }
        function handleInvalidMessage(message) {
          if (!message) {
            logger.error("Received empty message.");
            return;
          }
          logger.error(`Received message which is neither a response nor a notification message:
${JSON.stringify(message, null, 4)}`);
          const responseMessage = message;
          if (Is2.string(responseMessage.id) || Is2.number(responseMessage.id)) {
            const key = responseMessage.id;
            const responseHandler = responsePromises.get(key);
            if (responseHandler) {
              responseHandler.reject(new Error("The received response has neither a result nor an error property."));
            }
          }
        }
        function stringifyTrace(params) {
          if (params === void 0 || params === null) {
            return void 0;
          }
          switch (trace) {
            case Trace.Verbose:
              return JSON.stringify(params, null, 4);
            case Trace.Compact:
              return JSON.stringify(params);
            default:
              return void 0;
          }
        }
        function traceSendingRequest(message) {
          if (trace === Trace.Off || !tracer) {
            return;
          }
          if (traceFormat === TraceFormat.Text) {
            let data = void 0;
            if ((trace === Trace.Verbose || trace === Trace.Compact) && message.params) {
              data = `Params: ${stringifyTrace(message.params)}

`;
            }
            tracer.log(`Sending request '${message.method} - (${message.id})'.`, data);
          } else {
            logLSPMessage("send-request", message);
          }
        }
        function traceSendingNotification(message) {
          if (trace === Trace.Off || !tracer) {
            return;
          }
          if (traceFormat === TraceFormat.Text) {
            let data = void 0;
            if (trace === Trace.Verbose || trace === Trace.Compact) {
              if (message.params) {
                data = `Params: ${stringifyTrace(message.params)}

`;
              } else {
                data = "No parameters provided.\n\n";
              }
            }
            tracer.log(`Sending notification '${message.method}'.`, data);
          } else {
            logLSPMessage("send-notification", message);
          }
        }
        function traceSendingResponse(message, method, startTime) {
          if (trace === Trace.Off || !tracer) {
            return;
          }
          if (traceFormat === TraceFormat.Text) {
            let data = void 0;
            if (trace === Trace.Verbose || trace === Trace.Compact) {
              if (message.error && message.error.data) {
                data = `Error data: ${stringifyTrace(message.error.data)}

`;
              } else {
                if (message.result) {
                  data = `Result: ${stringifyTrace(message.result)}

`;
                } else if (message.error === void 0) {
                  data = "No result returned.\n\n";
                }
              }
            }
            tracer.log(`Sending response '${method} - (${message.id})'. Processing request took ${Date.now() - startTime}ms`, data);
          } else {
            logLSPMessage("send-response", message);
          }
        }
        function traceReceivedRequest(message) {
          if (trace === Trace.Off || !tracer) {
            return;
          }
          if (traceFormat === TraceFormat.Text) {
            let data = void 0;
            if ((trace === Trace.Verbose || trace === Trace.Compact) && message.params) {
              data = `Params: ${stringifyTrace(message.params)}

`;
            }
            tracer.log(`Received request '${message.method} - (${message.id})'.`, data);
          } else {
            logLSPMessage("receive-request", message);
          }
        }
        function traceReceivedNotification(message) {
          if (trace === Trace.Off || !tracer || message.method === LogTraceNotification.type.method) {
            return;
          }
          if (traceFormat === TraceFormat.Text) {
            let data = void 0;
            if (trace === Trace.Verbose || trace === Trace.Compact) {
              if (message.params) {
                data = `Params: ${stringifyTrace(message.params)}

`;
              } else {
                data = "No parameters provided.\n\n";
              }
            }
            tracer.log(`Received notification '${message.method}'.`, data);
          } else {
            logLSPMessage("receive-notification", message);
          }
        }
        function traceReceivedResponse(message, responsePromise) {
          if (trace === Trace.Off || !tracer) {
            return;
          }
          if (traceFormat === TraceFormat.Text) {
            let data = void 0;
            if (trace === Trace.Verbose || trace === Trace.Compact) {
              if (message.error && message.error.data) {
                data = `Error data: ${stringifyTrace(message.error.data)}

`;
              } else {
                if (message.result) {
                  data = `Result: ${stringifyTrace(message.result)}

`;
                } else if (message.error === void 0) {
                  data = "No result returned.\n\n";
                }
              }
            }
            if (responsePromise) {
              const error = message.error ? ` Request failed: ${message.error.message} (${message.error.code}).` : "";
              tracer.log(`Received response '${responsePromise.method} - (${message.id})' in ${Date.now() - responsePromise.timerStart}ms.${error}`, data);
            } else {
              tracer.log(`Received response ${message.id} without active response promise.`, data);
            }
          } else {
            logLSPMessage("receive-response", message);
          }
        }
        function logLSPMessage(type, message) {
          if (!tracer || trace === Trace.Off) {
            return;
          }
          const lspMessage = {
            isLSPMessage: true,
            type,
            message,
            timestamp: Date.now()
          };
          tracer.log(lspMessage);
        }
        function throwIfClosedOrDisposed() {
          if (isClosed()) {
            throw new ConnectionError(ConnectionErrors.Closed, "Connection is closed.");
          }
          if (isDisposed()) {
            throw new ConnectionError(ConnectionErrors.Disposed, "Connection is disposed.");
          }
        }
        function throwIfListening() {
          if (isListening()) {
            throw new ConnectionError(ConnectionErrors.AlreadyListening, "Connection is already listening");
          }
        }
        function throwIfNotListening() {
          if (!isListening()) {
            throw new Error("Call listen() first.");
          }
        }
        function undefinedToNull(param) {
          if (param === void 0) {
            return null;
          } else {
            return param;
          }
        }
        function nullToUndefined(param) {
          if (param === null) {
            return void 0;
          } else {
            return param;
          }
        }
        function isNamedParam(param) {
          return param !== void 0 && param !== null && !Array.isArray(param) && typeof param === "object";
        }
        function computeSingleParam(parameterStructures, param) {
          switch (parameterStructures) {
            case messages_1.ParameterStructures.auto:
              if (isNamedParam(param)) {
                return nullToUndefined(param);
              } else {
                return [undefinedToNull(param)];
              }
            case messages_1.ParameterStructures.byName:
              if (!isNamedParam(param)) {
                throw new Error(`Received parameters by name but param is not an object literal.`);
              }
              return nullToUndefined(param);
            case messages_1.ParameterStructures.byPosition:
              return [undefinedToNull(param)];
            default:
              throw new Error(`Unknown parameter structure ${parameterStructures.toString()}`);
          }
        }
        function computeMessageParams(type, params) {
          let result;
          const numberOfParams = type.numberOfParams;
          switch (numberOfParams) {
            case 0:
              result = void 0;
              break;
            case 1:
              result = computeSingleParam(type.parameterStructures, params[0]);
              break;
            default:
              result = [];
              for (let i = 0; i < params.length && i < numberOfParams; i++) {
                result.push(undefinedToNull(params[i]));
              }
              if (params.length < numberOfParams) {
                for (let i = params.length; i < numberOfParams; i++) {
                  result.push(null);
                }
              }
              break;
          }
          return result;
        }
        const connection = {
          sendNotification: (type, ...args) => {
            throwIfClosedOrDisposed();
            let method;
            let messageParams;
            if (Is2.string(type)) {
              method = type;
              const first = args[0];
              let paramStart = 0;
              let parameterStructures = messages_1.ParameterStructures.auto;
              if (messages_1.ParameterStructures.is(first)) {
                paramStart = 1;
                parameterStructures = first;
              }
              let paramEnd = args.length;
              const numberOfParams = paramEnd - paramStart;
              switch (numberOfParams) {
                case 0:
                  messageParams = void 0;
                  break;
                case 1:
                  messageParams = computeSingleParam(parameterStructures, args[paramStart]);
                  break;
                default:
                  if (parameterStructures === messages_1.ParameterStructures.byName) {
                    throw new Error(`Received ${numberOfParams} parameters for 'by Name' notification parameter structure.`);
                  }
                  messageParams = args.slice(paramStart, paramEnd).map((value) => undefinedToNull(value));
                  break;
              }
            } else {
              const params = args;
              method = type.method;
              messageParams = computeMessageParams(type, params);
            }
            const notificationMessage = {
              jsonrpc: version,
              method,
              params: messageParams
            };
            traceSendingNotification(notificationMessage);
            return messageWriter.write(notificationMessage).catch((error) => {
              logger.error(`Sending notification failed.`);
              throw error;
            });
          },
          onNotification: (type, handler) => {
            throwIfClosedOrDisposed();
            let method;
            if (Is2.func(type)) {
              starNotificationHandler = type;
            } else if (handler) {
              if (Is2.string(type)) {
                method = type;
                notificationHandlers.set(type, { type: void 0, handler });
              } else {
                method = type.method;
                notificationHandlers.set(type.method, { type, handler });
              }
            }
            return {
              dispose: () => {
                if (method !== void 0) {
                  notificationHandlers.delete(method);
                } else {
                  starNotificationHandler = void 0;
                }
              }
            };
          },
          onProgress: (_type, token, handler) => {
            if (progressHandlers.has(token)) {
              throw new Error(`Progress handler for token ${token} already registered`);
            }
            progressHandlers.set(token, handler);
            return {
              dispose: () => {
                progressHandlers.delete(token);
              }
            };
          },
          sendProgress: (_type, token, value) => {
            return connection.sendNotification(ProgressNotification.type, { token, value });
          },
          onUnhandledProgress: unhandledProgressEmitter.event,
          sendRequest: (type, ...args) => {
            throwIfClosedOrDisposed();
            throwIfNotListening();
            let method;
            let messageParams;
            let token = void 0;
            if (Is2.string(type)) {
              method = type;
              const first = args[0];
              const last = args[args.length - 1];
              let paramStart = 0;
              let parameterStructures = messages_1.ParameterStructures.auto;
              if (messages_1.ParameterStructures.is(first)) {
                paramStart = 1;
                parameterStructures = first;
              }
              let paramEnd = args.length;
              if (cancellation_1.CancellationToken.is(last)) {
                paramEnd = paramEnd - 1;
                token = last;
              }
              const numberOfParams = paramEnd - paramStart;
              switch (numberOfParams) {
                case 0:
                  messageParams = void 0;
                  break;
                case 1:
                  messageParams = computeSingleParam(parameterStructures, args[paramStart]);
                  break;
                default:
                  if (parameterStructures === messages_1.ParameterStructures.byName) {
                    throw new Error(`Received ${numberOfParams} parameters for 'by Name' request parameter structure.`);
                  }
                  messageParams = args.slice(paramStart, paramEnd).map((value) => undefinedToNull(value));
                  break;
              }
            } else {
              const params = args;
              method = type.method;
              messageParams = computeMessageParams(type, params);
              const numberOfParams = type.numberOfParams;
              token = cancellation_1.CancellationToken.is(params[numberOfParams]) ? params[numberOfParams] : void 0;
            }
            const id = sequenceNumber++;
            let disposable;
            if (token) {
              disposable = token.onCancellationRequested(() => {
                const p = cancellationStrategy.sender.sendCancellation(connection, id);
                if (p === void 0) {
                  logger.log(`Received no promise from cancellation strategy when cancelling id ${id}`);
                  return Promise.resolve();
                } else {
                  return p.catch(() => {
                    logger.log(`Sending cancellation messages for id ${id} failed`);
                  });
                }
              });
            }
            const requestMessage = {
              jsonrpc: version,
              id,
              method,
              params: messageParams
            };
            traceSendingRequest(requestMessage);
            if (typeof cancellationStrategy.sender.enableCancellation === "function") {
              cancellationStrategy.sender.enableCancellation(requestMessage);
            }
            return new Promise(async (resolve, reject) => {
              const resolveWithCleanup = (r) => {
                resolve(r);
                cancellationStrategy.sender.cleanup(id);
                disposable?.dispose();
              };
              const rejectWithCleanup = (r) => {
                reject(r);
                cancellationStrategy.sender.cleanup(id);
                disposable?.dispose();
              };
              const responsePromise = { method, timerStart: Date.now(), resolve: resolveWithCleanup, reject: rejectWithCleanup };
              try {
                responsePromises.set(id, responsePromise);
                await messageWriter.write(requestMessage);
              } catch (error) {
                responsePromises.delete(id);
                responsePromise.reject(new messages_1.ResponseError(messages_1.ErrorCodes.MessageWriteError, error.message ? error.message : "Unknown reason"));
                logger.error(`Sending request failed.`);
                throw error;
              }
            });
          },
          onRequest: (type, handler) => {
            throwIfClosedOrDisposed();
            let method = null;
            if (StarRequestHandler.is(type)) {
              method = void 0;
              starRequestHandler = type;
            } else if (Is2.string(type)) {
              method = null;
              if (handler !== void 0) {
                method = type;
                requestHandlers.set(type, { handler, type: void 0 });
              }
            } else {
              if (handler !== void 0) {
                method = type.method;
                requestHandlers.set(type.method, { type, handler });
              }
            }
            return {
              dispose: () => {
                if (method === null) {
                  return;
                }
                if (method !== void 0) {
                  requestHandlers.delete(method);
                } else {
                  starRequestHandler = void 0;
                }
              }
            };
          },
          hasPendingResponse: () => {
            return responsePromises.size > 0;
          },
          trace: async (_value, _tracer, sendNotificationOrTraceOptions) => {
            let _sendNotification = false;
            let _traceFormat = TraceFormat.Text;
            if (sendNotificationOrTraceOptions !== void 0) {
              if (Is2.boolean(sendNotificationOrTraceOptions)) {
                _sendNotification = sendNotificationOrTraceOptions;
              } else {
                _sendNotification = sendNotificationOrTraceOptions.sendNotification || false;
                _traceFormat = sendNotificationOrTraceOptions.traceFormat || TraceFormat.Text;
              }
            }
            trace = _value;
            traceFormat = _traceFormat;
            if (trace === Trace.Off) {
              tracer = void 0;
            } else {
              tracer = _tracer;
            }
            if (_sendNotification && !isClosed() && !isDisposed()) {
              await connection.sendNotification(SetTraceNotification.type, { value: Trace.toString(_value) });
            }
          },
          onError: errorEmitter.event,
          onClose: closeEmitter.event,
          onUnhandledNotification: unhandledNotificationEmitter.event,
          onDispose: disposeEmitter.event,
          end: () => {
            messageWriter.end();
          },
          dispose: () => {
            if (isDisposed()) {
              return;
            }
            state = ConnectionState.Disposed;
            disposeEmitter.fire(void 0);
            const error = new messages_1.ResponseError(messages_1.ErrorCodes.PendingResponseRejected, "Pending response rejected since connection got disposed");
            for (const promise of responsePromises.values()) {
              promise.reject(error);
            }
            responsePromises = /* @__PURE__ */ new Map();
            requestTokens = /* @__PURE__ */ new Map();
            knownCanceledRequests = /* @__PURE__ */ new Set();
            messageQueue = new linkedMap_1.LinkedMap();
            if (Is2.func(messageWriter.dispose)) {
              messageWriter.dispose();
            }
            if (Is2.func(messageReader.dispose)) {
              messageReader.dispose();
            }
          },
          listen: () => {
            throwIfClosedOrDisposed();
            throwIfListening();
            state = ConnectionState.Listening;
            messageReader.listen(callback);
          },
          inspect: () => {
            (0, ral_1.default)().console.log("inspect");
          }
        };
        connection.onNotification(LogTraceNotification.type, (params) => {
          if (trace === Trace.Off || !tracer) {
            return;
          }
          const verbose = trace === Trace.Verbose || trace === Trace.Compact;
          tracer.log(params.message, verbose ? params.verbose : void 0);
        });
        connection.onNotification(ProgressNotification.type, (params) => {
          const handler = progressHandlers.get(params.token);
          if (handler) {
            handler(params.value);
          } else {
            unhandledProgressEmitter.fire(params);
          }
        });
        return connection;
      }
      exports.createMessageConnection = createMessageConnection3;
    }
  });

  // node_modules/vscode-jsonrpc/lib/common/api.js
  var require_api = __commonJS({
    "node_modules/vscode-jsonrpc/lib/common/api.js"(exports) {
      "use strict";
      Object.defineProperty(exports, "__esModule", { value: true });
      exports.ProgressType = exports.ProgressToken = exports.createMessageConnection = exports.NullLogger = exports.ConnectionOptions = exports.ConnectionStrategy = exports.AbstractMessageBuffer = exports.WriteableStreamMessageWriter = exports.AbstractMessageWriter = exports.MessageWriter = exports.ReadableStreamMessageReader = exports.AbstractMessageReader = exports.MessageReader = exports.SharedArrayReceiverStrategy = exports.SharedArraySenderStrategy = exports.CancellationToken = exports.CancellationTokenSource = exports.Emitter = exports.Event = exports.Disposable = exports.LRUCache = exports.Touch = exports.LinkedMap = exports.ParameterStructures = exports.NotificationType9 = exports.NotificationType8 = exports.NotificationType7 = exports.NotificationType6 = exports.NotificationType5 = exports.NotificationType4 = exports.NotificationType3 = exports.NotificationType2 = exports.NotificationType1 = exports.NotificationType0 = exports.NotificationType = exports.ErrorCodes = exports.ResponseError = exports.RequestType9 = exports.RequestType8 = exports.RequestType7 = exports.RequestType6 = exports.RequestType5 = exports.RequestType4 = exports.RequestType3 = exports.RequestType2 = exports.RequestType1 = exports.RequestType0 = exports.RequestType = exports.Message = exports.RAL = void 0;
      exports.MessageStrategy = exports.CancellationStrategy = exports.CancellationSenderStrategy = exports.CancellationReceiverStrategy = exports.ConnectionError = exports.ConnectionErrors = exports.LogTraceNotification = exports.SetTraceNotification = exports.TraceFormat = exports.TraceValues = exports.Trace = void 0;
      var messages_1 = require_messages();
      Object.defineProperty(exports, "Message", { enumerable: true, get: function() {
        return messages_1.Message;
      } });
      Object.defineProperty(exports, "RequestType", { enumerable: true, get: function() {
        return messages_1.RequestType;
      } });
      Object.defineProperty(exports, "RequestType0", { enumerable: true, get: function() {
        return messages_1.RequestType0;
      } });
      Object.defineProperty(exports, "RequestType1", { enumerable: true, get: function() {
        return messages_1.RequestType1;
      } });
      Object.defineProperty(exports, "RequestType2", { enumerable: true, get: function() {
        return messages_1.RequestType2;
      } });
      Object.defineProperty(exports, "RequestType3", { enumerable: true, get: function() {
        return messages_1.RequestType3;
      } });
      Object.defineProperty(exports, "RequestType4", { enumerable: true, get: function() {
        return messages_1.RequestType4;
      } });
      Object.defineProperty(exports, "RequestType5", { enumerable: true, get: function() {
        return messages_1.RequestType5;
      } });
      Object.defineProperty(exports, "RequestType6", { enumerable: true, get: function() {
        return messages_1.RequestType6;
      } });
      Object.defineProperty(exports, "RequestType7", { enumerable: true, get: function() {
        return messages_1.RequestType7;
      } });
      Object.defineProperty(exports, "RequestType8", { enumerable: true, get: function() {
        return messages_1.RequestType8;
      } });
      Object.defineProperty(exports, "RequestType9", { enumerable: true, get: function() {
        return messages_1.RequestType9;
      } });
      Object.defineProperty(exports, "ResponseError", { enumerable: true, get: function() {
        return messages_1.ResponseError;
      } });
      Object.defineProperty(exports, "ErrorCodes", { enumerable: true, get: function() {
        return messages_1.ErrorCodes;
      } });
      Object.defineProperty(exports, "NotificationType", { enumerable: true, get: function() {
        return messages_1.NotificationType;
      } });
      Object.defineProperty(exports, "NotificationType0", { enumerable: true, get: function() {
        return messages_1.NotificationType0;
      } });
      Object.defineProperty(exports, "NotificationType1", { enumerable: true, get: function() {
        return messages_1.NotificationType1;
      } });
      Object.defineProperty(exports, "NotificationType2", { enumerable: true, get: function() {
        return messages_1.NotificationType2;
      } });
      Object.defineProperty(exports, "NotificationType3", { enumerable: true, get: function() {
        return messages_1.NotificationType3;
      } });
      Object.defineProperty(exports, "NotificationType4", { enumerable: true, get: function() {
        return messages_1.NotificationType4;
      } });
      Object.defineProperty(exports, "NotificationType5", { enumerable: true, get: function() {
        return messages_1.NotificationType5;
      } });
      Object.defineProperty(exports, "NotificationType6", { enumerable: true, get: function() {
        return messages_1.NotificationType6;
      } });
      Object.defineProperty(exports, "NotificationType7", { enumerable: true, get: function() {
        return messages_1.NotificationType7;
      } });
      Object.defineProperty(exports, "NotificationType8", { enumerable: true, get: function() {
        return messages_1.NotificationType8;
      } });
      Object.defineProperty(exports, "NotificationType9", { enumerable: true, get: function() {
        return messages_1.NotificationType9;
      } });
      Object.defineProperty(exports, "ParameterStructures", { enumerable: true, get: function() {
        return messages_1.ParameterStructures;
      } });
      var linkedMap_1 = require_linkedMap();
      Object.defineProperty(exports, "LinkedMap", { enumerable: true, get: function() {
        return linkedMap_1.LinkedMap;
      } });
      Object.defineProperty(exports, "LRUCache", { enumerable: true, get: function() {
        return linkedMap_1.LRUCache;
      } });
      Object.defineProperty(exports, "Touch", { enumerable: true, get: function() {
        return linkedMap_1.Touch;
      } });
      var disposable_1 = require_disposable();
      Object.defineProperty(exports, "Disposable", { enumerable: true, get: function() {
        return disposable_1.Disposable;
      } });
      var events_1 = require_events();
      Object.defineProperty(exports, "Event", { enumerable: true, get: function() {
        return events_1.Event;
      } });
      Object.defineProperty(exports, "Emitter", { enumerable: true, get: function() {
        return events_1.Emitter;
      } });
      var cancellation_1 = require_cancellation();
      Object.defineProperty(exports, "CancellationTokenSource", { enumerable: true, get: function() {
        return cancellation_1.CancellationTokenSource;
      } });
      Object.defineProperty(exports, "CancellationToken", { enumerable: true, get: function() {
        return cancellation_1.CancellationToken;
      } });
      var sharedArrayCancellation_1 = require_sharedArrayCancellation();
      Object.defineProperty(exports, "SharedArraySenderStrategy", { enumerable: true, get: function() {
        return sharedArrayCancellation_1.SharedArraySenderStrategy;
      } });
      Object.defineProperty(exports, "SharedArrayReceiverStrategy", { enumerable: true, get: function() {
        return sharedArrayCancellation_1.SharedArrayReceiverStrategy;
      } });
      var messageReader_1 = require_messageReader();
      Object.defineProperty(exports, "MessageReader", { enumerable: true, get: function() {
        return messageReader_1.MessageReader;
      } });
      Object.defineProperty(exports, "AbstractMessageReader", { enumerable: true, get: function() {
        return messageReader_1.AbstractMessageReader;
      } });
      Object.defineProperty(exports, "ReadableStreamMessageReader", { enumerable: true, get: function() {
        return messageReader_1.ReadableStreamMessageReader;
      } });
      var messageWriter_1 = require_messageWriter();
      Object.defineProperty(exports, "MessageWriter", { enumerable: true, get: function() {
        return messageWriter_1.MessageWriter;
      } });
      Object.defineProperty(exports, "AbstractMessageWriter", { enumerable: true, get: function() {
        return messageWriter_1.AbstractMessageWriter;
      } });
      Object.defineProperty(exports, "WriteableStreamMessageWriter", { enumerable: true, get: function() {
        return messageWriter_1.WriteableStreamMessageWriter;
      } });
      var messageBuffer_1 = require_messageBuffer();
      Object.defineProperty(exports, "AbstractMessageBuffer", { enumerable: true, get: function() {
        return messageBuffer_1.AbstractMessageBuffer;
      } });
      var connection_1 = require_connection();
      Object.defineProperty(exports, "ConnectionStrategy", { enumerable: true, get: function() {
        return connection_1.ConnectionStrategy;
      } });
      Object.defineProperty(exports, "ConnectionOptions", { enumerable: true, get: function() {
        return connection_1.ConnectionOptions;
      } });
      Object.defineProperty(exports, "NullLogger", { enumerable: true, get: function() {
        return connection_1.NullLogger;
      } });
      Object.defineProperty(exports, "createMessageConnection", { enumerable: true, get: function() {
        return connection_1.createMessageConnection;
      } });
      Object.defineProperty(exports, "ProgressToken", { enumerable: true, get: function() {
        return connection_1.ProgressToken;
      } });
      Object.defineProperty(exports, "ProgressType", { enumerable: true, get: function() {
        return connection_1.ProgressType;
      } });
      Object.defineProperty(exports, "Trace", { enumerable: true, get: function() {
        return connection_1.Trace;
      } });
      Object.defineProperty(exports, "TraceValues", { enumerable: true, get: function() {
        return connection_1.TraceValues;
      } });
      Object.defineProperty(exports, "TraceFormat", { enumerable: true, get: function() {
        return connection_1.TraceFormat;
      } });
      Object.defineProperty(exports, "SetTraceNotification", { enumerable: true, get: function() {
        return connection_1.SetTraceNotification;
      } });
      Object.defineProperty(exports, "LogTraceNotification", { enumerable: true, get: function() {
        return connection_1.LogTraceNotification;
      } });
      Object.defineProperty(exports, "ConnectionErrors", { enumerable: true, get: function() {
        return connection_1.ConnectionErrors;
      } });
      Object.defineProperty(exports, "ConnectionError", { enumerable: true, get: function() {
        return connection_1.ConnectionError;
      } });
      Object.defineProperty(exports, "CancellationReceiverStrategy", { enumerable: true, get: function() {
        return connection_1.CancellationReceiverStrategy;
      } });
      Object.defineProperty(exports, "CancellationSenderStrategy", { enumerable: true, get: function() {
        return connection_1.CancellationSenderStrategy;
      } });
      Object.defineProperty(exports, "CancellationStrategy", { enumerable: true, get: function() {
        return connection_1.CancellationStrategy;
      } });
      Object.defineProperty(exports, "MessageStrategy", { enumerable: true, get: function() {
        return connection_1.MessageStrategy;
      } });
      var ral_1 = require_ral();
      exports.RAL = ral_1.default;
    }
  });

  // node_modules/vscode-jsonrpc/lib/browser/ril.js
  var require_ril = __commonJS({
    "node_modules/vscode-jsonrpc/lib/browser/ril.js"(exports) {
      "use strict";
      Object.defineProperty(exports, "__esModule", { value: true });
      var api_1 = require_api();
      var MessageBuffer = class _MessageBuffer extends api_1.AbstractMessageBuffer {
        constructor(encoding = "utf-8") {
          super(encoding);
          this.asciiDecoder = new TextDecoder("ascii");
        }
        emptyBuffer() {
          return _MessageBuffer.emptyBuffer;
        }
        fromString(value, _encoding) {
          return new TextEncoder().encode(value);
        }
        toString(value, encoding) {
          if (encoding === "ascii") {
            return this.asciiDecoder.decode(value);
          } else {
            return new TextDecoder(encoding).decode(value);
          }
        }
        asNative(buffer, length) {
          if (length === void 0) {
            return buffer;
          } else {
            return buffer.slice(0, length);
          }
        }
        allocNative(length) {
          return new Uint8Array(length);
        }
      };
      MessageBuffer.emptyBuffer = new Uint8Array(0);
      var ReadableStreamWrapper = class {
        constructor(socket) {
          this.socket = socket;
          this._onData = new api_1.Emitter();
          this._messageListener = (event) => {
            const blob = event.data;
            blob.arrayBuffer().then((buffer) => {
              this._onData.fire(new Uint8Array(buffer));
            }, () => {
              (0, api_1.RAL)().console.error(`Converting blob to array buffer failed.`);
            });
          };
          this.socket.addEventListener("message", this._messageListener);
        }
        onClose(listener) {
          this.socket.addEventListener("close", listener);
          return api_1.Disposable.create(() => this.socket.removeEventListener("close", listener));
        }
        onError(listener) {
          this.socket.addEventListener("error", listener);
          return api_1.Disposable.create(() => this.socket.removeEventListener("error", listener));
        }
        onEnd(listener) {
          this.socket.addEventListener("end", listener);
          return api_1.Disposable.create(() => this.socket.removeEventListener("end", listener));
        }
        onData(listener) {
          return this._onData.event(listener);
        }
      };
      var WritableStreamWrapper = class {
        constructor(socket) {
          this.socket = socket;
        }
        onClose(listener) {
          this.socket.addEventListener("close", listener);
          return api_1.Disposable.create(() => this.socket.removeEventListener("close", listener));
        }
        onError(listener) {
          this.socket.addEventListener("error", listener);
          return api_1.Disposable.create(() => this.socket.removeEventListener("error", listener));
        }
        onEnd(listener) {
          this.socket.addEventListener("end", listener);
          return api_1.Disposable.create(() => this.socket.removeEventListener("end", listener));
        }
        write(data, encoding) {
          if (typeof data === "string") {
            if (encoding !== void 0 && encoding !== "utf-8") {
              throw new Error(`In a Browser environments only utf-8 text encoding is supported. But got encoding: ${encoding}`);
            }
            this.socket.send(data);
          } else {
            this.socket.send(data);
          }
          return Promise.resolve();
        }
        end() {
          this.socket.close();
        }
      };
      var _textEncoder = new TextEncoder();
      var _ril = Object.freeze({
        messageBuffer: Object.freeze({
          create: (encoding) => new MessageBuffer(encoding)
        }),
        applicationJson: Object.freeze({
          encoder: Object.freeze({
            name: "application/json",
            encode: (msg, options) => {
              if (options.charset !== "utf-8") {
                throw new Error(`In a Browser environments only utf-8 text encoding is supported. But got encoding: ${options.charset}`);
              }
              return Promise.resolve(_textEncoder.encode(JSON.stringify(msg, void 0, 0)));
            }
          }),
          decoder: Object.freeze({
            name: "application/json",
            decode: (buffer, options) => {
              if (!(buffer instanceof Uint8Array)) {
                throw new Error(`In a Browser environments only Uint8Arrays are supported.`);
              }
              return Promise.resolve(JSON.parse(new TextDecoder(options.charset).decode(buffer)));
            }
          })
        }),
        stream: Object.freeze({
          asReadableStream: (socket) => new ReadableStreamWrapper(socket),
          asWritableStream: (socket) => new WritableStreamWrapper(socket)
        }),
        console,
        timer: Object.freeze({
          setTimeout(callback, ms, ...args) {
            const handle = setTimeout(callback, ms, ...args);
            return { dispose: () => clearTimeout(handle) };
          },
          setImmediate(callback, ...args) {
            const handle = setTimeout(callback, 0, ...args);
            return { dispose: () => clearTimeout(handle) };
          },
          setInterval(callback, ms, ...args) {
            const handle = setInterval(callback, ms, ...args);
            return { dispose: () => clearInterval(handle) };
          }
        })
      });
      function RIL() {
        return _ril;
      }
      (function(RIL2) {
        function install() {
          api_1.RAL.install(_ril);
        }
        RIL2.install = install;
      })(RIL || (RIL = {}));
      exports.default = RIL;
    }
  });

  // node_modules/vscode-jsonrpc/lib/browser/main.js
  var require_main = __commonJS({
    "node_modules/vscode-jsonrpc/lib/browser/main.js"(exports) {
      "use strict";
      var __createBinding = exports && exports.__createBinding || (Object.create ? function(o, m2, k, k2) {
        if (k2 === void 0) k2 = k;
        var desc = Object.getOwnPropertyDescriptor(m2, k);
        if (!desc || ("get" in desc ? !m2.__esModule : desc.writable || desc.configurable)) {
          desc = { enumerable: true, get: function() {
            return m2[k];
          } };
        }
        Object.defineProperty(o, k2, desc);
      } : function(o, m2, k, k2) {
        if (k2 === void 0) k2 = k;
        o[k2] = m2[k];
      });
      var __exportStar = exports && exports.__exportStar || function(m2, exports2) {
        for (var p in m2) if (p !== "default" && !Object.prototype.hasOwnProperty.call(exports2, p)) __createBinding(exports2, m2, p);
      };
      Object.defineProperty(exports, "__esModule", { value: true });
      exports.createMessageConnection = exports.BrowserMessageWriter = exports.BrowserMessageReader = void 0;
      var ril_1 = require_ril();
      ril_1.default.install();
      var api_1 = require_api();
      __exportStar(require_api(), exports);
      var BrowserMessageReader = class extends api_1.AbstractMessageReader {
        constructor(port) {
          super();
          this._onData = new api_1.Emitter();
          this._messageListener = (event) => {
            this._onData.fire(event.data);
          };
          port.addEventListener("error", (event) => this.fireError(event));
          port.onmessage = this._messageListener;
        }
        listen(callback) {
          return this._onData.event(callback);
        }
      };
      exports.BrowserMessageReader = BrowserMessageReader;
      var BrowserMessageWriter = class extends api_1.AbstractMessageWriter {
        constructor(port) {
          super();
          this.port = port;
          this.errorCount = 0;
          port.addEventListener("error", (event) => this.fireError(event));
        }
        write(msg) {
          try {
            this.port.postMessage(msg);
            return Promise.resolve();
          } catch (error) {
            this.handleError(error, msg);
            return Promise.reject(error);
          }
        }
        handleError(error, msg) {
          this.errorCount++;
          this.fireError(error, msg, this.errorCount);
        }
        end() {
        }
      };
      exports.BrowserMessageWriter = BrowserMessageWriter;
      function createMessageConnection3(reader, writer, logger, options) {
        if (logger === void 0) {
          logger = api_1.NullLogger;
        }
        if (api_1.ConnectionStrategy.is(options)) {
          options = { connectionStrategy: options };
        }
        return (0, api_1.createMessageConnection)(reader, writer, logger, options);
      }
      exports.createMessageConnection = createMessageConnection3;
    }
  });

  // node_modules/vscode-jsonrpc/browser.js
  var require_browser = __commonJS({
    "node_modules/vscode-jsonrpc/browser.js"(exports, module) {
      "use strict";
      module.exports = require_main();
    }
  });

  // java-lsp-client.ts
  var java_lsp_client_exports = {};
  __export(java_lsp_client_exports, {
    connect: () => connect
  });
  var import_browser = __toESM(require_browser(), 1);

  // node_modules/vscode-ws-jsonrpc/lib/disposable.js
  var import_vscode_jsonrpc = __toESM(require_main(), 1);

  // node_modules/vscode-ws-jsonrpc/lib/socket/socket.js
  var import_vscode_jsonrpc2 = __toESM(require_main(), 1);

  // node_modules/vscode-ws-jsonrpc/lib/socket/reader.js
  var import_vscode_jsonrpc3 = __toESM(require_main(), 1);
  var import_vscode_jsonrpc4 = __toESM(require_main(), 1);
  var WebSocketMessageReader = class extends import_vscode_jsonrpc4.AbstractMessageReader {
    constructor(socket) {
      super();
      __publicField(this, "socket");
      __publicField(this, "state", "initial");
      __publicField(this, "callback");
      // eslint-disable-next-line @typescript-eslint/no-explicit-any
      __publicField(this, "events", []);
      this.socket = socket;
      this.socket.onMessage((message) => this.readMessage(message));
      this.socket.onError((error) => this.fireError(error));
      this.socket.onClose((code, reason) => {
        if (code !== 1e3) {
          const error = {
            name: "" + code,
            message: `Error during socket reconnect: code = ${code}, reason = ${reason}`
          };
          this.fireError(error);
        }
        this.fireClose();
      });
    }
    listen(callback) {
      if (this.state === "initial") {
        this.state = "listening";
        this.callback = callback;
        while (this.events.length !== 0) {
          const event = this.events.pop();
          if (event.message !== void 0) {
            this.readMessage(event.message);
          } else if (event.error !== void 0) {
            this.fireError(event.error);
          } else {
            this.fireClose();
          }
        }
      }
      return {
        dispose: () => {
          if (this.callback === callback) {
            this.state = "initial";
            this.callback = void 0;
          }
        }
      };
    }
    dispose() {
      super.dispose();
      this.state = "initial";
      this.callback = void 0;
      this.events.splice(0, this.events.length);
    }
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    readMessage(message) {
      if (this.state === "initial") {
        this.events.splice(0, 0, { message });
      } else if (this.state === "listening") {
        try {
          const data = JSON.parse(message);
          this.callback(data);
        } catch (err) {
          const error = {
            name: "400",
            // eslint-disable-next-line @typescript-eslint/no-explicit-any
            message: `Error during message parsing, reason = ${typeof err === "object" ? err.message : "unknown"}`
          };
          this.fireError(error);
        }
      }
    }
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    fireError(error) {
      if (this.state === "initial") {
        this.events.splice(0, 0, { error });
      } else if (this.state === "listening") {
        super.fireError(error);
      }
    }
    fireClose() {
      if (this.state === "initial") {
        this.events.splice(0, 0, {});
      } else if (this.state === "listening") {
        super.fireClose();
      }
      this.state = "closed";
    }
  };

  // node_modules/vscode-ws-jsonrpc/lib/socket/writer.js
  var import_vscode_jsonrpc5 = __toESM(require_main(), 1);
  var import_vscode_jsonrpc6 = __toESM(require_main(), 1);
  var WebSocketMessageWriter = class extends import_vscode_jsonrpc6.AbstractMessageWriter {
    constructor(socket) {
      super();
      __publicField(this, "errorCount", 0);
      __publicField(this, "socket");
      this.socket = socket;
    }
    end() {
    }
    async write(msg) {
      try {
        const content = JSON.stringify(msg);
        this.socket.send(content);
      } catch (e) {
        this.errorCount++;
        this.fireError(e, msg, this.errorCount);
      }
    }
  };

  // node_modules/vscode-ws-jsonrpc/lib/socket/connection.js
  var import_vscode_jsonrpc7 = __toESM(require_main(), 1);

  // node_modules/vscode-ws-jsonrpc/lib/connection.js
  function toSocket(webSocket) {
    return {
      send: (content) => webSocket.send(content),
      onMessage: (cb) => {
        webSocket.onmessage = (event) => cb(event.data);
      },
      onError: (cb) => {
        webSocket.onerror = (event) => {
          if (Object.hasOwn(event, "message")) {
            cb(event.message);
          }
        };
      },
      onClose: (cb) => {
        webSocket.onclose = (event) => cb(event.code, event.reason);
      },
      dispose: () => webSocket.close()
    };
  }

  // monaco-shim.ts
  function m() {
    return globalThis.monaco;
  }
  function ns(getter) {
    return new Proxy({}, {
      get: (_, k) => getter()[k]
    });
  }
  function cls(getter) {
    return new Proxy(function() {
    }, {
      construct: (_, args) => new (getter())(...args),
      get: (_, k) => getter()[k]
    });
  }
  var editor = ns(() => m().editor);
  var languages = ns(() => m().languages);
  var MarkerSeverity = ns(() => m().MarkerSeverity);
  var MarkerTag = ns(() => m().MarkerTag);
  var Uri = cls(() => m().Uri);
  var Range = cls(() => m().Range);
  var Position = cls(() => m().Position);
  var Selection = cls(() => m().Selection);
  var KeyCode = ns(() => m().KeyCode);
  var KeyMod = ns(() => m().KeyMod);

  // node_modules/vscode-languageserver-types/lib/esm/main.js
  var DocumentUri;
  (function(DocumentUri2) {
    function is(value) {
      return typeof value === "string";
    }
    DocumentUri2.is = is;
  })(DocumentUri || (DocumentUri = {}));
  var URI;
  (function(URI2) {
    function is(value) {
      return typeof value === "string";
    }
    URI2.is = is;
  })(URI || (URI = {}));
  var integer;
  (function(integer2) {
    integer2.MIN_VALUE = -2147483648;
    integer2.MAX_VALUE = 2147483647;
    function is(value) {
      return typeof value === "number" && integer2.MIN_VALUE <= value && value <= integer2.MAX_VALUE;
    }
    integer2.is = is;
  })(integer || (integer = {}));
  var uinteger;
  (function(uinteger2) {
    uinteger2.MIN_VALUE = 0;
    uinteger2.MAX_VALUE = 2147483647;
    function is(value) {
      return typeof value === "number" && uinteger2.MIN_VALUE <= value && value <= uinteger2.MAX_VALUE;
    }
    uinteger2.is = is;
  })(uinteger || (uinteger = {}));
  var Position2;
  (function(Position3) {
    function create(line, character) {
      if (line === Number.MAX_VALUE) {
        line = uinteger.MAX_VALUE;
      }
      if (character === Number.MAX_VALUE) {
        character = uinteger.MAX_VALUE;
      }
      return { line, character };
    }
    Position3.create = create;
    function is(value) {
      const candidate = value;
      return Is.objectLiteral(candidate) && Is.uinteger(candidate.line) && Is.uinteger(candidate.character);
    }
    Position3.is = is;
  })(Position2 || (Position2 = {}));
  var Range2;
  (function(Range3) {
    function create(one, two, three, four) {
      if (Is.uinteger(one) && Is.uinteger(two) && Is.uinteger(three) && Is.uinteger(four)) {
        return { start: Position2.create(one, two), end: Position2.create(three, four) };
      } else if (Position2.is(one) && Position2.is(two)) {
        return { start: one, end: two };
      } else {
        throw new Error(`Range#create called with invalid arguments[${one}, ${two}, ${three}, ${four}]`);
      }
    }
    Range3.create = create;
    function is(value) {
      const candidate = value;
      return Is.objectLiteral(candidate) && Position2.is(candidate.start) && Position2.is(candidate.end);
    }
    Range3.is = is;
  })(Range2 || (Range2 = {}));
  var Location;
  (function(Location2) {
    function create(uri, range) {
      return { uri, range };
    }
    Location2.create = create;
    function is(value) {
      const candidate = value;
      return Is.objectLiteral(candidate) && Range2.is(candidate.range) && (Is.string(candidate.uri) || Is.undefined(candidate.uri));
    }
    Location2.is = is;
  })(Location || (Location = {}));
  var LocationLink;
  (function(LocationLink2) {
    function create(targetUri, targetRange, targetSelectionRange, originSelectionRange) {
      return { targetUri, targetRange, targetSelectionRange, originSelectionRange };
    }
    LocationLink2.create = create;
    function is(value) {
      const candidate = value;
      return Is.objectLiteral(candidate) && Range2.is(candidate.targetRange) && Is.string(candidate.targetUri) && Range2.is(candidate.targetSelectionRange) && (Range2.is(candidate.originSelectionRange) || Is.undefined(candidate.originSelectionRange));
    }
    LocationLink2.is = is;
  })(LocationLink || (LocationLink = {}));
  var Color;
  (function(Color2) {
    function create(red, green, blue, alpha) {
      return {
        red,
        green,
        blue,
        alpha
      };
    }
    Color2.create = create;
    function is(value) {
      const candidate = value;
      return Is.objectLiteral(candidate) && Is.numberRange(candidate.red, 0, 1) && Is.numberRange(candidate.green, 0, 1) && Is.numberRange(candidate.blue, 0, 1) && Is.numberRange(candidate.alpha, 0, 1);
    }
    Color2.is = is;
  })(Color || (Color = {}));
  var ColorInformation;
  (function(ColorInformation2) {
    function create(range, color) {
      return {
        range,
        color
      };
    }
    ColorInformation2.create = create;
    function is(value) {
      const candidate = value;
      return Is.objectLiteral(candidate) && Range2.is(candidate.range) && Color.is(candidate.color);
    }
    ColorInformation2.is = is;
  })(ColorInformation || (ColorInformation = {}));
  var ColorPresentation;
  (function(ColorPresentation2) {
    function create(label, textEdit, additionalTextEdits) {
      return {
        label,
        textEdit,
        additionalTextEdits
      };
    }
    ColorPresentation2.create = create;
    function is(value) {
      const candidate = value;
      return Is.objectLiteral(candidate) && Is.string(candidate.label) && (Is.undefined(candidate.textEdit) || TextEdit.is(candidate)) && (Is.undefined(candidate.additionalTextEdits) || Is.typedArray(candidate.additionalTextEdits, TextEdit.is));
    }
    ColorPresentation2.is = is;
  })(ColorPresentation || (ColorPresentation = {}));
  var FoldingRangeKind;
  (function(FoldingRangeKind2) {
    FoldingRangeKind2.Comment = "comment";
    FoldingRangeKind2.Imports = "imports";
    FoldingRangeKind2.Region = "region";
  })(FoldingRangeKind || (FoldingRangeKind = {}));
  var FoldingRange;
  (function(FoldingRange2) {
    function create(startLine, endLine, startCharacter, endCharacter, kind, collapsedText) {
      const result = {
        startLine,
        endLine
      };
      if (Is.defined(startCharacter)) {
        result.startCharacter = startCharacter;
      }
      if (Is.defined(endCharacter)) {
        result.endCharacter = endCharacter;
      }
      if (Is.defined(kind)) {
        result.kind = kind;
      }
      if (Is.defined(collapsedText)) {
        result.collapsedText = collapsedText;
      }
      return result;
    }
    FoldingRange2.create = create;
    function is(value) {
      const candidate = value;
      return Is.objectLiteral(candidate) && Is.uinteger(candidate.startLine) && Is.uinteger(candidate.startLine) && (Is.undefined(candidate.startCharacter) || Is.uinteger(candidate.startCharacter)) && (Is.undefined(candidate.endCharacter) || Is.uinteger(candidate.endCharacter)) && (Is.undefined(candidate.kind) || Is.string(candidate.kind));
    }
    FoldingRange2.is = is;
  })(FoldingRange || (FoldingRange = {}));
  var DiagnosticRelatedInformation;
  (function(DiagnosticRelatedInformation2) {
    function create(location2, message) {
      return {
        location: location2,
        message
      };
    }
    DiagnosticRelatedInformation2.create = create;
    function is(value) {
      const candidate = value;
      return Is.defined(candidate) && Location.is(candidate.location) && Is.string(candidate.message);
    }
    DiagnosticRelatedInformation2.is = is;
  })(DiagnosticRelatedInformation || (DiagnosticRelatedInformation = {}));
  var DiagnosticSeverity;
  (function(DiagnosticSeverity2) {
    DiagnosticSeverity2.Error = 1;
    DiagnosticSeverity2.Warning = 2;
    DiagnosticSeverity2.Information = 3;
    DiagnosticSeverity2.Hint = 4;
  })(DiagnosticSeverity || (DiagnosticSeverity = {}));
  var DiagnosticTag;
  (function(DiagnosticTag2) {
    DiagnosticTag2.Unnecessary = 1;
    DiagnosticTag2.Deprecated = 2;
  })(DiagnosticTag || (DiagnosticTag = {}));
  var CodeDescription;
  (function(CodeDescription2) {
    function is(value) {
      const candidate = value;
      return Is.objectLiteral(candidate) && Is.string(candidate.href);
    }
    CodeDescription2.is = is;
  })(CodeDescription || (CodeDescription = {}));
  var Diagnostic;
  (function(Diagnostic2) {
    function create(range, message, severity, code, source, relatedInformation) {
      const result = { range, message };
      if (Is.defined(severity)) {
        result.severity = severity;
      }
      if (Is.defined(code)) {
        result.code = code;
      }
      if (Is.defined(source)) {
        result.source = source;
      }
      if (Is.defined(relatedInformation)) {
        result.relatedInformation = relatedInformation;
      }
      return result;
    }
    Diagnostic2.create = create;
    function is(value) {
      var _a;
      const candidate = value;
      return Is.defined(candidate) && Range2.is(candidate.range) && (Is.string(candidate.message) || MarkupContent.is(candidate.message)) && (Is.number(candidate.severity) || Is.undefined(candidate.severity)) && (Is.integer(candidate.code) || Is.string(candidate.code) || Is.undefined(candidate.code)) && (Is.undefined(candidate.codeDescription) || Is.string((_a = candidate.codeDescription) === null || _a === void 0 ? void 0 : _a.href)) && (Is.string(candidate.source) || Is.undefined(candidate.source)) && (Is.undefined(candidate.relatedInformation) || Is.typedArray(candidate.relatedInformation, DiagnosticRelatedInformation.is));
    }
    Diagnostic2.is = is;
    function is3_17(value) {
      return Is.string(value.message);
    }
    Diagnostic2.is3_17 = is3_17;
    function getMessageString(diagnostic) {
      if (Is.string(diagnostic.message)) {
        return diagnostic.message;
      } else if (MarkupContent.is(diagnostic.message)) {
        return diagnostic.message.value;
      } else {
        throw new Error(`Unknown message type ${typeof diagnostic.message}`);
      }
    }
    Diagnostic2.getMessageString = getMessageString;
  })(Diagnostic || (Diagnostic = {}));
  var Command;
  (function(Command2) {
    function create(title, command, ...args) {
      const result = { title, command };
      if (Is.defined(args) && args.length > 0) {
        result.arguments = args;
      }
      return result;
    }
    Command2.create = create;
    function is(value) {
      const candidate = value;
      return Is.defined(candidate) && Is.string(candidate.title) && (candidate.tooltip === void 0 || Is.string(candidate.tooltip)) && Is.string(candidate.command);
    }
    Command2.is = is;
  })(Command || (Command = {}));
  var TextEdit;
  (function(TextEdit2) {
    function replace(range, newText) {
      return { range, newText };
    }
    TextEdit2.replace = replace;
    function insert(position, newText) {
      return { range: { start: position, end: position }, newText };
    }
    TextEdit2.insert = insert;
    function del(range) {
      return { range, newText: "" };
    }
    TextEdit2.del = del;
    function is(value) {
      const candidate = value;
      return Is.objectLiteral(candidate) && Is.string(candidate.newText) && Range2.is(candidate.range);
    }
    TextEdit2.is = is;
  })(TextEdit || (TextEdit = {}));
  var ChangeAnnotation;
  (function(ChangeAnnotation2) {
    function create(label, needsConfirmation, description) {
      const result = { label };
      if (needsConfirmation !== void 0) {
        result.needsConfirmation = needsConfirmation;
      }
      if (description !== void 0) {
        result.description = description;
      }
      return result;
    }
    ChangeAnnotation2.create = create;
    function is(value) {
      const candidate = value;
      return Is.objectLiteral(candidate) && Is.string(candidate.label) && (Is.boolean(candidate.needsConfirmation) || candidate.needsConfirmation === void 0) && (Is.string(candidate.description) || candidate.description === void 0);
    }
    ChangeAnnotation2.is = is;
  })(ChangeAnnotation || (ChangeAnnotation = {}));
  var ChangeAnnotationIdentifier;
  (function(ChangeAnnotationIdentifier2) {
    function is(value) {
      const candidate = value;
      return Is.string(candidate);
    }
    ChangeAnnotationIdentifier2.is = is;
  })(ChangeAnnotationIdentifier || (ChangeAnnotationIdentifier = {}));
  var AnnotatedTextEdit;
  (function(AnnotatedTextEdit2) {
    function replace(range, newText, annotation) {
      return { range, newText, annotationId: annotation };
    }
    AnnotatedTextEdit2.replace = replace;
    function insert(position, newText, annotation) {
      return { range: { start: position, end: position }, newText, annotationId: annotation };
    }
    AnnotatedTextEdit2.insert = insert;
    function del(range, annotation) {
      return { range, newText: "", annotationId: annotation };
    }
    AnnotatedTextEdit2.del = del;
    function is(value) {
      const candidate = value;
      return TextEdit.is(candidate) && (ChangeAnnotation.is(candidate.annotationId) || ChangeAnnotationIdentifier.is(candidate.annotationId));
    }
    AnnotatedTextEdit2.is = is;
  })(AnnotatedTextEdit || (AnnotatedTextEdit = {}));
  var TextDocumentEdit;
  (function(TextDocumentEdit2) {
    function create(textDocument, edits) {
      return { textDocument, edits };
    }
    TextDocumentEdit2.create = create;
    function is(value) {
      const candidate = value;
      return Is.defined(candidate) && OptionalVersionedTextDocumentIdentifier.is(candidate.textDocument) && Array.isArray(candidate.edits);
    }
    TextDocumentEdit2.is = is;
  })(TextDocumentEdit || (TextDocumentEdit = {}));
  var CreateFile;
  (function(CreateFile2) {
    function create(uri, options, annotation) {
      const result = {
        kind: "create",
        uri
      };
      if (options !== void 0 && (options.overwrite !== void 0 || options.ignoreIfExists !== void 0)) {
        result.options = options;
      }
      if (annotation !== void 0) {
        result.annotationId = annotation;
      }
      return result;
    }
    CreateFile2.create = create;
    function is(value) {
      const candidate = value;
      return candidate && candidate.kind === "create" && Is.string(candidate.uri) && (candidate.options === void 0 || (candidate.options.overwrite === void 0 || Is.boolean(candidate.options.overwrite)) && (candidate.options.ignoreIfExists === void 0 || Is.boolean(candidate.options.ignoreIfExists))) && (candidate.annotationId === void 0 || ChangeAnnotationIdentifier.is(candidate.annotationId));
    }
    CreateFile2.is = is;
  })(CreateFile || (CreateFile = {}));
  var RenameFile;
  (function(RenameFile2) {
    function create(oldUri, newUri, options, annotation) {
      const result = {
        kind: "rename",
        oldUri,
        newUri
      };
      if (options !== void 0 && (options.overwrite !== void 0 || options.ignoreIfExists !== void 0)) {
        result.options = options;
      }
      if (annotation !== void 0) {
        result.annotationId = annotation;
      }
      return result;
    }
    RenameFile2.create = create;
    function is(value) {
      const candidate = value;
      return candidate && candidate.kind === "rename" && Is.string(candidate.oldUri) && Is.string(candidate.newUri) && (candidate.options === void 0 || (candidate.options.overwrite === void 0 || Is.boolean(candidate.options.overwrite)) && (candidate.options.ignoreIfExists === void 0 || Is.boolean(candidate.options.ignoreIfExists))) && (candidate.annotationId === void 0 || ChangeAnnotationIdentifier.is(candidate.annotationId));
    }
    RenameFile2.is = is;
  })(RenameFile || (RenameFile = {}));
  var DeleteFile;
  (function(DeleteFile2) {
    function create(uri, options, annotation) {
      const result = {
        kind: "delete",
        uri
      };
      if (options !== void 0 && (options.recursive !== void 0 || options.ignoreIfNotExists !== void 0)) {
        result.options = options;
      }
      if (annotation !== void 0) {
        result.annotationId = annotation;
      }
      return result;
    }
    DeleteFile2.create = create;
    function is(value) {
      const candidate = value;
      return candidate && candidate.kind === "delete" && Is.string(candidate.uri) && (candidate.options === void 0 || (candidate.options.recursive === void 0 || Is.boolean(candidate.options.recursive)) && (candidate.options.ignoreIfNotExists === void 0 || Is.boolean(candidate.options.ignoreIfNotExists))) && (candidate.annotationId === void 0 || ChangeAnnotationIdentifier.is(candidate.annotationId));
    }
    DeleteFile2.is = is;
  })(DeleteFile || (DeleteFile = {}));
  var WorkspaceEdit;
  (function(WorkspaceEdit2) {
    function is(value) {
      const candidate = value;
      return candidate && (candidate.changes !== void 0 || candidate.documentChanges !== void 0) && (candidate.documentChanges === void 0 || candidate.documentChanges.every((change) => {
        if (Is.string(change.kind)) {
          return CreateFile.is(change) || RenameFile.is(change) || DeleteFile.is(change);
        } else {
          return TextDocumentEdit.is(change);
        }
      }));
    }
    WorkspaceEdit2.is = is;
  })(WorkspaceEdit || (WorkspaceEdit = {}));
  var SnippetTextEdit;
  (function(SnippetTextEdit2) {
    function is(value) {
      const candidate = value;
      return Is.objectLiteral(candidate) && Range2.is(candidate.range) && StringValue.isSnippet(candidate.snippet) && (candidate.annotationId === void 0 || (ChangeAnnotation.is(candidate.annotationId) || ChangeAnnotationIdentifier.is(candidate.annotationId)));
    }
    SnippetTextEdit2.is = is;
  })(SnippetTextEdit || (SnippetTextEdit = {}));
  var TextDocumentIdentifier;
  (function(TextDocumentIdentifier2) {
    function create(uri) {
      return { uri };
    }
    TextDocumentIdentifier2.create = create;
    function is(value) {
      const candidate = value;
      return Is.defined(candidate) && Is.string(candidate.uri);
    }
    TextDocumentIdentifier2.is = is;
  })(TextDocumentIdentifier || (TextDocumentIdentifier = {}));
  var VersionedTextDocumentIdentifier;
  (function(VersionedTextDocumentIdentifier2) {
    function create(uri, version) {
      return { uri, version };
    }
    VersionedTextDocumentIdentifier2.create = create;
    function is(value) {
      const candidate = value;
      return Is.defined(candidate) && Is.string(candidate.uri) && Is.integer(candidate.version);
    }
    VersionedTextDocumentIdentifier2.is = is;
  })(VersionedTextDocumentIdentifier || (VersionedTextDocumentIdentifier = {}));
  var OptionalVersionedTextDocumentIdentifier;
  (function(OptionalVersionedTextDocumentIdentifier2) {
    function create(uri, version) {
      return { uri, version };
    }
    OptionalVersionedTextDocumentIdentifier2.create = create;
    function is(value) {
      const candidate = value;
      return Is.defined(candidate) && Is.string(candidate.uri) && (candidate.version === null || Is.integer(candidate.version));
    }
    OptionalVersionedTextDocumentIdentifier2.is = is;
  })(OptionalVersionedTextDocumentIdentifier || (OptionalVersionedTextDocumentIdentifier = {}));
  var LanguageKind;
  (function(LanguageKind2) {
    LanguageKind2.ABAP = "abap";
    LanguageKind2.WindowsBat = "bat";
    LanguageKind2.BibTeX = "bibtex";
    LanguageKind2.Clojure = "clojure";
    LanguageKind2.Coffeescript = "coffeescript";
    LanguageKind2.C = "c";
    LanguageKind2.CPP = "cpp";
    LanguageKind2.CSharp = "csharp";
    LanguageKind2.CSS = "css";
    LanguageKind2.D = "d";
    LanguageKind2.Delphi = "pascal";
    LanguageKind2.Diff = "diff";
    LanguageKind2.Dart = "dart";
    LanguageKind2.Dockerfile = "dockerfile";
    LanguageKind2.Elixir = "elixir";
    LanguageKind2.Erlang = "erlang";
    LanguageKind2.FSharp = "fsharp";
    LanguageKind2.GitCommit = "git-commit";
    LanguageKind2.GitRebase = "git-rebase";
    LanguageKind2.Go = "go";
    LanguageKind2.Groovy = "groovy";
    LanguageKind2.Handlebars = "handlebars";
    LanguageKind2.Haskell = "haskell";
    LanguageKind2.HTML = "html";
    LanguageKind2.Ini = "ini";
    LanguageKind2.Java = "java";
    LanguageKind2.JavaScript = "javascript";
    LanguageKind2.JavaScriptReact = "javascriptreact";
    LanguageKind2.JSON = "json";
    LanguageKind2.LaTeX = "latex";
    LanguageKind2.Less = "less";
    LanguageKind2.Lua = "lua";
    LanguageKind2.Makefile = "makefile";
    LanguageKind2.Markdown = "markdown";
    LanguageKind2.ObjectiveC = "objective-c";
    LanguageKind2.ObjectiveCPP = "objective-cpp";
    LanguageKind2.Pascal = "pascal";
    LanguageKind2.Perl = "perl";
    LanguageKind2.Perl6 = "perl6";
    LanguageKind2.PHP = "php";
    LanguageKind2.Plaintext = "plaintext";
    LanguageKind2.Powershell = "powershell";
    LanguageKind2.Pug = "jade";
    LanguageKind2.Python = "python";
    LanguageKind2.R = "r";
    LanguageKind2.Razor = "razor";
    LanguageKind2.Ruby = "ruby";
    LanguageKind2.Rust = "rust";
    LanguageKind2.SCSS = "scss";
    LanguageKind2.SASS = "sass";
    LanguageKind2.Scala = "scala";
    LanguageKind2.ShaderLab = "shaderlab";
    LanguageKind2.ShellScript = "shellscript";
    LanguageKind2.SQL = "sql";
    LanguageKind2.Swift = "swift";
    LanguageKind2.TypeScript = "typescript";
    LanguageKind2.TypeScriptReact = "typescriptreact";
    LanguageKind2.TeX = "tex";
    LanguageKind2.VisualBasic = "vb";
    LanguageKind2.XML = "xml";
    LanguageKind2.XSL = "xsl";
    LanguageKind2.YAML = "yaml";
  })(LanguageKind || (LanguageKind = {}));
  var TextDocumentItem;
  (function(TextDocumentItem2) {
    function create(uri, languageId, version, text) {
      return { uri, languageId, version, text };
    }
    TextDocumentItem2.create = create;
    function is(value) {
      const candidate = value;
      return Is.defined(candidate) && Is.string(candidate.uri) && Is.string(candidate.languageId) && Is.integer(candidate.version) && Is.string(candidate.text);
    }
    TextDocumentItem2.is = is;
  })(TextDocumentItem || (TextDocumentItem = {}));
  var MarkupKind;
  (function(MarkupKind3) {
    MarkupKind3.PlainText = "plaintext";
    MarkupKind3.Markdown = "markdown";
    function is(value) {
      const candidate = value;
      return candidate === MarkupKind3.PlainText || candidate === MarkupKind3.Markdown;
    }
    MarkupKind3.is = is;
  })(MarkupKind || (MarkupKind = {}));
  var MarkupContent;
  (function(MarkupContent3) {
    function is(value) {
      const candidate = value;
      return Is.objectLiteral(value) && MarkupKind.is(candidate.kind) && Is.string(candidate.value);
    }
    MarkupContent3.is = is;
  })(MarkupContent || (MarkupContent = {}));
  var CompletionItemKind;
  (function(CompletionItemKind2) {
    CompletionItemKind2.Text = 1;
    CompletionItemKind2.Method = 2;
    CompletionItemKind2.Function = 3;
    CompletionItemKind2.Constructor = 4;
    CompletionItemKind2.Field = 5;
    CompletionItemKind2.Variable = 6;
    CompletionItemKind2.Class = 7;
    CompletionItemKind2.Interface = 8;
    CompletionItemKind2.Module = 9;
    CompletionItemKind2.Property = 10;
    CompletionItemKind2.Unit = 11;
    CompletionItemKind2.Value = 12;
    CompletionItemKind2.Enum = 13;
    CompletionItemKind2.Keyword = 14;
    CompletionItemKind2.Snippet = 15;
    CompletionItemKind2.Color = 16;
    CompletionItemKind2.File = 17;
    CompletionItemKind2.Reference = 18;
    CompletionItemKind2.Folder = 19;
    CompletionItemKind2.EnumMember = 20;
    CompletionItemKind2.Constant = 21;
    CompletionItemKind2.Struct = 22;
    CompletionItemKind2.Event = 23;
    CompletionItemKind2.Operator = 24;
    CompletionItemKind2.TypeParameter = 25;
  })(CompletionItemKind || (CompletionItemKind = {}));
  var InsertTextFormat;
  (function(InsertTextFormat2) {
    InsertTextFormat2.PlainText = 1;
    InsertTextFormat2.Snippet = 2;
  })(InsertTextFormat || (InsertTextFormat = {}));
  var CompletionItemTag;
  (function(CompletionItemTag2) {
    CompletionItemTag2.Deprecated = 1;
  })(CompletionItemTag || (CompletionItemTag = {}));
  var InsertReplaceEdit;
  (function(InsertReplaceEdit2) {
    function create(newText, insert, replace) {
      return { newText, insert, replace };
    }
    InsertReplaceEdit2.create = create;
    function is(value) {
      const candidate = value;
      return candidate && Is.string(candidate.newText) && Range2.is(candidate.insert) && Range2.is(candidate.replace);
    }
    InsertReplaceEdit2.is = is;
  })(InsertReplaceEdit || (InsertReplaceEdit = {}));
  var InsertTextMode;
  (function(InsertTextMode2) {
    InsertTextMode2.asIs = 1;
    InsertTextMode2.adjustIndentation = 2;
  })(InsertTextMode || (InsertTextMode = {}));
  var ApplyKind;
  (function(ApplyKind2) {
    ApplyKind2.Replace = 1;
    ApplyKind2.Merge = 2;
  })(ApplyKind || (ApplyKind = {}));
  var CompletionItemLabelDetails;
  (function(CompletionItemLabelDetails2) {
    function is(value) {
      const candidate = value;
      return candidate && (Is.string(candidate.detail) || candidate.detail === void 0) && (Is.string(candidate.description) || candidate.description === void 0);
    }
    CompletionItemLabelDetails2.is = is;
  })(CompletionItemLabelDetails || (CompletionItemLabelDetails = {}));
  var CompletionItem;
  (function(CompletionItem2) {
    function create(label) {
      return { label };
    }
    CompletionItem2.create = create;
  })(CompletionItem || (CompletionItem = {}));
  var CompletionList;
  (function(CompletionList2) {
    function create(items, isIncomplete) {
      return { items: items ? items : [], isIncomplete: !!isIncomplete };
    }
    CompletionList2.create = create;
  })(CompletionList || (CompletionList = {}));
  var MarkedString;
  (function(MarkedString2) {
    function fromPlainText(plainText) {
      return plainText.replace(/[\\`*_{}[\]()#+\-.!]/g, "\\$&");
    }
    MarkedString2.fromPlainText = fromPlainText;
    function is(value) {
      const candidate = value;
      return Is.string(candidate) || Is.objectLiteral(candidate) && Is.string(candidate.language) && Is.string(candidate.value);
    }
    MarkedString2.is = is;
  })(MarkedString || (MarkedString = {}));
  var Hover;
  (function(Hover2) {
    function is(value) {
      const candidate = value;
      return !!candidate && Is.objectLiteral(candidate) && (MarkupContent.is(candidate.contents) || MarkedString.is(candidate.contents) || Is.typedArray(candidate.contents, MarkedString.is)) && (value.range === void 0 || Range2.is(value.range));
    }
    Hover2.is = is;
  })(Hover || (Hover = {}));
  var ParameterInformation;
  (function(ParameterInformation2) {
    function create(label, documentation) {
      return documentation ? { label, documentation } : { label };
    }
    ParameterInformation2.create = create;
  })(ParameterInformation || (ParameterInformation = {}));
  var SignatureInformation;
  (function(SignatureInformation2) {
    function create(label, documentation, ...parameters) {
      const result = { label };
      if (Is.defined(documentation)) {
        result.documentation = documentation;
      }
      if (Is.defined(parameters)) {
        result.parameters = parameters;
      } else {
        result.parameters = [];
      }
      return result;
    }
    SignatureInformation2.create = create;
  })(SignatureInformation || (SignatureInformation = {}));
  var DocumentHighlightKind;
  (function(DocumentHighlightKind2) {
    DocumentHighlightKind2.Text = 1;
    DocumentHighlightKind2.Read = 2;
    DocumentHighlightKind2.Write = 3;
  })(DocumentHighlightKind || (DocumentHighlightKind = {}));
  var DocumentHighlight;
  (function(DocumentHighlight2) {
    function create(range, kind) {
      const result = { range };
      if (Is.number(kind)) {
        result.kind = kind;
      }
      return result;
    }
    DocumentHighlight2.create = create;
  })(DocumentHighlight || (DocumentHighlight = {}));
  var SymbolKind;
  (function(SymbolKind2) {
    SymbolKind2.File = 1;
    SymbolKind2.Module = 2;
    SymbolKind2.Namespace = 3;
    SymbolKind2.Package = 4;
    SymbolKind2.Class = 5;
    SymbolKind2.Method = 6;
    SymbolKind2.Property = 7;
    SymbolKind2.Field = 8;
    SymbolKind2.Constructor = 9;
    SymbolKind2.Enum = 10;
    SymbolKind2.Interface = 11;
    SymbolKind2.Function = 12;
    SymbolKind2.Variable = 13;
    SymbolKind2.Constant = 14;
    SymbolKind2.String = 15;
    SymbolKind2.Number = 16;
    SymbolKind2.Boolean = 17;
    SymbolKind2.Array = 18;
    SymbolKind2.Object = 19;
    SymbolKind2.Key = 20;
    SymbolKind2.Null = 21;
    SymbolKind2.EnumMember = 22;
    SymbolKind2.Struct = 23;
    SymbolKind2.Event = 24;
    SymbolKind2.Operator = 25;
    SymbolKind2.TypeParameter = 26;
  })(SymbolKind || (SymbolKind = {}));
  var SymbolTag;
  (function(SymbolTag2) {
    SymbolTag2.Deprecated = 1;
  })(SymbolTag || (SymbolTag = {}));
  var SymbolInformation;
  (function(SymbolInformation2) {
    function create(name, kind, range, uri, containerName) {
      const result = {
        name,
        kind,
        location: { uri, range }
      };
      if (containerName) {
        result.containerName = containerName;
      }
      return result;
    }
    SymbolInformation2.create = create;
  })(SymbolInformation || (SymbolInformation = {}));
  var WorkspaceSymbol;
  (function(WorkspaceSymbol2) {
    function create(name, kind, uri, range) {
      return range !== void 0 ? { name, kind, location: { uri, range } } : { name, kind, location: { uri } };
    }
    WorkspaceSymbol2.create = create;
  })(WorkspaceSymbol || (WorkspaceSymbol = {}));
  var DocumentSymbol;
  (function(DocumentSymbol2) {
    function create(name, detail, kind, range, selectionRange, children) {
      const result = {
        name,
        detail,
        kind,
        range,
        selectionRange
      };
      if (children !== void 0) {
        result.children = children;
      }
      return result;
    }
    DocumentSymbol2.create = create;
    function is(value) {
      const candidate = value;
      return candidate && Is.string(candidate.name) && Is.number(candidate.kind) && Range2.is(candidate.range) && Range2.is(candidate.selectionRange) && (candidate.detail === void 0 || Is.string(candidate.detail)) && (candidate.deprecated === void 0 || Is.boolean(candidate.deprecated)) && (candidate.children === void 0 || Array.isArray(candidate.children)) && (candidate.tags === void 0 || Array.isArray(candidate.tags));
    }
    DocumentSymbol2.is = is;
  })(DocumentSymbol || (DocumentSymbol = {}));
  var CodeActionKind;
  (function(CodeActionKind2) {
    CodeActionKind2.Empty = "";
    CodeActionKind2.QuickFix = "quickfix";
    CodeActionKind2.Refactor = "refactor";
    CodeActionKind2.RefactorExtract = "refactor.extract";
    CodeActionKind2.RefactorInline = "refactor.inline";
    CodeActionKind2.RefactorMove = "refactor.move";
    CodeActionKind2.RefactorRewrite = "refactor.rewrite";
    CodeActionKind2.Source = "source";
    CodeActionKind2.SourceOrganizeImports = "source.organizeImports";
    CodeActionKind2.SourceFixAll = "source.fixAll";
    CodeActionKind2.Notebook = "notebook";
  })(CodeActionKind || (CodeActionKind = {}));
  var CodeActionTriggerKind;
  (function(CodeActionTriggerKind2) {
    CodeActionTriggerKind2.Invoked = 1;
    CodeActionTriggerKind2.Automatic = 2;
  })(CodeActionTriggerKind || (CodeActionTriggerKind = {}));
  var CodeActionContext;
  (function(CodeActionContext2) {
    function create(diagnostics, only, triggerKind) {
      const result = { diagnostics };
      if (only !== void 0 && only !== null) {
        result.only = only;
      }
      if (triggerKind !== void 0 && triggerKind !== null) {
        result.triggerKind = triggerKind;
      }
      return result;
    }
    CodeActionContext2.create = create;
    function is(value) {
      const candidate = value;
      return Is.defined(candidate) && Is.typedArray(candidate.diagnostics, Diagnostic.is) && (candidate.only === void 0 || Is.typedArray(candidate.only, Is.string)) && (candidate.triggerKind === void 0 || candidate.triggerKind === CodeActionTriggerKind.Invoked || candidate.triggerKind === CodeActionTriggerKind.Automatic);
    }
    CodeActionContext2.is = is;
  })(CodeActionContext || (CodeActionContext = {}));
  var CodeActionTag;
  (function(CodeActionTag2) {
    CodeActionTag2.LLMGenerated = 1;
    function is(value) {
      return Is.defined(value) && value === CodeActionTag2.LLMGenerated;
    }
    CodeActionTag2.is = is;
  })(CodeActionTag || (CodeActionTag = {}));
  var CodeAction;
  (function(CodeAction2) {
    function create(title, kindOrCommandOrEdit, kind) {
      const result = { title };
      let checkKind = true;
      if (typeof kindOrCommandOrEdit === "string") {
        checkKind = false;
        result.kind = kindOrCommandOrEdit;
      } else if (Command.is(kindOrCommandOrEdit)) {
        result.command = kindOrCommandOrEdit;
      } else {
        result.edit = kindOrCommandOrEdit;
      }
      if (checkKind && kind !== void 0) {
        result.kind = kind;
      }
      return result;
    }
    CodeAction2.create = create;
    function is(value) {
      const candidate = value;
      return candidate && Is.string(candidate.title) && (candidate.diagnostics === void 0 || Is.typedArray(candidate.diagnostics, Diagnostic.is)) && (candidate.kind === void 0 || Is.string(candidate.kind)) && (candidate.edit !== void 0 || candidate.command !== void 0) && (candidate.command === void 0 || Command.is(candidate.command)) && (candidate.isPreferred === void 0 || Is.boolean(candidate.isPreferred)) && (candidate.edit === void 0 || WorkspaceEdit.is(candidate.edit)) && (candidate.tags === void 0 || Is.typedArray(candidate.tags, CodeActionTag.is));
    }
    CodeAction2.is = is;
  })(CodeAction || (CodeAction = {}));
  var CodeLens;
  (function(CodeLens2) {
    function create(range, data) {
      const result = { range };
      if (Is.defined(data)) {
        result.data = data;
      }
      return result;
    }
    CodeLens2.create = create;
    function is(value) {
      const candidate = value;
      return Is.defined(candidate) && Range2.is(candidate.range) && (Is.undefined(candidate.command) || Command.is(candidate.command));
    }
    CodeLens2.is = is;
  })(CodeLens || (CodeLens = {}));
  var FormattingOptions;
  (function(FormattingOptions2) {
    function create(tabSize, insertSpaces) {
      return { tabSize, insertSpaces };
    }
    FormattingOptions2.create = create;
    function is(value) {
      const candidate = value;
      return Is.defined(candidate) && Is.uinteger(candidate.tabSize) && Is.boolean(candidate.insertSpaces);
    }
    FormattingOptions2.is = is;
  })(FormattingOptions || (FormattingOptions = {}));
  var DocumentLink;
  (function(DocumentLink2) {
    function create(range, target, data) {
      return { range, target, data };
    }
    DocumentLink2.create = create;
    function is(value) {
      const candidate = value;
      return Is.defined(candidate) && Range2.is(candidate.range) && (Is.undefined(candidate.target) || Is.string(candidate.target));
    }
    DocumentLink2.is = is;
  })(DocumentLink || (DocumentLink = {}));
  var SelectionRange;
  (function(SelectionRange2) {
    function create(range, parent) {
      return { range, parent };
    }
    SelectionRange2.create = create;
    function is(value) {
      const candidate = value;
      return Is.objectLiteral(candidate) && Range2.is(candidate.range) && (candidate.parent === void 0 || SelectionRange2.is(candidate.parent));
    }
    SelectionRange2.is = is;
  })(SelectionRange || (SelectionRange = {}));
  var SemanticTokenTypes;
  (function(SemanticTokenTypes2) {
    SemanticTokenTypes2["namespace"] = "namespace";
    SemanticTokenTypes2["type"] = "type";
    SemanticTokenTypes2["class"] = "class";
    SemanticTokenTypes2["enum"] = "enum";
    SemanticTokenTypes2["interface"] = "interface";
    SemanticTokenTypes2["struct"] = "struct";
    SemanticTokenTypes2["typeParameter"] = "typeParameter";
    SemanticTokenTypes2["parameter"] = "parameter";
    SemanticTokenTypes2["variable"] = "variable";
    SemanticTokenTypes2["property"] = "property";
    SemanticTokenTypes2["enumMember"] = "enumMember";
    SemanticTokenTypes2["event"] = "event";
    SemanticTokenTypes2["function"] = "function";
    SemanticTokenTypes2["method"] = "method";
    SemanticTokenTypes2["macro"] = "macro";
    SemanticTokenTypes2["keyword"] = "keyword";
    SemanticTokenTypes2["modifier"] = "modifier";
    SemanticTokenTypes2["comment"] = "comment";
    SemanticTokenTypes2["string"] = "string";
    SemanticTokenTypes2["number"] = "number";
    SemanticTokenTypes2["regexp"] = "regexp";
    SemanticTokenTypes2["operator"] = "operator";
    SemanticTokenTypes2["decorator"] = "decorator";
    SemanticTokenTypes2["label"] = "label";
  })(SemanticTokenTypes || (SemanticTokenTypes = {}));
  var SemanticTokenModifiers;
  (function(SemanticTokenModifiers2) {
    SemanticTokenModifiers2["declaration"] = "declaration";
    SemanticTokenModifiers2["definition"] = "definition";
    SemanticTokenModifiers2["readonly"] = "readonly";
    SemanticTokenModifiers2["static"] = "static";
    SemanticTokenModifiers2["deprecated"] = "deprecated";
    SemanticTokenModifiers2["abstract"] = "abstract";
    SemanticTokenModifiers2["async"] = "async";
    SemanticTokenModifiers2["modification"] = "modification";
    SemanticTokenModifiers2["documentation"] = "documentation";
    SemanticTokenModifiers2["defaultLibrary"] = "defaultLibrary";
  })(SemanticTokenModifiers || (SemanticTokenModifiers = {}));
  var SemanticTokens;
  (function(SemanticTokens2) {
    function is(value) {
      const candidate = value;
      return Is.objectLiteral(candidate) && (candidate.resultId === void 0 || typeof candidate.resultId === "string") && Array.isArray(candidate.data) && (candidate.data.length === 0 || typeof candidate.data[0] === "number");
    }
    SemanticTokens2.is = is;
  })(SemanticTokens || (SemanticTokens = {}));
  var InlineValueText;
  (function(InlineValueText2) {
    function create(range, text) {
      return { range, text };
    }
    InlineValueText2.create = create;
    function is(value) {
      const candidate = value;
      return candidate !== void 0 && candidate !== null && Range2.is(candidate.range) && Is.string(candidate.text);
    }
    InlineValueText2.is = is;
  })(InlineValueText || (InlineValueText = {}));
  var InlineValueVariableLookup;
  (function(InlineValueVariableLookup2) {
    function create(range, variableName, caseSensitiveLookup) {
      return { range, variableName, caseSensitiveLookup };
    }
    InlineValueVariableLookup2.create = create;
    function is(value) {
      const candidate = value;
      return candidate !== void 0 && candidate !== null && Range2.is(candidate.range) && Is.boolean(candidate.caseSensitiveLookup) && (Is.string(candidate.variableName) || candidate.variableName === void 0);
    }
    InlineValueVariableLookup2.is = is;
  })(InlineValueVariableLookup || (InlineValueVariableLookup = {}));
  var InlineValueEvaluatableExpression;
  (function(InlineValueEvaluatableExpression2) {
    function create(range, expression) {
      return { range, expression };
    }
    InlineValueEvaluatableExpression2.create = create;
    function is(value) {
      const candidate = value;
      return candidate !== void 0 && candidate !== null && Range2.is(candidate.range) && (Is.string(candidate.expression) || candidate.expression === void 0);
    }
    InlineValueEvaluatableExpression2.is = is;
  })(InlineValueEvaluatableExpression || (InlineValueEvaluatableExpression = {}));
  var InlineValueContext;
  (function(InlineValueContext2) {
    function create(frameId, stoppedLocation) {
      return { frameId, stoppedLocation };
    }
    InlineValueContext2.create = create;
    function is(value) {
      const candidate = value;
      return Is.defined(candidate) && Range2.is(value.stoppedLocation);
    }
    InlineValueContext2.is = is;
  })(InlineValueContext || (InlineValueContext = {}));
  var InlayHintKind;
  (function(InlayHintKind2) {
    InlayHintKind2.Type = 1;
    InlayHintKind2.Parameter = 2;
    function is(value) {
      return value === 1 || value === 2;
    }
    InlayHintKind2.is = is;
  })(InlayHintKind || (InlayHintKind = {}));
  var InlayHintLabelPart;
  (function(InlayHintLabelPart2) {
    function create(value) {
      return { value };
    }
    InlayHintLabelPart2.create = create;
    function is(value) {
      const candidate = value;
      return Is.objectLiteral(candidate) && (candidate.tooltip === void 0 || Is.string(candidate.tooltip) || MarkupContent.is(candidate.tooltip)) && (candidate.location === void 0 || Location.is(candidate.location)) && (candidate.command === void 0 || Command.is(candidate.command));
    }
    InlayHintLabelPart2.is = is;
  })(InlayHintLabelPart || (InlayHintLabelPart = {}));
  var InlayHint;
  (function(InlayHint2) {
    function create(position, label, kind) {
      const result = { position, label };
      if (kind !== void 0) {
        result.kind = kind;
      }
      return result;
    }
    InlayHint2.create = create;
    function is(value) {
      const candidate = value;
      return Is.objectLiteral(candidate) && Position2.is(candidate.position) && (Is.string(candidate.label) || Is.typedArray(candidate.label, InlayHintLabelPart.is)) && (candidate.kind === void 0 || InlayHintKind.is(candidate.kind)) && candidate.textEdits === void 0 || Is.typedArray(candidate.textEdits, TextEdit.is) && (candidate.tooltip === void 0 || Is.string(candidate.tooltip) || MarkupContent.is(candidate.tooltip)) && (candidate.paddingLeft === void 0 || Is.boolean(candidate.paddingLeft)) && (candidate.paddingRight === void 0 || Is.boolean(candidate.paddingRight));
    }
    InlayHint2.is = is;
  })(InlayHint || (InlayHint = {}));
  var StringValue;
  (function(StringValue2) {
    function createSnippet(value) {
      return { kind: "snippet", value };
    }
    StringValue2.createSnippet = createSnippet;
    function isSnippet(value) {
      const candidate = value;
      return Is.objectLiteral(candidate) && candidate.kind === "snippet" && Is.string(candidate.value);
    }
    StringValue2.isSnippet = isSnippet;
  })(StringValue || (StringValue = {}));
  var InlineCompletionItem;
  (function(InlineCompletionItem2) {
    function create(insertText, filterText, range, command) {
      return { insertText, filterText, range, command };
    }
    InlineCompletionItem2.create = create;
  })(InlineCompletionItem || (InlineCompletionItem = {}));
  var InlineCompletionList;
  (function(InlineCompletionList2) {
    function create(items) {
      return { items };
    }
    InlineCompletionList2.create = create;
  })(InlineCompletionList || (InlineCompletionList = {}));
  var InlineCompletionTriggerKind;
  (function(InlineCompletionTriggerKind2) {
    InlineCompletionTriggerKind2.Invoked = 1;
    InlineCompletionTriggerKind2.Automatic = 2;
  })(InlineCompletionTriggerKind || (InlineCompletionTriggerKind = {}));
  var SelectedCompletionInfo;
  (function(SelectedCompletionInfo2) {
    function create(range, text) {
      return { range, text };
    }
    SelectedCompletionInfo2.create = create;
  })(SelectedCompletionInfo || (SelectedCompletionInfo = {}));
  var InlineCompletionContext;
  (function(InlineCompletionContext2) {
    function create(triggerKind, selectedCompletionInfo) {
      return { triggerKind, selectedCompletionInfo };
    }
    InlineCompletionContext2.create = create;
  })(InlineCompletionContext || (InlineCompletionContext = {}));
  var WorkspaceFolder;
  (function(WorkspaceFolder2) {
    function is(value) {
      const candidate = value;
      return Is.objectLiteral(candidate) && URI.is(candidate.uri) && Is.string(candidate.name);
    }
    WorkspaceFolder2.is = is;
  })(WorkspaceFolder || (WorkspaceFolder = {}));
  var TextDocument;
  (function(TextDocument2) {
    function create(uri, languageId, version, content) {
      return new FullTextDocument(uri, languageId, version, content);
    }
    TextDocument2.create = create;
    function is(value) {
      const candidate = value;
      return Is.defined(candidate) && Is.string(candidate.uri) && (Is.undefined(candidate.languageId) || Is.string(candidate.languageId)) && Is.uinteger(candidate.lineCount) && Is.func(candidate.getText) && Is.func(candidate.positionAt) && Is.func(candidate.offsetAt) ? true : false;
    }
    TextDocument2.is = is;
    function applyEdits(document, edits) {
      let text = document.getText();
      const sortedEdits = mergeSort(edits, (a, b) => {
        const diff = a.range.start.line - b.range.start.line;
        if (diff === 0) {
          return a.range.start.character - b.range.start.character;
        }
        return diff;
      });
      let lastModifiedOffset = text.length;
      for (let i = sortedEdits.length - 1; i >= 0; i--) {
        const e = sortedEdits[i];
        const startOffset = document.offsetAt(e.range.start);
        const endOffset = document.offsetAt(e.range.end);
        if (endOffset <= lastModifiedOffset) {
          text = text.substring(0, startOffset) + e.newText + text.substring(endOffset, text.length);
        } else {
          throw new Error("Overlapping edit");
        }
        lastModifiedOffset = startOffset;
      }
      return text;
    }
    TextDocument2.applyEdits = applyEdits;
    function mergeSort(data, compare) {
      if (data.length <= 1) {
        return data;
      }
      const p = data.length / 2 | 0;
      const left = data.slice(0, p);
      const right = data.slice(p);
      mergeSort(left, compare);
      mergeSort(right, compare);
      let leftIdx = 0;
      let rightIdx = 0;
      let i = 0;
      while (leftIdx < left.length && rightIdx < right.length) {
        const ret = compare(left[leftIdx], right[rightIdx]);
        if (ret <= 0) {
          data[i++] = left[leftIdx++];
        } else {
          data[i++] = right[rightIdx++];
        }
      }
      while (leftIdx < left.length) {
        data[i++] = left[leftIdx++];
      }
      while (rightIdx < right.length) {
        data[i++] = right[rightIdx++];
      }
      return data;
    }
  })(TextDocument || (TextDocument = {}));
  var FullTextDocument = class {
    constructor(uri, languageId, version, content) {
      this._uri = uri;
      this._languageId = languageId;
      this._version = version;
      this._content = content;
      this._lineOffsets = void 0;
    }
    get uri() {
      return this._uri;
    }
    get languageId() {
      return this._languageId;
    }
    get version() {
      return this._version;
    }
    getText(range) {
      if (range) {
        const start = this.offsetAt(range.start);
        const end = this.offsetAt(range.end);
        return this._content.substring(start, end);
      }
      return this._content;
    }
    update(event, version) {
      this._content = event.text;
      this._version = version;
      this._lineOffsets = void 0;
    }
    getLineOffsets() {
      if (this._lineOffsets === void 0) {
        const lineOffsets = [];
        const text = this._content;
        let isLineStart = true;
        for (let i = 0; i < text.length; i++) {
          if (isLineStart) {
            lineOffsets.push(i);
            isLineStart = false;
          }
          const ch = text.charAt(i);
          isLineStart = ch === "\r" || ch === "\n";
          if (ch === "\r" && i + 1 < text.length && text.charAt(i + 1) === "\n") {
            i++;
          }
        }
        if (isLineStart && text.length > 0) {
          lineOffsets.push(text.length);
        }
        this._lineOffsets = lineOffsets;
      }
      return this._lineOffsets;
    }
    positionAt(offset) {
      offset = Math.max(Math.min(offset, this._content.length), 0);
      const lineOffsets = this.getLineOffsets();
      let low = 0, high = lineOffsets.length;
      if (high === 0) {
        return Position2.create(0, offset);
      }
      while (low < high) {
        const mid = Math.floor((low + high) / 2);
        if (lineOffsets[mid] > offset) {
          high = mid;
        } else {
          low = mid + 1;
        }
      }
      const line = low - 1;
      return Position2.create(line, offset - lineOffsets[line]);
    }
    offsetAt(position) {
      const lineOffsets = this.getLineOffsets();
      if (position.line >= lineOffsets.length) {
        return this._content.length;
      } else if (position.line < 0) {
        return 0;
      }
      const lineOffset = lineOffsets[position.line];
      const nextLineOffset = position.line + 1 < lineOffsets.length ? lineOffsets[position.line + 1] : this._content.length;
      return Math.max(Math.min(lineOffset + position.character, nextLineOffset), lineOffset);
    }
    get lineCount() {
      return this.getLineOffsets().length;
    }
  };
  var Is;
  (function(Is2) {
    const toString = Object.prototype.toString;
    function defined(value) {
      return typeof value !== "undefined";
    }
    Is2.defined = defined;
    function undefined2(value) {
      return typeof value === "undefined";
    }
    Is2.undefined = undefined2;
    function boolean(value) {
      return value === true || value === false;
    }
    Is2.boolean = boolean;
    function string(value) {
      return toString.call(value) === "[object String]";
    }
    Is2.string = string;
    function number(value) {
      return toString.call(value) === "[object Number]";
    }
    Is2.number = number;
    function numberRange(value, min, max) {
      return toString.call(value) === "[object Number]" && min <= value && value <= max;
    }
    Is2.numberRange = numberRange;
    function integer2(value) {
      return toString.call(value) === "[object Number]" && -2147483648 <= value && value <= 2147483647;
    }
    Is2.integer = integer2;
    function uinteger2(value) {
      return toString.call(value) === "[object Number]" && 0 <= value && value <= 2147483647;
    }
    Is2.uinteger = uinteger2;
    function func(value) {
      return toString.call(value) === "[object Function]";
    }
    Is2.func = func;
    function objectLiteral(value) {
      return value !== null && typeof value === "object";
    }
    Is2.objectLiteral = objectLiteral;
    function typedArray(value, check) {
      return Array.isArray(value) && value.every(check);
    }
    Is2.typedArray = typedArray;
  })(Is || (Is = {}));

  // java-lsp-client.ts
  var _conn = null;
  var _workspaceRoot = "";
  var _openFiles = /* @__PURE__ */ new Set();
  var _changeTimers = /* @__PURE__ */ new Map();
  var _diagnostics = /* @__PURE__ */ new Map();
  var _providersRegistered = false;
  var _semanticTokensLegend = null;
  async function connect(resourcePath) {
    const parts = resourcePath.replace(/^\//, "").split("/");
    const workspace = parts[0];
    const project = parts[1];
    const fileUri = `file:///workspace/${workspace}/${project}/${parts.slice(2).join("/")}`;
    if (_conn) {
      openFile(fileUri);
      return;
    }
    _workspaceRoot = `file:///workspace/${workspace}/`;
    const proto = location.protocol === "https:" ? "wss" : "ws";
    const wsUrl = `${proto}://${location.host}/websockets/ide/java-lsp?workspace=${encodeURIComponent(workspace)}`;
    const ws = new WebSocket(wsUrl);
    await new Promise((resolve, reject) => {
      ws.onopen = () => resolve();
      ws.onerror = () => reject(new Error(`[java-lsp] WebSocket connect failed: ${wsUrl}`));
    });
    const socket = toSocket(ws);
    const reader = new WebSocketMessageReader(socket);
    const writer = new WebSocketMessageWriter(socket);
    _conn = (0, import_browser.createMessageConnection)(reader, writer);
    _conn.onNotification("textDocument/publishDiagnostics", (params) => {
      _diagnostics.set(params.uri, params.diagnostics ?? []);
      window.javaLspDiagnosticsChanged?.();
      const model = editor.getModels().find((m2) => m2.uri.toString() === params.uri);
      if (!model) return;
      editor.setModelMarkers(model, "java-lsp", params.diagnostics.map((d) => ({
        severity: lspSeverity(d.severity),
        message: d.message,
        source: d.source ?? "java",
        startLineNumber: d.range.start.line + 1,
        startColumn: d.range.start.character + 1,
        endLineNumber: d.range.end.line + 1,
        endColumn: d.range.end.character + 1
      })));
    });
    _conn.onRequest("workspace/applyEdit", (params) => {
      applyWorkspaceEdit(params.edit);
      return { applied: true };
    });
    _conn.onRequest("workspace/configuration", (params) => (params.items ?? []).map(() => jdtlsSettings().java));
    _conn.onRequest("client/registerCapability", () => null);
    _conn.onRequest("client/unregisterCapability", () => null);
    _conn.onRequest("window/showMessageRequest", () => null);
    _conn.onRequest("window/workDoneProgress/create", () => null);
    _conn.onNotification("window/logMessage", (p) => console.debug("[java-lsp]", p?.message));
    _conn.onNotification("window/showMessage", (p) => console.info("[java-lsp]", p?.message));
    _conn.onNotification("language/status", () => {
    });
    _conn.onNotification("language/progressReport", () => {
    });
    _conn.listen();
    const rootUri = _workspaceRoot;
    const initResult = await _conn.sendRequest("initialize", {
      processId: null,
      rootUri,
      initializationOptions: {
        settings: jdtlsSettings(),
        extendedClientCapabilities: {
          progressReportProvider: false,
          classFileContentsSupport: true,
          resolveAdditionalTextEditsSupport: true,
          // Do NOT advertise the *PromptSupport flags: those make JDT.LS return source actions
          // (generate toString/constructors/accessors, override/implement, organize imports) as
          // client-side "*Prompt" commands the vscode-java extension implements but we don't.
          // With them off, JDT.LS returns the same actions as resolvable WorkspaceEdits operating
          // on all members, which applyCodeAction resolves and applies directly.
          inferSelectionSupport: ["extractMethod", "extractVariable", "extractField"]
        }
      },
      workspaceFolders: [{ uri: rootUri, name: workspace }],
      capabilities: {
        textDocument: {
          synchronization: { dynamicRegistration: true, willSave: false, didSave: true, willSaveWaitUntil: false },
          completion: {
            dynamicRegistration: true,
            completionItem: {
              snippetSupport: true,
              documentationFormat: ["markdown", "plaintext"],
              deprecatedSupport: true,
              commitCharactersSupport: true,
              resolveSupport: { properties: ["documentation", "detail", "additionalTextEdits"] }
            },
            contextSupport: true
          },
          hover: { dynamicRegistration: true, contentFormat: ["markdown", "plaintext"] },
          signatureHelp: { dynamicRegistration: true, signatureInformation: { documentationFormat: ["markdown", "plaintext"], parameterInformation: { labelOffsetSupport: true } } },
          definition: { dynamicRegistration: true },
          references: { dynamicRegistration: true },
          implementation: { dynamicRegistration: true },
          typeDefinition: { dynamicRegistration: true },
          // Advertised so JDT.LS serves call/type hierarchy. The editor doesn't drive them
          // directly; the server-side REST facade (JavaLspQueryEndpoint) does — but JDT.LS only
          // exposes these providers when the *last* initialize advertised them, and a browser
          // editor re-initializes the shared process, so the editor must advertise them too.
          callHierarchy: { dynamicRegistration: true },
          typeHierarchy: { dynamicRegistration: true },
          documentHighlight: { dynamicRegistration: true },
          documentSymbol: { dynamicRegistration: true, hierarchicalDocumentSymbolSupport: true },
          foldingRange: { dynamicRegistration: true, lineFoldingOnly: false },
          selectionRange: { dynamicRegistration: true },
          codeLens: { dynamicRegistration: true },
          inlayHint: { dynamicRegistration: true, resolveSupport: { properties: ["label"] } },
          semanticTokens: {
            dynamicRegistration: true,
            requests: { range: false, full: { delta: false } },
            tokenTypes: [
              "namespace",
              "type",
              "class",
              "enum",
              "interface",
              "struct",
              "typeParameter",
              "parameter",
              "variable",
              "property",
              "enumMember",
              "event",
              "function",
              "method",
              "macro",
              "keyword",
              "modifier",
              "comment",
              "string",
              "number",
              "regexp",
              "operator",
              "decorator"
            ],
            tokenModifiers: [
              "declaration",
              "definition",
              "readonly",
              "static",
              "deprecated",
              "abstract",
              "async",
              "modification",
              "documentation",
              "defaultLibrary"
            ],
            formats: ["relative"],
            overlappingTokenSupport: false,
            multilineTokenSupport: false
          },
          formatting: { dynamicRegistration: true },
          rangeFormatting: { dynamicRegistration: true },
          rename: { dynamicRegistration: true, prepareSupport: true },
          codeAction: {
            dynamicRegistration: true,
            codeActionLiteralSupport: {
              codeActionKind: {
                valueSet: [
                  "quickfix",
                  "refactor",
                  "refactor.extract",
                  "refactor.inline",
                  "refactor.rewrite",
                  "source",
                  "source.organizeImports"
                ]
              }
            },
            isPreferredSupport: true,
            dataSupport: true,
            resolveSupport: { properties: ["edit"] }
          },
          publishDiagnostics: { relatedInformation: true }
        },
        workspace: {
          applyEdit: true,
          configuration: true,
          executeCommand: { dynamicRegistration: true },
          didChangeConfiguration: { dynamicRegistration: true },
          workspaceEdit: { documentChanges: true, resourceOperations: ["create", "rename", "delete"] }
        }
      }
    });
    _semanticTokensLegend = initResult?.capabilities?.semanticTokensProvider?.legend ?? null;
    _conn.sendNotification("initialized", {});
    _conn.sendNotification("workspace/didChangeConfiguration", { settings: jdtlsSettings() });
    openFile(fileUri);
    if (!_providersRegistered) {
      _providersRegistered = true;
      registerProviders();
    }
  }
  function openFile(fileUri) {
    if (_openFiles.has(fileUri) || !_conn) return;
    _openFiles.add(fileUri);
    const model = editor.getModels().find((m2) => m2.uri.toString() === fileUri);
    _conn.sendNotification("textDocument/didOpen", {
      textDocument: {
        uri: fileUri,
        languageId: "java",
        version: 1,
        text: model?.getValue() ?? ""
      }
    });
    _conn.sendNotification("workspace/didChangeWatchedFiles", { changes: [{
      uri: fileUri,
      type: 1
      /* Created */
    }] });
    if (model) {
      model.onDidChangeContent(() => {
        const existing = _changeTimers.get(fileUri);
        if (existing) clearTimeout(existing);
        _changeTimers.set(fileUri, setTimeout(() => sendDidChange(fileUri), 400));
      });
    }
  }
  function sendDidChange(fileUri) {
    _changeTimers.delete(fileUri);
    const model = editor.getModel(Uri.parse(fileUri));
    if (_conn && model) {
      _conn.sendNotification("textDocument/didChange", {
        textDocument: { uri: fileUri, version: model.getVersionId() },
        contentChanges: [{ text: model.getValue() }]
      });
    }
  }
  function flushPendingChange(fileUri) {
    if (_changeTimers.has(fileUri)) {
      clearTimeout(_changeTimers.get(fileUri));
      sendDidChange(fileUri);
    }
  }
  function isWorkspaceFile(uri) {
    return _workspaceRoot !== "" && uri.startsWith(_workspaceRoot);
  }
  function registerProviders() {
    editor.registerCommand(APPLY_ACTION_COMMAND, (_accessor, action) => {
      applyCodeAction(action);
    });
    editor.registerCommand(NOOP_COMMAND, () => {
    });
    editor.registerEditorOpener({
      openCodeEditor: (source, resource, selectionOrPosition) => {
        const uri = resource.toString();
        if (!isWorkspaceFile(uri) || !uri.startsWith(VIRTUAL_FILE_PREFIX)) return false;
        const currentModel = source.getModel();
        if (currentModel && currentModel.uri.toString() === uri) return false;
        const opener = globalThis.javaLspOpenFile;
        if (typeof opener !== "function") return false;
        const pos = selectionOrPosition;
        const line = pos ? pos.startLineNumber ?? pos.lineNumber : void 0;
        const column = pos ? pos.startColumn ?? pos.column : void 0;
        opener(uri.substring(VIRTUAL_FILE_PREFIX.length), line, column);
        return true;
      }
    });
    languages.registerCompletionItemProvider("java", {
      triggerCharacters: [".", "@", "<"],
      provideCompletionItems: async (model, position, context) => {
        if (!_conn || !isWorkspaceFile(model.uri.toString())) return null;
        const fileUri = model.uri.toString();
        flushPendingChange(fileUri);
        const result = await _conn.sendRequest("textDocument/completion", {
          textDocument: { uri: fileUri },
          position: { line: position.lineNumber - 1, character: position.column - 1 },
          // Monaco trigger kinds are 0-based (Invoke/TriggerCharacter/ForIncomplete); LSP is 1-based.
          context: { triggerKind: (context.triggerKind ?? 0) + 1, triggerCharacter: context.triggerCharacter }
        });
        const items = Array.isArray(result) ? result : result?.items ?? [];
        return {
          suggestions: items.map((item) => lspCompletionToMonaco(item, model, position)),
          // JDT.LS returns a truncated list on the first keystrokes; propagating "incomplete" makes
          // Monaco re-query as the user types instead of caching the first (often empty) result.
          incomplete: Array.isArray(result) ? false : !!result?.isIncomplete
        };
      },
      // Resolve documentation and, crucially, the auto-import additionalTextEdits which JDT.LS only
      // attaches on resolve — selecting a type then inserts its import statement.
      resolveCompletionItem: async (item) => {
        const lsp = item._lsp;
        if (!_conn || !lsp) return item;
        try {
          const resolved = await _conn.sendRequest("completionItem/resolve", lsp);
          if (resolved.documentation) {
            item.documentation = { value: markupToString(resolved.documentation), isTrusted: false };
          }
          if (resolved.detail) item.detail = resolved.detail;
          if (resolved.additionalTextEdits?.length) {
            item.additionalTextEdits = resolved.additionalTextEdits.map(textEditToMonaco);
          }
        } catch (e) {
          console.debug("[java-lsp] completion resolve failed:", e?.message);
        }
        return item;
      }
    });
    languages.registerHoverProvider("java", {
      provideHover: async (model, position) => {
        if (!_conn || !isWorkspaceFile(model.uri.toString())) return null;
        const fileUri = model.uri.toString();
        const result = await _conn.sendRequest("textDocument/hover", {
          textDocument: { uri: fileUri },
          position: { line: position.lineNumber - 1, character: position.column - 1 }
        });
        if (!result?.contents) return null;
        const contents = Array.isArray(result.contents) ? result.contents : [result.contents];
        return {
          contents: contents.map((c) => ({
            value: typeof c === "string" ? c : c.value,
            isTrusted: false
          })),
          range: result.range ? lspRangeToMonaco(result.range) : void 0
        };
      }
    });
    languages.registerSignatureHelpProvider("java", {
      signatureHelpTriggerCharacters: ["(", ","],
      provideSignatureHelp: async (model, position) => {
        if (!_conn || !isWorkspaceFile(model.uri.toString())) return null;
        const fileUri = model.uri.toString();
        const result = await _conn.sendRequest("textDocument/signatureHelp", {
          textDocument: { uri: fileUri },
          position: { line: position.lineNumber - 1, character: position.column - 1 }
        });
        if (!result) return null;
        return {
          value: {
            signatures: result.signatures.map((sig) => ({
              label: sig.label,
              documentation: sig.documentation ? markupToString(sig.documentation) : void 0,
              parameters: (sig.parameters ?? []).map((p) => ({
                label: p.label,
                documentation: p.documentation ? markupToString(p.documentation) : void 0
              }))
            })),
            activeSignature: result.activeSignature ?? 0,
            activeParameter: result.activeParameter ?? 0
          },
          dispose: () => {
          }
        };
      }
    });
    languages.registerDefinitionProvider("java", {
      provideDefinition: async (model, position) => {
        if (!_conn || !isWorkspaceFile(model.uri.toString())) return null;
        const fileUri = model.uri.toString();
        const result = await _conn.sendRequest("textDocument/definition", {
          textDocument: { uri: fileUri },
          position: { line: position.lineNumber - 1, character: position.column - 1 }
        });
        if (!result) return null;
        const locations = (Array.isArray(result) ? result : [result]).map((loc) => ({
          uri: Uri.parse(loc.uri),
          range: lspRangeToMonaco(loc.range)
        }));
        await ensureModelsForLocations(locations);
        return locations;
      }
    });
    languages.registerReferenceProvider("java", {
      provideReferences: async (model, position, context) => {
        if (!_conn || !isWorkspaceFile(model.uri.toString())) return null;
        const result = await _conn.sendRequest("textDocument/references", {
          textDocument: { uri: model.uri.toString() },
          position: { line: position.lineNumber - 1, character: position.column - 1 },
          context: { includeDeclaration: context.includeDeclaration }
        });
        if (!result) return null;
        const locations = result.map((loc) => ({ uri: Uri.parse(loc.uri), range: lspRangeToMonaco(loc.range) }));
        await ensureModelsForLocations(locations);
        return locations;
      }
    });
    languages.registerRenameProvider("java", {
      provideRenameEdits: async (model, position, newName) => {
        if (!_conn || !isWorkspaceFile(model.uri.toString())) return { edits: [] };
        const edit = await _conn.sendRequest("textDocument/rename", {
          textDocument: { uri: model.uri.toString() },
          position: { line: position.lineNumber - 1, character: position.column - 1 },
          newName
        });
        if (!edit) return { edits: [] };
        if (typeof globalThis.javaLspPersistRename === "function") {
          try {
            await applyRenameAcrossWorkspace(model, edit);
            return { edits: [] };
          } catch (e) {
            console.error("[java-lsp] cross-file rename failed, applying to the current file only:", e);
            return workspaceEditToMonaco(edit);
          }
        }
        return workspaceEditToMonaco(edit);
      },
      resolveRenameLocation: async (model, position) => {
        if (!_conn || !isWorkspaceFile(model.uri.toString())) return null;
        try {
          const result = await _conn.sendRequest("textDocument/prepareRename", {
            textDocument: { uri: model.uri.toString() },
            position: { line: position.lineNumber - 1, character: position.column - 1 }
          });
          if (!result) return null;
          const range = "range" in result ? result.range : result;
          const placeholder = "placeholder" in result && result.placeholder ? result.placeholder : model.getWordAtPosition(position)?.word ?? "";
          return { range: lspRangeToMonaco(range), text: placeholder };
        } catch {
          const word = model.getWordAtPosition(position);
          return word ? {
            range: { startLineNumber: position.lineNumber, startColumn: word.startColumn, endLineNumber: position.lineNumber, endColumn: word.endColumn },
            text: word.word
          } : null;
        }
      }
    });
    languages.registerDocumentFormattingEditProvider("java", {
      provideDocumentFormattingEdits: async (model) => {
        if (!_conn || !isWorkspaceFile(model.uri.toString())) return null;
        const edits = await _conn.sendRequest("textDocument/formatting", {
          textDocument: { uri: model.uri.toString() },
          options: { tabSize: model.getOptions().tabSize, insertSpaces: model.getOptions().insertSpaces }
        });
        return edits ? edits.map(textEditToMonaco) : null;
      }
    });
    languages.registerCodeActionProvider("java", {
      provideCodeActions: async (model, range, context) => {
        const empty = { actions: [], dispose() {
        } };
        if (!_conn || !isWorkspaceFile(model.uri.toString())) return empty;
        const lspRange = monacoRangeToLsp(range);
        const diagnostics = (_diagnostics.get(model.uri.toString()) ?? []).filter((d) => rangesOverlap(d.range, lspRange));
        const result = await _conn.sendRequest("textDocument/codeAction", {
          textDocument: { uri: model.uri.toString() },
          range: lspRange,
          // Monaco's CodeActionTriggerType (Invoke=1, Auto=2) maps 1:1 to the LSP trigger kind.
          // Forwarding it lets JDT.LS compute only quick-fixes for the passive lightbulb (cheap)
          // and the full assists/refactorings only on explicit Ctrl+. / Refactor… (Invoked).
          context: { diagnostics, only: context.only ? [context.only] : void 0, triggerKind: context.trigger }
        });
        if (!result?.length) return empty;
        return {
          actions: result.map(lspCodeActionToMonaco),
          dispose() {
          }
        };
      }
    }, {
      providedCodeActionKinds: [
        "quickfix",
        "refactor",
        "refactor.extract",
        "refactor.inline",
        "refactor.rewrite",
        "source",
        "source.organizeImports"
      ]
    });
    languages.registerImplementationProvider("java", {
      provideImplementation: (model, position) => requestLocations("textDocument/implementation", model, position)
    });
    languages.registerTypeDefinitionProvider("java", {
      provideTypeDefinition: (model, position) => requestLocations("textDocument/typeDefinition", model, position)
    });
    languages.registerDocumentHighlightProvider("java", {
      provideDocumentHighlights: async (model, position) => {
        if (!_conn || !isWorkspaceFile(model.uri.toString())) return null;
        const result = await _conn.sendRequest("textDocument/documentHighlight", {
          textDocument: { uri: model.uri.toString() },
          position: { line: position.lineNumber - 1, character: position.column - 1 }
        });
        if (!result) return null;
        return result.map((h) => ({ range: lspRangeToMonaco(h.range), kind: h.kind ? h.kind - 1 : void 0 }));
      }
    });
    languages.registerDocumentSymbolProvider("java", {
      provideDocumentSymbols: async (model) => {
        if (!_conn || !isWorkspaceFile(model.uri.toString())) return null;
        const result = await _conn.sendRequest("textDocument/documentSymbol", {
          textDocument: { uri: model.uri.toString() }
        });
        return result ? mapDocumentSymbols(result) : null;
      }
    });
    languages.registerFoldingRangeProvider("java", {
      provideFoldingRanges: async (model) => {
        if (!_conn || !isWorkspaceFile(model.uri.toString())) return null;
        const result = await _conn.sendRequest("textDocument/foldingRange", {
          textDocument: { uri: model.uri.toString() }
        });
        if (!result) return null;
        return result.map((r) => ({ start: r.startLine + 1, end: r.endLine + 1, kind: foldingKind(r.kind) }));
      }
    });
    languages.registerSelectionRangeProvider("java", {
      provideSelectionRanges: async (model, positions) => {
        if (!_conn || !isWorkspaceFile(model.uri.toString())) return null;
        const result = await _conn.sendRequest("textDocument/selectionRange", {
          textDocument: { uri: model.uri.toString() },
          positions: positions.map((p) => ({ line: p.lineNumber - 1, character: p.column - 1 }))
        });
        if (!result) return null;
        return result.map(flattenSelectionRange);
      }
    });
    languages.registerDocumentRangeFormattingEditProvider("java", {
      provideDocumentRangeFormattingEdits: async (model, range) => {
        if (!_conn || !isWorkspaceFile(model.uri.toString())) return null;
        const edits = await _conn.sendRequest("textDocument/rangeFormatting", {
          textDocument: { uri: model.uri.toString() },
          range: monacoRangeToLsp(range),
          options: { tabSize: model.getOptions().tabSize, insertSpaces: model.getOptions().insertSpaces }
        });
        return edits ? edits.map(textEditToMonaco) : null;
      }
    });
    languages.registerInlayHintsProvider("java", {
      provideInlayHints: async (model, range) => {
        if (!_conn || !isWorkspaceFile(model.uri.toString())) return null;
        const result = await _conn.sendRequest("textDocument/inlayHint", {
          textDocument: { uri: model.uri.toString() },
          range: monacoRangeToLsp(range)
        });
        if (!result) return null;
        return {
          hints: result.map((h) => ({
            position: { lineNumber: h.position.line + 1, column: h.position.character + 1 },
            label: typeof h.label === "string" ? h.label : (h.label ?? []).map((p) => ({ label: p.value })),
            kind: h.kind,
            paddingLeft: h.paddingLeft,
            paddingRight: h.paddingRight,
            tooltip: h.tooltip ? markupToString(h.tooltip) : void 0
          })),
          dispose() {
          }
        };
      }
    });
    languages.registerDocumentSemanticTokensProvider("java", {
      getLegend: () => _semanticTokensLegend ?? { tokenTypes: [], tokenModifiers: [] },
      provideDocumentSemanticTokens: async (model) => {
        if (!_conn || !isWorkspaceFile(model.uri.toString()) || !_semanticTokensLegend) return null;
        const result = await _conn.sendRequest("textDocument/semanticTokens/full", {
          textDocument: { uri: model.uri.toString() }
        });
        if (!result?.data) return null;
        return { data: new Uint32Array(result.data), resultId: result.resultId };
      },
      releaseDocumentSemanticTokens: () => {
      }
    });
    languages.registerCodeLensProvider("java", {
      provideCodeLenses: async (model) => {
        if (!_conn || !isWorkspaceFile(model.uri.toString())) return { lenses: [], dispose() {
        } };
        const result = await _conn.sendRequest("textDocument/codeLens", {
          textDocument: { uri: model.uri.toString() }
        });
        const lenses = (result ?? []).map((lens, i) => ({
          range: lspRangeToMonaco(lens.range),
          id: String(i),
          command: lens.command ? mapLensCommand(lens.command) : void 0,
          _lsp: lens
        }));
        return { lenses, dispose() {
        } };
      },
      resolveCodeLens: async (_model, codeLens) => {
        const lsp = codeLens._lsp;
        if (_conn && lsp && !lsp.command) {
          try {
            const resolved = await _conn.sendRequest("codeLens/resolve", lsp);
            codeLens.command = resolved?.command ? mapLensCommand(resolved.command) : { id: NOOP_COMMAND, title: "" };
          } catch {
            codeLens.command = { id: NOOP_COMMAND, title: "" };
          }
        }
        return codeLens;
      }
    });
    languages.registerCompletionItemProvider("java", {
      provideCompletionItems: (model, position) => {
        if (!isWorkspaceFile(model.uri.toString())) return null;
        const word = model.getWordUntilPosition(position);
        const range = {
          startLineNumber: position.lineNumber,
          startColumn: word.startColumn,
          endLineNumber: position.lineNumber,
          endColumn: word.endColumn
        };
        return {
          suggestions: JAVA_KEYWORDS.map((keyword) => ({
            label: keyword,
            kind: languages.CompletionItemKind.Keyword,
            insertText: keyword,
            range,
            sortText: `9_${keyword}`
          }))
        };
      }
    });
  }
  function lspSeverity(severity) {
    switch (severity) {
      case DiagnosticSeverity.Error:
        return MarkerSeverity.Error;
      case DiagnosticSeverity.Warning:
        return MarkerSeverity.Warning;
      case DiagnosticSeverity.Information:
        return MarkerSeverity.Info;
      case DiagnosticSeverity.Hint:
        return MarkerSeverity.Hint;
      default:
        return MarkerSeverity.Error;
    }
  }
  function lspRangeToMonaco(r) {
    return {
      startLineNumber: r.start.line + 1,
      startColumn: r.start.character + 1,
      endLineNumber: r.end.line + 1,
      endColumn: r.end.character + 1
    };
  }
  function markupToString(c) {
    return typeof c === "string" ? c : c.value;
  }
  function lspCompletionKind(kind) {
    const K = languages.CompletionItemKind;
    switch (kind) {
      case CompletionItemKind.Text:
        return K.Text;
      case CompletionItemKind.Method:
        return K.Method;
      case CompletionItemKind.Function:
        return K.Function;
      case CompletionItemKind.Constructor:
        return K.Constructor;
      case CompletionItemKind.Field:
        return K.Field;
      case CompletionItemKind.Variable:
        return K.Variable;
      case CompletionItemKind.Class:
        return K.Class;
      case CompletionItemKind.Interface:
        return K.Interface;
      case CompletionItemKind.Module:
        return K.Module;
      case CompletionItemKind.Property:
        return K.Property;
      case CompletionItemKind.Keyword:
        return K.Keyword;
      case CompletionItemKind.Snippet:
        return K.Snippet;
      case CompletionItemKind.Constant:
        return K.Constant;
      case CompletionItemKind.Struct:
        return K.Struct;
      case CompletionItemKind.TypeParameter:
        return K.TypeParameter;
      default:
        return K.Text;
    }
  }
  function lspCompletionToMonaco(item, model, position) {
    const word = model.getWordUntilPosition(position);
    let range = {
      startLineNumber: position.lineNumber,
      startColumn: word.startColumn,
      endLineNumber: position.lineNumber,
      endColumn: word.endColumn
    };
    let insertText = item.insertText ?? item.label;
    const textEdit = item.textEdit;
    if (textEdit) {
      const r = textEdit.range ?? textEdit.replace ?? textEdit.insert;
      if (r) range = lspRangeToMonaco(r);
      if (typeof textEdit.newText === "string") insertText = textEdit.newText;
    }
    const result = {
      label: item.label,
      kind: lspCompletionKind(item.kind),
      detail: item.detail,
      documentation: item.documentation ? { value: markupToString(item.documentation), isTrusted: false } : void 0,
      insertText,
      insertTextRules: item.insertTextFormat === InsertTextFormat.Snippet ? languages.CompletionItemInsertTextRule.InsertAsSnippet : void 0,
      range,
      sortText: sdkPrioritisedSortText(item),
      filterText: item.filterText,
      preselect: item.preselect,
      commitCharacters: item.commitCharacters,
      additionalTextEdits: item.additionalTextEdits?.map(textEditToMonaco)
    };
    result._lsp = item;
    return result;
  }
  function sdkPrioritisedSortText(item) {
    const base = item.sortText ?? (typeof item.label === "string" ? item.label : "");
    const description = item.labelDetails && typeof item.labelDetails.description === "string" ? item.labelDetails.description : "";
    const haystack = `${item.detail ?? ""} ${description}`;
    return haystack.includes("org.eclipse.dirigible.sdk") ? `0_${base}` : `1_${base}`;
  }
  var APPLY_ACTION_COMMAND = "dirigible.java.applyCodeAction";
  var VIRTUAL_FILE_PREFIX = "file:///workspace";
  var NOOP_COMMAND = "dirigible.java.noopLens";
  var JAVA_KEYWORDS = [
    "abstract",
    "assert",
    "boolean",
    "break",
    "byte",
    "case",
    "catch",
    "char",
    "class",
    "const",
    "continue",
    "default",
    "do",
    "double",
    "else",
    "enum",
    "extends",
    "final",
    "finally",
    "float",
    "for",
    "goto",
    "if",
    "implements",
    "import",
    "instanceof",
    "int",
    "interface",
    "long",
    "native",
    "new",
    "package",
    "private",
    "protected",
    "public",
    "return",
    "short",
    "static",
    "strictfp",
    "super",
    "switch",
    "synchronized",
    "this",
    "throw",
    "throws",
    "transient",
    "try",
    "void",
    "volatile",
    "while",
    "var",
    "yield",
    "record",
    "sealed",
    "permits",
    "true",
    "false",
    "null"
  ];
  async function requestLocations(method, model, position) {
    if (!_conn || !isWorkspaceFile(model.uri.toString())) return null;
    const result = await _conn.sendRequest(method, {
      textDocument: { uri: model.uri.toString() },
      position: { line: position.lineNumber - 1, character: position.column - 1 }
    });
    if (!result) return null;
    const locations = (Array.isArray(result) ? result : [result]).map((loc) => ({ uri: Uri.parse(loc.uri), range: lspRangeToMonaco(loc.range) }));
    await ensureModelsForLocations(locations);
    return locations;
  }
  async function ensureModelsForLocations(locations) {
    const seen = /* @__PURE__ */ new Set();
    await Promise.all(locations.map(async ({ uri }) => {
      const uriStr = uri.toString();
      if (seen.has(uriStr) || !uriStr.startsWith(VIRTUAL_FILE_PREFIX) || editor.getModel(uri)) return;
      seen.add(uriStr);
      try {
        const text = await fetchWorkspaceFileText(uriToWorkspacePath(uriStr) ?? uriStr);
        if (!editor.getModel(uri)) editor.createModel(text, "java", uri);
      } catch {
      }
    }));
  }
  function mapDocumentSymbols(symbols) {
    return (symbols ?? []).map((s) => ({
      name: s.name,
      detail: s.detail ?? "",
      kind: (s.kind ?? 1) - 1,
      tags: s.tags ?? [],
      range: lspRangeToMonaco(s.range),
      selectionRange: lspRangeToMonaco(s.selectionRange ?? s.range),
      children: s.children ? mapDocumentSymbols(s.children) : []
    }));
  }
  function foldingKind(kind) {
    const FK = languages.FoldingRangeKind;
    switch (kind) {
      case "comment":
        return FK.Comment;
      case "imports":
        return FK.Imports;
      case "region":
        return FK.Region;
      default:
        return void 0;
    }
  }
  function flattenSelectionRange(selectionRange) {
    const ranges = [];
    let current = selectionRange;
    while (current) {
      ranges.push({ range: lspRangeToMonaco(current.range) });
      current = current.parent;
    }
    return ranges;
  }
  function mapLensCommand(cmd) {
    const args = cmd.arguments ?? [];
    if ((cmd.command === "java.show.references" || cmd.command === "java.show.implementations") && args.length >= 3) {
      const locations = (args[2] ?? []).map((l) => ({ uri: Uri.parse(l.uri), range: lspRangeToMonaco(l.range) }));
      return {
        id: "editor.action.showReferences",
        title: cmd.title,
        arguments: [Uri.parse(args[0]), { lineNumber: args[1].line + 1, column: args[1].character + 1 }, locations]
      };
    }
    return { id: NOOP_COMMAND, title: cmd.title };
  }
  function textEditToMonaco(edit) {
    return { range: lspRangeToMonaco(edit.range), text: edit.newText };
  }
  function monacoRangeToLsp(r) {
    return {
      start: { line: r.startLineNumber - 1, character: r.startColumn - 1 },
      end: { line: r.endLineNumber - 1, character: r.endColumn - 1 }
    };
  }
  function rangesOverlap(a, b) {
    const notAfter = (p, q) => p.line < q.line || p.line === q.line && p.character <= q.character;
    return notAfter(a.start, b.end) && notAfter(b.start, a.end);
  }
  function lspCodeActionToMonaco(action) {
    const isCommand = typeof action.command === "string";
    const title = action.title ?? (isCommand ? action.command : action.command?.title) ?? "Action";
    const kind = isCommand ? "quickfix" : action.kind ?? "quickfix";
    return {
      title,
      kind,
      diagnostics: [],
      isPreferred: action.isPreferred,
      // Apply lazily through our command so we can resolve, run server commands and apply edits uniformly.
      command: { id: APPLY_ACTION_COMMAND, title, arguments: [action] }
    };
  }
  async function applyCodeAction(action) {
    if (!_conn) return;
    try {
      if (typeof action.command === "string") {
        await runServerCommand(action);
        return;
      }
      let resolved = action;
      if (!resolved.edit && resolved.data !== void 0) {
        resolved = await _conn.sendRequest("codeAction/resolve", resolved);
      }
      if (resolved.edit) applyWorkspaceEdit(resolved.edit);
      if (resolved.command) await runServerCommand(resolved.command);
    } catch (e) {
      console.warn("[java-lsp] code action failed:", e?.message ?? e);
    }
  }
  function isWorkspaceEdit(value) {
    return !!value && (!!value.changes || !!value.documentChanges);
  }
  async function runServerCommand(cmd) {
    if (!_conn || !cmd?.command) return;
    if (GENERATE[cmd.command]) {
      await runGenerate(cmd.command, cmd.arguments ?? []);
      return;
    }
    const result = await _conn.sendRequest("workspace/executeCommand", { command: cmd.command, arguments: cmd.arguments ?? [] });
    if (isWorkspaceEdit(result)) applyWorkspaceEdit(result);
  }
  function fieldLabel(f) {
    const name = f?.name ?? f?.fieldName ?? "";
    const type = f?.type ?? f?.typeName;
    return type ? `${name}: ${type}` : `${name}`;
  }
  var GENERATE = {
    "java.action.generateConstructorsPrompt": {
      label: "Select fields and constructors",
      status: "java.action.checkConstructorsStatus",
      generate: "java.action.generateConstructors",
      members: (s) => (s?.fields ?? []).map((f) => ({ label: fieldLabel(f), ref: f })),
      buildArgs: (args, s, sel) => [args[0], { constructors: s?.constructors ?? [], fields: sel.map((m2) => m2.ref) }]
    },
    "java.action.generateToStringPrompt": {
      label: "Select fields to include in toString()",
      status: "java.action.checkToStringStatus",
      generate: "java.action.generateToString",
      members: (s) => (s?.fields ?? []).map((f) => ({ label: fieldLabel(f), ref: f })),
      buildArgs: (args, _s, sel) => [args[0], sel.map((m2) => m2.ref)]
    },
    "java.action.hashCodeEqualsPrompt": {
      label: "Select fields for hashCode() and equals()",
      status: "java.action.checkHashCodeEqualsStatus",
      generate: "java.action.generateHashCodeEquals",
      members: (s) => (s?.fields ?? []).map((f) => ({ label: fieldLabel(f), ref: f })),
      buildArgs: (args, _s, sel) => [args[0], sel.map((m2) => m2.ref), false]
    },
    "java.action.generateAccessorsPrompt": {
      label: "Select fields to generate getters and setters",
      status: "java.action.checkAccessorsStatus",
      generate: "java.action.generateAccessors",
      members: (s) => (s?.accessors ?? s ?? []).map((a) => ({ label: fieldLabel(a), ref: a })),
      buildArgs: (args, _s, sel) => [args[0], sel.map((m2) => m2.ref)]
    },
    "java.action.overrideMethodsPrompt": {
      label: "Select methods to override or implement",
      status: "java.action.listOverridableMethods",
      generate: "java.action.addOverridableMethods",
      members: (s) => (s?.methods ?? []).map((m2) => ({
        label: `${m2.name}(${(m2.parameters ?? []).join(", ")})${m2.declaringClass ? " : " + m2.declaringClass : ""}`,
        ref: m2
      })),
      buildArgs: (args, status, sel) => [args[0], { overridableMethods: sel.map((m2) => m2.ref), type: status?.type }]
    }
  };
  async function runGenerate(promptId, args) {
    if (!_conn) return;
    const spec = GENERATE[promptId];
    const status = await _conn.sendRequest("workspace/executeCommand", { command: spec.status, arguments: args });
    if (!status) return;
    const members = spec.members(status);
    let selected = members;
    const picker = globalThis.javaLspMemberPicker;
    if (members.length && typeof picker === "function") {
      const chosen = await picker(spec.label, members.map((m2) => m2.label));
      if (chosen === null) return;
      selected = members.filter((m2) => chosen.includes(m2.label));
    }
    const edit = await _conn.sendRequest("workspace/executeCommand", {
      command: spec.generate,
      arguments: spec.buildArgs(args, status, selected)
    });
    if (isWorkspaceEdit(edit)) applyWorkspaceEdit(edit);
  }
  function applyWorkspaceEdit(edit) {
    if (!edit) return;
    const byUri = {};
    if (edit.changes) {
      for (const uri in edit.changes) byUri[uri] = (byUri[uri] ?? []).concat(edit.changes[uri]);
    }
    if (edit.documentChanges) {
      for (const dc of edit.documentChanges) {
        if (dc?.textDocument?.uri && Array.isArray(dc.edits)) {
          byUri[dc.textDocument.uri] = (byUri[dc.textDocument.uri] ?? []).concat(dc.edits);
        }
      }
    }
    for (const uri in byUri) {
      const model = editor.getModels().find((m2) => m2.uri.toString() === uri);
      if (!model) continue;
      const ops = byUri[uri].map((e) => ({ range: lspRangeToMonaco(e.range), text: e.newText, forceMoveMarkers: true }));
      model.pushEditOperations([], ops, () => null);
    }
  }
  function workspaceEditToMonaco(edit) {
    const edits = [];
    const push = (uri, list) => {
      for (const e of list) {
        edits.push({ resource: Uri.parse(uri), textEdit: { range: lspRangeToMonaco(e.range), text: e.newText }, versionId: void 0 });
      }
    };
    if (edit?.changes) {
      for (const uri in edit.changes) push(uri, edit.changes[uri]);
    }
    if (edit?.documentChanges) {
      for (const dc of edit.documentChanges) {
        if (dc?.textDocument?.uri && Array.isArray(dc.edits)) push(dc.textDocument.uri, dc.edits);
      }
    }
    return { edits };
  }
  function uriToWorkspacePath(uri) {
    if (!uri.startsWith(VIRTUAL_FILE_PREFIX)) return null;
    return decodeURIComponent(uri.substring(VIRTUAL_FILE_PREFIX.length));
  }
  async function fetchWorkspaceFileText(idePath) {
    const response = await fetch("/services/ide/workspaces" + idePath, { headers: { "X-Requested-With": "Fetch" } });
    if (!response.ok) {
      throw new Error(`Could not read ${idePath} (HTTP ${response.status})`);
    }
    return response.text();
  }
  function applyEditsToText(text, edits) {
    const lineStarts = [0];
    for (let i = 0; i < text.length; i++) {
      if (text.charCodeAt(i) === 10) lineStarts.push(i + 1);
    }
    const offset = (p) => (lineStarts[p.line] ?? text.length) + p.character;
    const ordered = edits.slice().sort((a, b) => offset(b.range.start) - offset(a.range.start));
    let result = text;
    for (const e of ordered) {
      result = result.slice(0, offset(e.range.start)) + e.newText + result.slice(offset(e.range.end));
    }
    return result;
  }
  async function applyRenameAcrossWorkspace(model, edit) {
    const currentUri = model.uri.toString();
    const textByUri = {};
    const renameByUri = {};
    if (edit.changes) {
      for (const uri in edit.changes) textByUri[uri] = (textByUri[uri] ?? []).concat(edit.changes[uri]);
    }
    if (edit.documentChanges) {
      for (const dc of edit.documentChanges) {
        if (dc?.kind === "rename" && dc.oldUri && dc.newUri) {
          renameByUri[dc.oldUri] = dc.newUri;
        } else if (dc?.textDocument?.uri && Array.isArray(dc.edits)) {
          textByUri[dc.textDocument.uri] = (textByUri[dc.textDocument.uri] ?? []).concat(dc.edits);
        }
      }
    }
    const newToOld = {};
    for (const oldUri in renameByUri) newToOld[renameByUri[oldUri]] = oldUri;
    const editsByOld = {};
    for (const uri in textByUri) {
      const onDiskUri = newToOld[uri] ?? uri;
      editsByOld[onDiskUri] = (editsByOld[onDiskUri] ?? []).concat(textByUri[uri]);
    }
    const payload = { currentPath: uriToWorkspacePath(currentUri), currentContent: null, currentNewPath: null, writes: [], renames: [] };
    const watchedChanges = [];
    const oldUris = /* @__PURE__ */ new Set([...Object.keys(editsByOld), ...Object.keys(renameByUri)]);
    for (const oldUri of oldUris) {
      const edits = editsByOld[oldUri] ?? [];
      let content;
      if (oldUri === currentUri) {
        if (edits.length) {
          model.pushEditOperations([], edits.map((e) => ({ range: lspRangeToMonaco(e.range), text: e.newText, forceMoveMarkers: true })), () => null);
        }
        content = model.getValue();
      } else {
        const source = await fetchWorkspaceFileText(uriToWorkspacePath(oldUri) ?? oldUri);
        content = edits.length ? applyEditsToText(source, edits) : source;
      }
      const newUri = renameByUri[oldUri];
      if (oldUri === currentUri) {
        payload.currentContent = content;
        payload.currentNewPath = newUri ? uriToWorkspacePath(newUri) : null;
      } else if (newUri) {
        payload.renames.push({ oldPath: uriToWorkspacePath(oldUri), newPath: uriToWorkspacePath(newUri), content });
      } else {
        payload.writes.push({ path: uriToWorkspacePath(oldUri), content });
      }
      if (newUri) {
        watchedChanges.push({
          uri: oldUri,
          type: 3
          /* Deleted */
        }, {
          uri: newUri,
          type: 1
          /* Created */
        });
        _openFiles.delete(oldUri);
        _conn?.sendNotification("textDocument/didClose", { textDocument: { uri: oldUri } });
      } else {
        watchedChanges.push({
          uri: oldUri,
          type: 2
          /* Changed */
        });
      }
    }
    await globalThis.javaLspPersistRename(payload);
    if (_conn && watchedChanges.length) {
      _conn.sendNotification("workspace/didChangeWatchedFiles", { changes: watchedChanges });
    }
  }
  function jdtlsSettings() {
    return {
      java: {
        import: {
          maven: { enabled: true },
          gradle: { enabled: false },
          exclusions: ["**/node_modules/**", "**/.metadata/**", "**/archetype-resources/**"]
        },
        autobuild: { enabled: true },
        completion: {
          overwrite: true,
          guessMethodArguments: false,
          postfix: { enabled: true },
          filteredTypes: [
            "com.sun.*",
            "sun.*",
            "jdk.*",
            "org.eclipse.jdt.internal.*",
            "org.eclipse.core.internal.*",
            "org.eclipse.osgi.internal.*"
          ],
          importOrder: ["java", "javax", "org", "com", ""]
        },
        signatureHelp: { enabled: true },
        format: { enabled: true },
        saveActions: { organizeImports: false },
        inlayHints: { parameterNames: { enabled: "all" } },
        // Off by default: the reference/implementation search behind these CodeLenses runs for every
        // declaration on open and on every edit and dominates JDT.LS load on a large classpath.
        referencesCodeLens: { enabled: false },
        implementationsCodeLens: { enabled: false }
      }
    };
  }
  return __toCommonJS(java_lsp_client_exports);
})();
//# sourceMappingURL=data:application/json;base64,ewogICJ2ZXJzaW9uIjogMywKICAic291cmNlcyI6IFsibHNwL25vZGVfbW9kdWxlcy92c2NvZGUtanNvbnJwYy9saWIvY29tbW9uL2lzLmpzIiwgImxzcC9ub2RlX21vZHVsZXMvdnNjb2RlLWpzb25ycGMvbGliL2NvbW1vbi9tZXNzYWdlcy5qcyIsICJsc3Avbm9kZV9tb2R1bGVzL3ZzY29kZS1qc29ucnBjL2xpYi9jb21tb24vbGlua2VkTWFwLmpzIiwgImxzcC9ub2RlX21vZHVsZXMvdnNjb2RlLWpzb25ycGMvbGliL2NvbW1vbi9kaXNwb3NhYmxlLmpzIiwgImxzcC9ub2RlX21vZHVsZXMvdnNjb2RlLWpzb25ycGMvbGliL2NvbW1vbi9yYWwuanMiLCAibHNwL25vZGVfbW9kdWxlcy92c2NvZGUtanNvbnJwYy9saWIvY29tbW9uL2V2ZW50cy5qcyIsICJsc3Avbm9kZV9tb2R1bGVzL3ZzY29kZS1qc29ucnBjL2xpYi9jb21tb24vY2FuY2VsbGF0aW9uLmpzIiwgImxzcC9ub2RlX21vZHVsZXMvdnNjb2RlLWpzb25ycGMvbGliL2NvbW1vbi9zaGFyZWRBcnJheUNhbmNlbGxhdGlvbi5qcyIsICJsc3Avbm9kZV9tb2R1bGVzL3ZzY29kZS1qc29ucnBjL2xpYi9jb21tb24vc2VtYXBob3JlLmpzIiwgImxzcC9ub2RlX21vZHVsZXMvdnNjb2RlLWpzb25ycGMvbGliL2NvbW1vbi9tZXNzYWdlUmVhZGVyLmpzIiwgImxzcC9ub2RlX21vZHVsZXMvdnNjb2RlLWpzb25ycGMvbGliL2NvbW1vbi9tZXNzYWdlV3JpdGVyLmpzIiwgImxzcC9ub2RlX21vZHVsZXMvdnNjb2RlLWpzb25ycGMvbGliL2NvbW1vbi9tZXNzYWdlQnVmZmVyLmpzIiwgImxzcC9ub2RlX21vZHVsZXMvdnNjb2RlLWpzb25ycGMvbGliL2NvbW1vbi9jb25uZWN0aW9uLmpzIiwgImxzcC9ub2RlX21vZHVsZXMvdnNjb2RlLWpzb25ycGMvbGliL2NvbW1vbi9hcGkuanMiLCAibHNwL25vZGVfbW9kdWxlcy92c2NvZGUtanNvbnJwYy9saWIvYnJvd3Nlci9yaWwuanMiLCAibHNwL25vZGVfbW9kdWxlcy92c2NvZGUtanNvbnJwYy9saWIvYnJvd3Nlci9tYWluLmpzIiwgImxzcC9ub2RlX21vZHVsZXMvdnNjb2RlLWpzb25ycGMvYnJvd3Nlci5qcyIsICJsc3AvamF2YS1sc3AtY2xpZW50LnRzIiwgImxzcC9ub2RlX21vZHVsZXMvdnNjb2RlLXdzLWpzb25ycGMvc3JjL2Rpc3Bvc2FibGUudHMiLCAibHNwL25vZGVfbW9kdWxlcy92c2NvZGUtd3MtanNvbnJwYy9zcmMvc29ja2V0L3NvY2tldC50cyIsICJsc3Avbm9kZV9tb2R1bGVzL3ZzY29kZS13cy1qc29ucnBjL3NyYy9zb2NrZXQvcmVhZGVyLnRzIiwgImxzcC9ub2RlX21vZHVsZXMvdnNjb2RlLXdzLWpzb25ycGMvc3JjL3NvY2tldC93cml0ZXIudHMiLCAibHNwL25vZGVfbW9kdWxlcy92c2NvZGUtd3MtanNvbnJwYy9zcmMvc29ja2V0L2Nvbm5lY3Rpb24udHMiLCAibHNwL25vZGVfbW9kdWxlcy92c2NvZGUtd3MtanNvbnJwYy9zcmMvY29ubmVjdGlvbi50cyIsICJsc3AvbW9uYWNvLXNoaW0udHMiLCAibHNwL25vZGVfbW9kdWxlcy92c2NvZGUtbGFuZ3VhZ2VzZXJ2ZXItdHlwZXMvbGliL2VzbS9tYWluLmpzIl0sCiAgInNvdXJjZXNDb250ZW50IjogWyJcInVzZSBzdHJpY3RcIjtcbi8qIC0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tXG4gKiBDb3B5cmlnaHQgKGMpIE1pY3Jvc29mdCBDb3Jwb3JhdGlvbi4gQWxsIHJpZ2h0cyByZXNlcnZlZC5cbiAqIExpY2Vuc2VkIHVuZGVyIHRoZSBNSVQgTGljZW5zZS4gU2VlIExpY2Vuc2UudHh0IGluIHRoZSBwcm9qZWN0IHJvb3QgZm9yIGxpY2Vuc2UgaW5mb3JtYXRpb24uXG4gKiAtLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0gKi9cbk9iamVjdC5kZWZpbmVQcm9wZXJ0eShleHBvcnRzLCBcIl9fZXNNb2R1bGVcIiwgeyB2YWx1ZTogdHJ1ZSB9KTtcbmV4cG9ydHMuc3RyaW5nQXJyYXkgPSBleHBvcnRzLmFycmF5ID0gZXhwb3J0cy5mdW5jID0gZXhwb3J0cy5lcnJvciA9IGV4cG9ydHMubnVtYmVyID0gZXhwb3J0cy5zdHJpbmcgPSBleHBvcnRzLmJvb2xlYW4gPSB2b2lkIDA7XG5mdW5jdGlvbiBib29sZWFuKHZhbHVlKSB7XG4gICAgcmV0dXJuIHZhbHVlID09PSB0cnVlIHx8IHZhbHVlID09PSBmYWxzZTtcbn1cbmV4cG9ydHMuYm9vbGVhbiA9IGJvb2xlYW47XG5mdW5jdGlvbiBzdHJpbmcodmFsdWUpIHtcbiAgICByZXR1cm4gdHlwZW9mIHZhbHVlID09PSAnc3RyaW5nJyB8fCB2YWx1ZSBpbnN0YW5jZW9mIFN0cmluZztcbn1cbmV4cG9ydHMuc3RyaW5nID0gc3RyaW5nO1xuZnVuY3Rpb24gbnVtYmVyKHZhbHVlKSB7XG4gICAgcmV0dXJuIHR5cGVvZiB2YWx1ZSA9PT0gJ251bWJlcicgfHwgdmFsdWUgaW5zdGFuY2VvZiBOdW1iZXI7XG59XG5leHBvcnRzLm51bWJlciA9IG51bWJlcjtcbmZ1bmN0aW9uIGVycm9yKHZhbHVlKSB7XG4gICAgcmV0dXJuIHZhbHVlIGluc3RhbmNlb2YgRXJyb3I7XG59XG5leHBvcnRzLmVycm9yID0gZXJyb3I7XG5mdW5jdGlvbiBmdW5jKHZhbHVlKSB7XG4gICAgcmV0dXJuIHR5cGVvZiB2YWx1ZSA9PT0gJ2Z1bmN0aW9uJztcbn1cbmV4cG9ydHMuZnVuYyA9IGZ1bmM7XG5mdW5jdGlvbiBhcnJheSh2YWx1ZSkge1xuICAgIHJldHVybiBBcnJheS5pc0FycmF5KHZhbHVlKTtcbn1cbmV4cG9ydHMuYXJyYXkgPSBhcnJheTtcbmZ1bmN0aW9uIHN0cmluZ0FycmF5KHZhbHVlKSB7XG4gICAgcmV0dXJuIGFycmF5KHZhbHVlKSAmJiB2YWx1ZS5ldmVyeShlbGVtID0+IHN0cmluZyhlbGVtKSk7XG59XG5leHBvcnRzLnN0cmluZ0FycmF5ID0gc3RyaW5nQXJyYXk7XG4iLCAiXCJ1c2Ugc3RyaWN0XCI7XG4vKiAtLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLVxuICogQ29weXJpZ2h0IChjKSBNaWNyb3NvZnQgQ29ycG9yYXRpb24uIEFsbCByaWdodHMgcmVzZXJ2ZWQuXG4gKiBMaWNlbnNlZCB1bmRlciB0aGUgTUlUIExpY2Vuc2UuIFNlZSBMaWNlbnNlLnR4dCBpbiB0aGUgcHJvamVjdCByb290IGZvciBsaWNlbnNlIGluZm9ybWF0aW9uLlxuICogLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tICovXG5PYmplY3QuZGVmaW5lUHJvcGVydHkoZXhwb3J0cywgXCJfX2VzTW9kdWxlXCIsIHsgdmFsdWU6IHRydWUgfSk7XG5leHBvcnRzLk1lc3NhZ2UgPSBleHBvcnRzLk5vdGlmaWNhdGlvblR5cGU5ID0gZXhwb3J0cy5Ob3RpZmljYXRpb25UeXBlOCA9IGV4cG9ydHMuTm90aWZpY2F0aW9uVHlwZTcgPSBleHBvcnRzLk5vdGlmaWNhdGlvblR5cGU2ID0gZXhwb3J0cy5Ob3RpZmljYXRpb25UeXBlNSA9IGV4cG9ydHMuTm90aWZpY2F0aW9uVHlwZTQgPSBleHBvcnRzLk5vdGlmaWNhdGlvblR5cGUzID0gZXhwb3J0cy5Ob3RpZmljYXRpb25UeXBlMiA9IGV4cG9ydHMuTm90aWZpY2F0aW9uVHlwZTEgPSBleHBvcnRzLk5vdGlmaWNhdGlvblR5cGUwID0gZXhwb3J0cy5Ob3RpZmljYXRpb25UeXBlID0gZXhwb3J0cy5SZXF1ZXN0VHlwZTkgPSBleHBvcnRzLlJlcXVlc3RUeXBlOCA9IGV4cG9ydHMuUmVxdWVzdFR5cGU3ID0gZXhwb3J0cy5SZXF1ZXN0VHlwZTYgPSBleHBvcnRzLlJlcXVlc3RUeXBlNSA9IGV4cG9ydHMuUmVxdWVzdFR5cGU0ID0gZXhwb3J0cy5SZXF1ZXN0VHlwZTMgPSBleHBvcnRzLlJlcXVlc3RUeXBlMiA9IGV4cG9ydHMuUmVxdWVzdFR5cGUxID0gZXhwb3J0cy5SZXF1ZXN0VHlwZSA9IGV4cG9ydHMuUmVxdWVzdFR5cGUwID0gZXhwb3J0cy5BYnN0cmFjdE1lc3NhZ2VTaWduYXR1cmUgPSBleHBvcnRzLlBhcmFtZXRlclN0cnVjdHVyZXMgPSBleHBvcnRzLlJlc3BvbnNlRXJyb3IgPSBleHBvcnRzLkVycm9yQ29kZXMgPSB2b2lkIDA7XG5jb25zdCBpcyA9IHJlcXVpcmUoXCIuL2lzXCIpO1xuLyoqXG4gKiBQcmVkZWZpbmVkIGVycm9yIGNvZGVzLlxuICovXG52YXIgRXJyb3JDb2RlcztcbihmdW5jdGlvbiAoRXJyb3JDb2Rlcykge1xuICAgIC8vIERlZmluZWQgYnkgSlNPTiBSUENcbiAgICBFcnJvckNvZGVzLlBhcnNlRXJyb3IgPSAtMzI3MDA7XG4gICAgRXJyb3JDb2Rlcy5JbnZhbGlkUmVxdWVzdCA9IC0zMjYwMDtcbiAgICBFcnJvckNvZGVzLk1ldGhvZE5vdEZvdW5kID0gLTMyNjAxO1xuICAgIEVycm9yQ29kZXMuSW52YWxpZFBhcmFtcyA9IC0zMjYwMjtcbiAgICBFcnJvckNvZGVzLkludGVybmFsRXJyb3IgPSAtMzI2MDM7XG4gICAgLyoqXG4gICAgICogVGhpcyBpcyB0aGUgc3RhcnQgcmFuZ2Ugb2YgSlNPTiBSUEMgcmVzZXJ2ZWQgZXJyb3IgY29kZXMuXG4gICAgICogSXQgZG9lc24ndCBkZW5vdGUgYSByZWFsIGVycm9yIGNvZGUuIE5vIGFwcGxpY2F0aW9uIGVycm9yIGNvZGVzIHNob3VsZFxuICAgICAqIGJlIGRlZmluZWQgYmV0d2VlbiB0aGUgc3RhcnQgYW5kIGVuZCByYW5nZS4gRm9yIGJhY2t3YXJkc1xuICAgICAqIGNvbXBhdGliaWxpdHkgdGhlIGBTZXJ2ZXJOb3RJbml0aWFsaXplZGAgYW5kIHRoZSBgVW5rbm93bkVycm9yQ29kZWBcbiAgICAgKiBhcmUgbGVmdCBpbiB0aGUgcmFuZ2UuXG4gICAgICpcbiAgICAgKiBAc2luY2UgMy4xNi4wXG4gICAgKi9cbiAgICBFcnJvckNvZGVzLmpzb25ycGNSZXNlcnZlZEVycm9yUmFuZ2VTdGFydCA9IC0zMjA5OTtcbiAgICAvKiogQGRlcHJlY2F0ZWQgdXNlICBqc29ucnBjUmVzZXJ2ZWRFcnJvclJhbmdlU3RhcnQgKi9cbiAgICBFcnJvckNvZGVzLnNlcnZlckVycm9yU3RhcnQgPSAtMzIwOTk7XG4gICAgLyoqXG4gICAgICogQW4gZXJyb3Igb2NjdXJyZWQgd2hlbiB3cml0ZSBhIG1lc3NhZ2UgdG8gdGhlIHRyYW5zcG9ydCBsYXllci5cbiAgICAgKi9cbiAgICBFcnJvckNvZGVzLk1lc3NhZ2VXcml0ZUVycm9yID0gLTMyMDk5O1xuICAgIC8qKlxuICAgICAqIEFuIGVycm9yIG9jY3VycmVkIHdoZW4gcmVhZGluZyBhIG1lc3NhZ2UgZnJvbSB0aGUgdHJhbnNwb3J0IGxheWVyLlxuICAgICAqL1xuICAgIEVycm9yQ29kZXMuTWVzc2FnZVJlYWRFcnJvciA9IC0zMjA5ODtcbiAgICAvKipcbiAgICAgKiBUaGUgY29ubmVjdGlvbiBnb3QgZGlzcG9zZWQgb3IgbG9zdCBhbmQgYWxsIHBlbmRpbmcgcmVzcG9uc2VzIGdvdFxuICAgICAqIHJlamVjdGVkLlxuICAgICAqL1xuICAgIEVycm9yQ29kZXMuUGVuZGluZ1Jlc3BvbnNlUmVqZWN0ZWQgPSAtMzIwOTc7XG4gICAgLyoqXG4gICAgICogVGhlIGNvbm5lY3Rpb24gaXMgaW5hY3RpdmUgYW5kIGEgdXNlIG9mIGl0IGZhaWxlZC5cbiAgICAgKi9cbiAgICBFcnJvckNvZGVzLkNvbm5lY3Rpb25JbmFjdGl2ZSA9IC0zMjA5NjtcbiAgICAvKipcbiAgICAgKiBFcnJvciBjb2RlIGluZGljYXRpbmcgdGhhdCBhIHNlcnZlciByZWNlaXZlZCBhIG5vdGlmaWNhdGlvbiBvclxuICAgICAqIHJlcXVlc3QgYmVmb3JlIHRoZSBzZXJ2ZXIgaGFzIHJlY2VpdmVkIHRoZSBgaW5pdGlhbGl6ZWAgcmVxdWVzdC5cbiAgICAgKi9cbiAgICBFcnJvckNvZGVzLlNlcnZlck5vdEluaXRpYWxpemVkID0gLTMyMDAyO1xuICAgIEVycm9yQ29kZXMuVW5rbm93bkVycm9yQ29kZSA9IC0zMjAwMTtcbiAgICAvKipcbiAgICAgKiBUaGlzIGlzIHRoZSBlbmQgcmFuZ2Ugb2YgSlNPTiBSUEMgcmVzZXJ2ZWQgZXJyb3IgY29kZXMuXG4gICAgICogSXQgZG9lc24ndCBkZW5vdGUgYSByZWFsIGVycm9yIGNvZGUuXG4gICAgICpcbiAgICAgKiBAc2luY2UgMy4xNi4wXG4gICAgKi9cbiAgICBFcnJvckNvZGVzLmpzb25ycGNSZXNlcnZlZEVycm9yUmFuZ2VFbmQgPSAtMzIwMDA7XG4gICAgLyoqIEBkZXByZWNhdGVkIHVzZSAganNvbnJwY1Jlc2VydmVkRXJyb3JSYW5nZUVuZCAqL1xuICAgIEVycm9yQ29kZXMuc2VydmVyRXJyb3JFbmQgPSAtMzIwMDA7XG59KShFcnJvckNvZGVzIHx8IChleHBvcnRzLkVycm9yQ29kZXMgPSBFcnJvckNvZGVzID0ge30pKTtcbi8qKlxuICogQW4gZXJyb3Igb2JqZWN0IHJldHVybiBpbiBhIHJlc3BvbnNlIGluIGNhc2UgYSByZXF1ZXN0XG4gKiBoYXMgZmFpbGVkLlxuICovXG5jbGFzcyBSZXNwb25zZUVycm9yIGV4dGVuZHMgRXJyb3Ige1xuICAgIGNvbnN0cnVjdG9yKGNvZGUsIG1lc3NhZ2UsIGRhdGEpIHtcbiAgICAgICAgc3VwZXIobWVzc2FnZSk7XG4gICAgICAgIHRoaXMuY29kZSA9IGlzLm51bWJlcihjb2RlKSA/IGNvZGUgOiBFcnJvckNvZGVzLlVua25vd25FcnJvckNvZGU7XG4gICAgICAgIHRoaXMuZGF0YSA9IGRhdGE7XG4gICAgICAgIE9iamVjdC5zZXRQcm90b3R5cGVPZih0aGlzLCBSZXNwb25zZUVycm9yLnByb3RvdHlwZSk7XG4gICAgfVxuICAgIHRvSnNvbigpIHtcbiAgICAgICAgY29uc3QgcmVzdWx0ID0ge1xuICAgICAgICAgICAgY29kZTogdGhpcy5jb2RlLFxuICAgICAgICAgICAgbWVzc2FnZTogdGhpcy5tZXNzYWdlXG4gICAgICAgIH07XG4gICAgICAgIGlmICh0aGlzLmRhdGEgIT09IHVuZGVmaW5lZCkge1xuICAgICAgICAgICAgcmVzdWx0LmRhdGEgPSB0aGlzLmRhdGE7XG4gICAgICAgIH1cbiAgICAgICAgcmV0dXJuIHJlc3VsdDtcbiAgICB9XG59XG5leHBvcnRzLlJlc3BvbnNlRXJyb3IgPSBSZXNwb25zZUVycm9yO1xuY2xhc3MgUGFyYW1ldGVyU3RydWN0dXJlcyB7XG4gICAgY29uc3RydWN0b3Ioa2luZCkge1xuICAgICAgICB0aGlzLmtpbmQgPSBraW5kO1xuICAgIH1cbiAgICBzdGF0aWMgaXModmFsdWUpIHtcbiAgICAgICAgcmV0dXJuIHZhbHVlID09PSBQYXJhbWV0ZXJTdHJ1Y3R1cmVzLmF1dG8gfHwgdmFsdWUgPT09IFBhcmFtZXRlclN0cnVjdHVyZXMuYnlOYW1lIHx8IHZhbHVlID09PSBQYXJhbWV0ZXJTdHJ1Y3R1cmVzLmJ5UG9zaXRpb247XG4gICAgfVxuICAgIHRvU3RyaW5nKCkge1xuICAgICAgICByZXR1cm4gdGhpcy5raW5kO1xuICAgIH1cbn1cbmV4cG9ydHMuUGFyYW1ldGVyU3RydWN0dXJlcyA9IFBhcmFtZXRlclN0cnVjdHVyZXM7XG4vKipcbiAqIFRoZSBwYXJhbWV0ZXIgc3RydWN0dXJlIGlzIGF1dG9tYXRpY2FsbHkgaW5mZXJyZWQgb24gdGhlIG51bWJlciBvZiBwYXJhbWV0ZXJzXG4gKiBhbmQgdGhlIHBhcmFtZXRlciB0eXBlIGluIGNhc2Ugb2YgYSBzaW5nbGUgcGFyYW0uXG4gKi9cblBhcmFtZXRlclN0cnVjdHVyZXMuYXV0byA9IG5ldyBQYXJhbWV0ZXJTdHJ1Y3R1cmVzKCdhdXRvJyk7XG4vKipcbiAqIEZvcmNlcyBgYnlQb3NpdGlvbmAgcGFyYW1ldGVyIHN0cnVjdHVyZS4gVGhpcyBpcyB1c2VmdWwgaWYgeW91IGhhdmUgYSBzaW5nbGVcbiAqIHBhcmFtZXRlciB3aGljaCBoYXMgYSBsaXRlcmFsIHR5cGUuXG4gKi9cblBhcmFtZXRlclN0cnVjdHVyZXMuYnlQb3NpdGlvbiA9IG5ldyBQYXJhbWV0ZXJTdHJ1Y3R1cmVzKCdieVBvc2l0aW9uJyk7XG4vKipcbiAqIEZvcmNlcyBgYnlOYW1lYCBwYXJhbWV0ZXIgc3RydWN0dXJlLiBUaGlzIGlzIG9ubHkgdXNlZnVsIHdoZW4gaGF2aW5nIGEgc2luZ2xlXG4gKiBwYXJhbWV0ZXIuIFRoZSBsaWJyYXJ5IHdpbGwgcmVwb3J0IGVycm9ycyBpZiB1c2VkIHdpdGggYSBkaWZmZXJlbnQgbnVtYmVyIG9mXG4gKiBwYXJhbWV0ZXJzLlxuICovXG5QYXJhbWV0ZXJTdHJ1Y3R1cmVzLmJ5TmFtZSA9IG5ldyBQYXJhbWV0ZXJTdHJ1Y3R1cmVzKCdieU5hbWUnKTtcbi8qKlxuICogQW4gYWJzdHJhY3QgaW1wbGVtZW50YXRpb24gb2YgYSBNZXNzYWdlVHlwZS5cbiAqL1xuY2xhc3MgQWJzdHJhY3RNZXNzYWdlU2lnbmF0dXJlIHtcbiAgICBjb25zdHJ1Y3RvcihtZXRob2QsIG51bWJlck9mUGFyYW1zKSB7XG4gICAgICAgIHRoaXMubWV0aG9kID0gbWV0aG9kO1xuICAgICAgICB0aGlzLm51bWJlck9mUGFyYW1zID0gbnVtYmVyT2ZQYXJhbXM7XG4gICAgfVxuICAgIGdldCBwYXJhbWV0ZXJTdHJ1Y3R1cmVzKCkge1xuICAgICAgICByZXR1cm4gUGFyYW1ldGVyU3RydWN0dXJlcy5hdXRvO1xuICAgIH1cbn1cbmV4cG9ydHMuQWJzdHJhY3RNZXNzYWdlU2lnbmF0dXJlID0gQWJzdHJhY3RNZXNzYWdlU2lnbmF0dXJlO1xuLyoqXG4gKiBDbGFzc2VzIHRvIHR5cGUgcmVxdWVzdCByZXNwb25zZSBwYWlyc1xuICovXG5jbGFzcyBSZXF1ZXN0VHlwZTAgZXh0ZW5kcyBBYnN0cmFjdE1lc3NhZ2VTaWduYXR1cmUge1xuICAgIGNvbnN0cnVjdG9yKG1ldGhvZCkge1xuICAgICAgICBzdXBlcihtZXRob2QsIDApO1xuICAgIH1cbn1cbmV4cG9ydHMuUmVxdWVzdFR5cGUwID0gUmVxdWVzdFR5cGUwO1xuY2xhc3MgUmVxdWVzdFR5cGUgZXh0ZW5kcyBBYnN0cmFjdE1lc3NhZ2VTaWduYXR1cmUge1xuICAgIGNvbnN0cnVjdG9yKG1ldGhvZCwgX3BhcmFtZXRlclN0cnVjdHVyZXMgPSBQYXJhbWV0ZXJTdHJ1Y3R1cmVzLmF1dG8pIHtcbiAgICAgICAgc3VwZXIobWV0aG9kLCAxKTtcbiAgICAgICAgdGhpcy5fcGFyYW1ldGVyU3RydWN0dXJlcyA9IF9wYXJhbWV0ZXJTdHJ1Y3R1cmVzO1xuICAgIH1cbiAgICBnZXQgcGFyYW1ldGVyU3RydWN0dXJlcygpIHtcbiAgICAgICAgcmV0dXJuIHRoaXMuX3BhcmFtZXRlclN0cnVjdHVyZXM7XG4gICAgfVxufVxuZXhwb3J0cy5SZXF1ZXN0VHlwZSA9IFJlcXVlc3RUeXBlO1xuY2xhc3MgUmVxdWVzdFR5cGUxIGV4dGVuZHMgQWJzdHJhY3RNZXNzYWdlU2lnbmF0dXJlIHtcbiAgICBjb25zdHJ1Y3RvcihtZXRob2QsIF9wYXJhbWV0ZXJTdHJ1Y3R1cmVzID0gUGFyYW1ldGVyU3RydWN0dXJlcy5hdXRvKSB7XG4gICAgICAgIHN1cGVyKG1ldGhvZCwgMSk7XG4gICAgICAgIHRoaXMuX3BhcmFtZXRlclN0cnVjdHVyZXMgPSBfcGFyYW1ldGVyU3RydWN0dXJlcztcbiAgICB9XG4gICAgZ2V0IHBhcmFtZXRlclN0cnVjdHVyZXMoKSB7XG4gICAgICAgIHJldHVybiB0aGlzLl9wYXJhbWV0ZXJTdHJ1Y3R1cmVzO1xuICAgIH1cbn1cbmV4cG9ydHMuUmVxdWVzdFR5cGUxID0gUmVxdWVzdFR5cGUxO1xuY2xhc3MgUmVxdWVzdFR5cGUyIGV4dGVuZHMgQWJzdHJhY3RNZXNzYWdlU2lnbmF0dXJlIHtcbiAgICBjb25zdHJ1Y3RvcihtZXRob2QpIHtcbiAgICAgICAgc3VwZXIobWV0aG9kLCAyKTtcbiAgICB9XG59XG5leHBvcnRzLlJlcXVlc3RUeXBlMiA9IFJlcXVlc3RUeXBlMjtcbmNsYXNzIFJlcXVlc3RUeXBlMyBleHRlbmRzIEFic3RyYWN0TWVzc2FnZVNpZ25hdHVyZSB7XG4gICAgY29uc3RydWN0b3IobWV0aG9kKSB7XG4gICAgICAgIHN1cGVyKG1ldGhvZCwgMyk7XG4gICAgfVxufVxuZXhwb3J0cy5SZXF1ZXN0VHlwZTMgPSBSZXF1ZXN0VHlwZTM7XG5jbGFzcyBSZXF1ZXN0VHlwZTQgZXh0ZW5kcyBBYnN0cmFjdE1lc3NhZ2VTaWduYXR1cmUge1xuICAgIGNvbnN0cnVjdG9yKG1ldGhvZCkge1xuICAgICAgICBzdXBlcihtZXRob2QsIDQpO1xuICAgIH1cbn1cbmV4cG9ydHMuUmVxdWVzdFR5cGU0ID0gUmVxdWVzdFR5cGU0O1xuY2xhc3MgUmVxdWVzdFR5cGU1IGV4dGVuZHMgQWJzdHJhY3RNZXNzYWdlU2lnbmF0dXJlIHtcbiAgICBjb25zdHJ1Y3RvcihtZXRob2QpIHtcbiAgICAgICAgc3VwZXIobWV0aG9kLCA1KTtcbiAgICB9XG59XG5leHBvcnRzLlJlcXVlc3RUeXBlNSA9IFJlcXVlc3RUeXBlNTtcbmNsYXNzIFJlcXVlc3RUeXBlNiBleHRlbmRzIEFic3RyYWN0TWVzc2FnZVNpZ25hdHVyZSB7XG4gICAgY29uc3RydWN0b3IobWV0aG9kKSB7XG4gICAgICAgIHN1cGVyKG1ldGhvZCwgNik7XG4gICAgfVxufVxuZXhwb3J0cy5SZXF1ZXN0VHlwZTYgPSBSZXF1ZXN0VHlwZTY7XG5jbGFzcyBSZXF1ZXN0VHlwZTcgZXh0ZW5kcyBBYnN0cmFjdE1lc3NhZ2VTaWduYXR1cmUge1xuICAgIGNvbnN0cnVjdG9yKG1ldGhvZCkge1xuICAgICAgICBzdXBlcihtZXRob2QsIDcpO1xuICAgIH1cbn1cbmV4cG9ydHMuUmVxdWVzdFR5cGU3ID0gUmVxdWVzdFR5cGU3O1xuY2xhc3MgUmVxdWVzdFR5cGU4IGV4dGVuZHMgQWJzdHJhY3RNZXNzYWdlU2lnbmF0dXJlIHtcbiAgICBjb25zdHJ1Y3RvcihtZXRob2QpIHtcbiAgICAgICAgc3VwZXIobWV0aG9kLCA4KTtcbiAgICB9XG59XG5leHBvcnRzLlJlcXVlc3RUeXBlOCA9IFJlcXVlc3RUeXBlODtcbmNsYXNzIFJlcXVlc3RUeXBlOSBleHRlbmRzIEFic3RyYWN0TWVzc2FnZVNpZ25hdHVyZSB7XG4gICAgY29uc3RydWN0b3IobWV0aG9kKSB7XG4gICAgICAgIHN1cGVyKG1ldGhvZCwgOSk7XG4gICAgfVxufVxuZXhwb3J0cy5SZXF1ZXN0VHlwZTkgPSBSZXF1ZXN0VHlwZTk7XG5jbGFzcyBOb3RpZmljYXRpb25UeXBlIGV4dGVuZHMgQWJzdHJhY3RNZXNzYWdlU2lnbmF0dXJlIHtcbiAgICBjb25zdHJ1Y3RvcihtZXRob2QsIF9wYXJhbWV0ZXJTdHJ1Y3R1cmVzID0gUGFyYW1ldGVyU3RydWN0dXJlcy5hdXRvKSB7XG4gICAgICAgIHN1cGVyKG1ldGhvZCwgMSk7XG4gICAgICAgIHRoaXMuX3BhcmFtZXRlclN0cnVjdHVyZXMgPSBfcGFyYW1ldGVyU3RydWN0dXJlcztcbiAgICB9XG4gICAgZ2V0IHBhcmFtZXRlclN0cnVjdHVyZXMoKSB7XG4gICAgICAgIHJldHVybiB0aGlzLl9wYXJhbWV0ZXJTdHJ1Y3R1cmVzO1xuICAgIH1cbn1cbmV4cG9ydHMuTm90aWZpY2F0aW9uVHlwZSA9IE5vdGlmaWNhdGlvblR5cGU7XG5jbGFzcyBOb3RpZmljYXRpb25UeXBlMCBleHRlbmRzIEFic3RyYWN0TWVzc2FnZVNpZ25hdHVyZSB7XG4gICAgY29uc3RydWN0b3IobWV0aG9kKSB7XG4gICAgICAgIHN1cGVyKG1ldGhvZCwgMCk7XG4gICAgfVxufVxuZXhwb3J0cy5Ob3RpZmljYXRpb25UeXBlMCA9IE5vdGlmaWNhdGlvblR5cGUwO1xuY2xhc3MgTm90aWZpY2F0aW9uVHlwZTEgZXh0ZW5kcyBBYnN0cmFjdE1lc3NhZ2VTaWduYXR1cmUge1xuICAgIGNvbnN0cnVjdG9yKG1ldGhvZCwgX3BhcmFtZXRlclN0cnVjdHVyZXMgPSBQYXJhbWV0ZXJTdHJ1Y3R1cmVzLmF1dG8pIHtcbiAgICAgICAgc3VwZXIobWV0aG9kLCAxKTtcbiAgICAgICAgdGhpcy5fcGFyYW1ldGVyU3RydWN0dXJlcyA9IF9wYXJhbWV0ZXJTdHJ1Y3R1cmVzO1xuICAgIH1cbiAgICBnZXQgcGFyYW1ldGVyU3RydWN0dXJlcygpIHtcbiAgICAgICAgcmV0dXJuIHRoaXMuX3BhcmFtZXRlclN0cnVjdHVyZXM7XG4gICAgfVxufVxuZXhwb3J0cy5Ob3RpZmljYXRpb25UeXBlMSA9IE5vdGlmaWNhdGlvblR5cGUxO1xuY2xhc3MgTm90aWZpY2F0aW9uVHlwZTIgZXh0ZW5kcyBBYnN0cmFjdE1lc3NhZ2VTaWduYXR1cmUge1xuICAgIGNvbnN0cnVjdG9yKG1ldGhvZCkge1xuICAgICAgICBzdXBlcihtZXRob2QsIDIpO1xuICAgIH1cbn1cbmV4cG9ydHMuTm90aWZpY2F0aW9uVHlwZTIgPSBOb3RpZmljYXRpb25UeXBlMjtcbmNsYXNzIE5vdGlmaWNhdGlvblR5cGUzIGV4dGVuZHMgQWJzdHJhY3RNZXNzYWdlU2lnbmF0dXJlIHtcbiAgICBjb25zdHJ1Y3RvcihtZXRob2QpIHtcbiAgICAgICAgc3VwZXIobWV0aG9kLCAzKTtcbiAgICB9XG59XG5leHBvcnRzLk5vdGlmaWNhdGlvblR5cGUzID0gTm90aWZpY2F0aW9uVHlwZTM7XG5jbGFzcyBOb3RpZmljYXRpb25UeXBlNCBleHRlbmRzIEFic3RyYWN0TWVzc2FnZVNpZ25hdHVyZSB7XG4gICAgY29uc3RydWN0b3IobWV0aG9kKSB7XG4gICAgICAgIHN1cGVyKG1ldGhvZCwgNCk7XG4gICAgfVxufVxuZXhwb3J0cy5Ob3RpZmljYXRpb25UeXBlNCA9IE5vdGlmaWNhdGlvblR5cGU0O1xuY2xhc3MgTm90aWZpY2F0aW9uVHlwZTUgZXh0ZW5kcyBBYnN0cmFjdE1lc3NhZ2VTaWduYXR1cmUge1xuICAgIGNvbnN0cnVjdG9yKG1ldGhvZCkge1xuICAgICAgICBzdXBlcihtZXRob2QsIDUpO1xuICAgIH1cbn1cbmV4cG9ydHMuTm90aWZpY2F0aW9uVHlwZTUgPSBOb3RpZmljYXRpb25UeXBlNTtcbmNsYXNzIE5vdGlmaWNhdGlvblR5cGU2IGV4dGVuZHMgQWJzdHJhY3RNZXNzYWdlU2lnbmF0dXJlIHtcbiAgICBjb25zdHJ1Y3RvcihtZXRob2QpIHtcbiAgICAgICAgc3VwZXIobWV0aG9kLCA2KTtcbiAgICB9XG59XG5leHBvcnRzLk5vdGlmaWNhdGlvblR5cGU2ID0gTm90aWZpY2F0aW9uVHlwZTY7XG5jbGFzcyBOb3RpZmljYXRpb25UeXBlNyBleHRlbmRzIEFic3RyYWN0TWVzc2FnZVNpZ25hdHVyZSB7XG4gICAgY29uc3RydWN0b3IobWV0aG9kKSB7XG4gICAgICAgIHN1cGVyKG1ldGhvZCwgNyk7XG4gICAgfVxufVxuZXhwb3J0cy5Ob3RpZmljYXRpb25UeXBlNyA9IE5vdGlmaWNhdGlvblR5cGU3O1xuY2xhc3MgTm90aWZpY2F0aW9uVHlwZTggZXh0ZW5kcyBBYnN0cmFjdE1lc3NhZ2VTaWduYXR1cmUge1xuICAgIGNvbnN0cnVjdG9yKG1ldGhvZCkge1xuICAgICAgICBzdXBlcihtZXRob2QsIDgpO1xuICAgIH1cbn1cbmV4cG9ydHMuTm90aWZpY2F0aW9uVHlwZTggPSBOb3RpZmljYXRpb25UeXBlODtcbmNsYXNzIE5vdGlmaWNhdGlvblR5cGU5IGV4dGVuZHMgQWJzdHJhY3RNZXNzYWdlU2lnbmF0dXJlIHtcbiAgICBjb25zdHJ1Y3RvcihtZXRob2QpIHtcbiAgICAgICAgc3VwZXIobWV0aG9kLCA5KTtcbiAgICB9XG59XG5leHBvcnRzLk5vdGlmaWNhdGlvblR5cGU5ID0gTm90aWZpY2F0aW9uVHlwZTk7XG52YXIgTWVzc2FnZTtcbihmdW5jdGlvbiAoTWVzc2FnZSkge1xuICAgIC8qKlxuICAgICAqIFRlc3RzIGlmIHRoZSBnaXZlbiBtZXNzYWdlIGlzIGEgcmVxdWVzdCBtZXNzYWdlXG4gICAgICovXG4gICAgZnVuY3Rpb24gaXNSZXF1ZXN0KG1lc3NhZ2UpIHtcbiAgICAgICAgY29uc3QgY2FuZGlkYXRlID0gbWVzc2FnZTtcbiAgICAgICAgcmV0dXJuIGNhbmRpZGF0ZSAmJiBpcy5zdHJpbmcoY2FuZGlkYXRlLm1ldGhvZCkgJiYgKGlzLnN0cmluZyhjYW5kaWRhdGUuaWQpIHx8IGlzLm51bWJlcihjYW5kaWRhdGUuaWQpKTtcbiAgICB9XG4gICAgTWVzc2FnZS5pc1JlcXVlc3QgPSBpc1JlcXVlc3Q7XG4gICAgLyoqXG4gICAgICogVGVzdHMgaWYgdGhlIGdpdmVuIG1lc3NhZ2UgaXMgYSBub3RpZmljYXRpb24gbWVzc2FnZVxuICAgICAqL1xuICAgIGZ1bmN0aW9uIGlzTm90aWZpY2F0aW9uKG1lc3NhZ2UpIHtcbiAgICAgICAgY29uc3QgY2FuZGlkYXRlID0gbWVzc2FnZTtcbiAgICAgICAgcmV0dXJuIGNhbmRpZGF0ZSAmJiBpcy5zdHJpbmcoY2FuZGlkYXRlLm1ldGhvZCkgJiYgbWVzc2FnZS5pZCA9PT0gdm9pZCAwO1xuICAgIH1cbiAgICBNZXNzYWdlLmlzTm90aWZpY2F0aW9uID0gaXNOb3RpZmljYXRpb247XG4gICAgLyoqXG4gICAgICogVGVzdHMgaWYgdGhlIGdpdmVuIG1lc3NhZ2UgaXMgYSByZXNwb25zZSBtZXNzYWdlXG4gICAgICovXG4gICAgZnVuY3Rpb24gaXNSZXNwb25zZShtZXNzYWdlKSB7XG4gICAgICAgIGNvbnN0IGNhbmRpZGF0ZSA9IG1lc3NhZ2U7XG4gICAgICAgIHJldHVybiBjYW5kaWRhdGUgJiYgKGNhbmRpZGF0ZS5yZXN1bHQgIT09IHZvaWQgMCB8fCAhIWNhbmRpZGF0ZS5lcnJvcikgJiYgKGlzLnN0cmluZyhjYW5kaWRhdGUuaWQpIHx8IGlzLm51bWJlcihjYW5kaWRhdGUuaWQpIHx8IGNhbmRpZGF0ZS5pZCA9PT0gbnVsbCk7XG4gICAgfVxuICAgIE1lc3NhZ2UuaXNSZXNwb25zZSA9IGlzUmVzcG9uc2U7XG59KShNZXNzYWdlIHx8IChleHBvcnRzLk1lc3NhZ2UgPSBNZXNzYWdlID0ge30pKTtcbiIsICJcInVzZSBzdHJpY3RcIjtcbi8qLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tXG4gKiAgQ29weXJpZ2h0IChjKSBNaWNyb3NvZnQgQ29ycG9yYXRpb24uIEFsbCByaWdodHMgcmVzZXJ2ZWQuXG4gKiAgTGljZW5zZWQgdW5kZXIgdGhlIE1JVCBMaWNlbnNlLiBTZWUgTGljZW5zZS50eHQgaW4gdGhlIHByb2plY3Qgcm9vdCBmb3IgbGljZW5zZSBpbmZvcm1hdGlvbi5cbiAqLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0qL1xudmFyIF9hO1xuT2JqZWN0LmRlZmluZVByb3BlcnR5KGV4cG9ydHMsIFwiX19lc01vZHVsZVwiLCB7IHZhbHVlOiB0cnVlIH0pO1xuZXhwb3J0cy5MUlVDYWNoZSA9IGV4cG9ydHMuTGlua2VkTWFwID0gZXhwb3J0cy5Ub3VjaCA9IHZvaWQgMDtcbnZhciBUb3VjaDtcbihmdW5jdGlvbiAoVG91Y2gpIHtcbiAgICBUb3VjaC5Ob25lID0gMDtcbiAgICBUb3VjaC5GaXJzdCA9IDE7XG4gICAgVG91Y2guQXNPbGQgPSBUb3VjaC5GaXJzdDtcbiAgICBUb3VjaC5MYXN0ID0gMjtcbiAgICBUb3VjaC5Bc05ldyA9IFRvdWNoLkxhc3Q7XG59KShUb3VjaCB8fCAoZXhwb3J0cy5Ub3VjaCA9IFRvdWNoID0ge30pKTtcbmNsYXNzIExpbmtlZE1hcCB7XG4gICAgY29uc3RydWN0b3IoKSB7XG4gICAgICAgIHRoaXNbX2FdID0gJ0xpbmtlZE1hcCc7XG4gICAgICAgIHRoaXMuX21hcCA9IG5ldyBNYXAoKTtcbiAgICAgICAgdGhpcy5faGVhZCA9IHVuZGVmaW5lZDtcbiAgICAgICAgdGhpcy5fdGFpbCA9IHVuZGVmaW5lZDtcbiAgICAgICAgdGhpcy5fc2l6ZSA9IDA7XG4gICAgICAgIHRoaXMuX3N0YXRlID0gMDtcbiAgICB9XG4gICAgY2xlYXIoKSB7XG4gICAgICAgIHRoaXMuX21hcC5jbGVhcigpO1xuICAgICAgICB0aGlzLl9oZWFkID0gdW5kZWZpbmVkO1xuICAgICAgICB0aGlzLl90YWlsID0gdW5kZWZpbmVkO1xuICAgICAgICB0aGlzLl9zaXplID0gMDtcbiAgICAgICAgdGhpcy5fc3RhdGUrKztcbiAgICB9XG4gICAgaXNFbXB0eSgpIHtcbiAgICAgICAgcmV0dXJuICF0aGlzLl9oZWFkICYmICF0aGlzLl90YWlsO1xuICAgIH1cbiAgICBnZXQgc2l6ZSgpIHtcbiAgICAgICAgcmV0dXJuIHRoaXMuX3NpemU7XG4gICAgfVxuICAgIGdldCBmaXJzdCgpIHtcbiAgICAgICAgcmV0dXJuIHRoaXMuX2hlYWQ/LnZhbHVlO1xuICAgIH1cbiAgICBnZXQgbGFzdCgpIHtcbiAgICAgICAgcmV0dXJuIHRoaXMuX3RhaWw/LnZhbHVlO1xuICAgIH1cbiAgICBoYXMoa2V5KSB7XG4gICAgICAgIHJldHVybiB0aGlzLl9tYXAuaGFzKGtleSk7XG4gICAgfVxuICAgIGdldChrZXksIHRvdWNoID0gVG91Y2guTm9uZSkge1xuICAgICAgICBjb25zdCBpdGVtID0gdGhpcy5fbWFwLmdldChrZXkpO1xuICAgICAgICBpZiAoIWl0ZW0pIHtcbiAgICAgICAgICAgIHJldHVybiB1bmRlZmluZWQ7XG4gICAgICAgIH1cbiAgICAgICAgaWYgKHRvdWNoICE9PSBUb3VjaC5Ob25lKSB7XG4gICAgICAgICAgICB0aGlzLnRvdWNoKGl0ZW0sIHRvdWNoKTtcbiAgICAgICAgfVxuICAgICAgICByZXR1cm4gaXRlbS52YWx1ZTtcbiAgICB9XG4gICAgc2V0KGtleSwgdmFsdWUsIHRvdWNoID0gVG91Y2guTm9uZSkge1xuICAgICAgICBsZXQgaXRlbSA9IHRoaXMuX21hcC5nZXQoa2V5KTtcbiAgICAgICAgaWYgKGl0ZW0pIHtcbiAgICAgICAgICAgIGl0ZW0udmFsdWUgPSB2YWx1ZTtcbiAgICAgICAgICAgIGlmICh0b3VjaCAhPT0gVG91Y2guTm9uZSkge1xuICAgICAgICAgICAgICAgIHRoaXMudG91Y2goaXRlbSwgdG91Y2gpO1xuICAgICAgICAgICAgfVxuICAgICAgICB9XG4gICAgICAgIGVsc2Uge1xuICAgICAgICAgICAgaXRlbSA9IHsga2V5LCB2YWx1ZSwgbmV4dDogdW5kZWZpbmVkLCBwcmV2aW91czogdW5kZWZpbmVkIH07XG4gICAgICAgICAgICBzd2l0Y2ggKHRvdWNoKSB7XG4gICAgICAgICAgICAgICAgY2FzZSBUb3VjaC5Ob25lOlxuICAgICAgICAgICAgICAgICAgICB0aGlzLmFkZEl0ZW1MYXN0KGl0ZW0pO1xuICAgICAgICAgICAgICAgICAgICBicmVhaztcbiAgICAgICAgICAgICAgICBjYXNlIFRvdWNoLkZpcnN0OlxuICAgICAgICAgICAgICAgICAgICB0aGlzLmFkZEl0ZW1GaXJzdChpdGVtKTtcbiAgICAgICAgICAgICAgICAgICAgYnJlYWs7XG4gICAgICAgICAgICAgICAgY2FzZSBUb3VjaC5MYXN0OlxuICAgICAgICAgICAgICAgICAgICB0aGlzLmFkZEl0ZW1MYXN0KGl0ZW0pO1xuICAgICAgICAgICAgICAgICAgICBicmVhaztcbiAgICAgICAgICAgICAgICBkZWZhdWx0OlxuICAgICAgICAgICAgICAgICAgICB0aGlzLmFkZEl0ZW1MYXN0KGl0ZW0pO1xuICAgICAgICAgICAgICAgICAgICBicmVhaztcbiAgICAgICAgICAgIH1cbiAgICAgICAgICAgIHRoaXMuX21hcC5zZXQoa2V5LCBpdGVtKTtcbiAgICAgICAgICAgIHRoaXMuX3NpemUrKztcbiAgICAgICAgfVxuICAgICAgICByZXR1cm4gdGhpcztcbiAgICB9XG4gICAgZGVsZXRlKGtleSkge1xuICAgICAgICByZXR1cm4gISF0aGlzLnJlbW92ZShrZXkpO1xuICAgIH1cbiAgICByZW1vdmUoa2V5KSB7XG4gICAgICAgIGNvbnN0IGl0ZW0gPSB0aGlzLl9tYXAuZ2V0KGtleSk7XG4gICAgICAgIGlmICghaXRlbSkge1xuICAgICAgICAgICAgcmV0dXJuIHVuZGVmaW5lZDtcbiAgICAgICAgfVxuICAgICAgICB0aGlzLl9tYXAuZGVsZXRlKGtleSk7XG4gICAgICAgIHRoaXMucmVtb3ZlSXRlbShpdGVtKTtcbiAgICAgICAgdGhpcy5fc2l6ZS0tO1xuICAgICAgICByZXR1cm4gaXRlbS52YWx1ZTtcbiAgICB9XG4gICAgc2hpZnQoKSB7XG4gICAgICAgIGlmICghdGhpcy5faGVhZCAmJiAhdGhpcy5fdGFpbCkge1xuICAgICAgICAgICAgcmV0dXJuIHVuZGVmaW5lZDtcbiAgICAgICAgfVxuICAgICAgICBpZiAoIXRoaXMuX2hlYWQgfHwgIXRoaXMuX3RhaWwpIHtcbiAgICAgICAgICAgIHRocm93IG5ldyBFcnJvcignSW52YWxpZCBsaXN0Jyk7XG4gICAgICAgIH1cbiAgICAgICAgY29uc3QgaXRlbSA9IHRoaXMuX2hlYWQ7XG4gICAgICAgIHRoaXMuX21hcC5kZWxldGUoaXRlbS5rZXkpO1xuICAgICAgICB0aGlzLnJlbW92ZUl0ZW0oaXRlbSk7XG4gICAgICAgIHRoaXMuX3NpemUtLTtcbiAgICAgICAgcmV0dXJuIGl0ZW0udmFsdWU7XG4gICAgfVxuICAgIGZvckVhY2goY2FsbGJhY2tmbiwgdGhpc0FyZykge1xuICAgICAgICBjb25zdCBzdGF0ZSA9IHRoaXMuX3N0YXRlO1xuICAgICAgICBsZXQgY3VycmVudCA9IHRoaXMuX2hlYWQ7XG4gICAgICAgIHdoaWxlIChjdXJyZW50KSB7XG4gICAgICAgICAgICBpZiAodGhpc0FyZykge1xuICAgICAgICAgICAgICAgIGNhbGxiYWNrZm4uYmluZCh0aGlzQXJnKShjdXJyZW50LnZhbHVlLCBjdXJyZW50LmtleSwgdGhpcyk7XG4gICAgICAgICAgICB9XG4gICAgICAgICAgICBlbHNlIHtcbiAgICAgICAgICAgICAgICBjYWxsYmFja2ZuKGN1cnJlbnQudmFsdWUsIGN1cnJlbnQua2V5LCB0aGlzKTtcbiAgICAgICAgICAgIH1cbiAgICAgICAgICAgIGlmICh0aGlzLl9zdGF0ZSAhPT0gc3RhdGUpIHtcbiAgICAgICAgICAgICAgICB0aHJvdyBuZXcgRXJyb3IoYExpbmtlZE1hcCBnb3QgbW9kaWZpZWQgZHVyaW5nIGl0ZXJhdGlvbi5gKTtcbiAgICAgICAgICAgIH1cbiAgICAgICAgICAgIGN1cnJlbnQgPSBjdXJyZW50Lm5leHQ7XG4gICAgICAgIH1cbiAgICB9XG4gICAga2V5cygpIHtcbiAgICAgICAgY29uc3Qgc3RhdGUgPSB0aGlzLl9zdGF0ZTtcbiAgICAgICAgbGV0IGN1cnJlbnQgPSB0aGlzLl9oZWFkO1xuICAgICAgICBjb25zdCBpdGVyYXRvciA9IHtcbiAgICAgICAgICAgIFtTeW1ib2wuaXRlcmF0b3JdOiAoKSA9PiB7XG4gICAgICAgICAgICAgICAgcmV0dXJuIGl0ZXJhdG9yO1xuICAgICAgICAgICAgfSxcbiAgICAgICAgICAgIG5leHQ6ICgpID0+IHtcbiAgICAgICAgICAgICAgICBpZiAodGhpcy5fc3RhdGUgIT09IHN0YXRlKSB7XG4gICAgICAgICAgICAgICAgICAgIHRocm93IG5ldyBFcnJvcihgTGlua2VkTWFwIGdvdCBtb2RpZmllZCBkdXJpbmcgaXRlcmF0aW9uLmApO1xuICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICBpZiAoY3VycmVudCkge1xuICAgICAgICAgICAgICAgICAgICBjb25zdCByZXN1bHQgPSB7IHZhbHVlOiBjdXJyZW50LmtleSwgZG9uZTogZmFsc2UgfTtcbiAgICAgICAgICAgICAgICAgICAgY3VycmVudCA9IGN1cnJlbnQubmV4dDtcbiAgICAgICAgICAgICAgICAgICAgcmV0dXJuIHJlc3VsdDtcbiAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgZWxzZSB7XG4gICAgICAgICAgICAgICAgICAgIHJldHVybiB7IHZhbHVlOiB1bmRlZmluZWQsIGRvbmU6IHRydWUgfTtcbiAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICB9XG4gICAgICAgIH07XG4gICAgICAgIHJldHVybiBpdGVyYXRvcjtcbiAgICB9XG4gICAgdmFsdWVzKCkge1xuICAgICAgICBjb25zdCBzdGF0ZSA9IHRoaXMuX3N0YXRlO1xuICAgICAgICBsZXQgY3VycmVudCA9IHRoaXMuX2hlYWQ7XG4gICAgICAgIGNvbnN0IGl0ZXJhdG9yID0ge1xuICAgICAgICAgICAgW1N5bWJvbC5pdGVyYXRvcl06ICgpID0+IHtcbiAgICAgICAgICAgICAgICByZXR1cm4gaXRlcmF0b3I7XG4gICAgICAgICAgICB9LFxuICAgICAgICAgICAgbmV4dDogKCkgPT4ge1xuICAgICAgICAgICAgICAgIGlmICh0aGlzLl9zdGF0ZSAhPT0gc3RhdGUpIHtcbiAgICAgICAgICAgICAgICAgICAgdGhyb3cgbmV3IEVycm9yKGBMaW5rZWRNYXAgZ290IG1vZGlmaWVkIGR1cmluZyBpdGVyYXRpb24uYCk7XG4gICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgIGlmIChjdXJyZW50KSB7XG4gICAgICAgICAgICAgICAgICAgIGNvbnN0IHJlc3VsdCA9IHsgdmFsdWU6IGN1cnJlbnQudmFsdWUsIGRvbmU6IGZhbHNlIH07XG4gICAgICAgICAgICAgICAgICAgIGN1cnJlbnQgPSBjdXJyZW50Lm5leHQ7XG4gICAgICAgICAgICAgICAgICAgIHJldHVybiByZXN1bHQ7XG4gICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgIGVsc2Uge1xuICAgICAgICAgICAgICAgICAgICByZXR1cm4geyB2YWx1ZTogdW5kZWZpbmVkLCBkb25lOiB0cnVlIH07XG4gICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgfVxuICAgICAgICB9O1xuICAgICAgICByZXR1cm4gaXRlcmF0b3I7XG4gICAgfVxuICAgIGVudHJpZXMoKSB7XG4gICAgICAgIGNvbnN0IHN0YXRlID0gdGhpcy5fc3RhdGU7XG4gICAgICAgIGxldCBjdXJyZW50ID0gdGhpcy5faGVhZDtcbiAgICAgICAgY29uc3QgaXRlcmF0b3IgPSB7XG4gICAgICAgICAgICBbU3ltYm9sLml0ZXJhdG9yXTogKCkgPT4ge1xuICAgICAgICAgICAgICAgIHJldHVybiBpdGVyYXRvcjtcbiAgICAgICAgICAgIH0sXG4gICAgICAgICAgICBuZXh0OiAoKSA9PiB7XG4gICAgICAgICAgICAgICAgaWYgKHRoaXMuX3N0YXRlICE9PSBzdGF0ZSkge1xuICAgICAgICAgICAgICAgICAgICB0aHJvdyBuZXcgRXJyb3IoYExpbmtlZE1hcCBnb3QgbW9kaWZpZWQgZHVyaW5nIGl0ZXJhdGlvbi5gKTtcbiAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgaWYgKGN1cnJlbnQpIHtcbiAgICAgICAgICAgICAgICAgICAgY29uc3QgcmVzdWx0ID0geyB2YWx1ZTogW2N1cnJlbnQua2V5LCBjdXJyZW50LnZhbHVlXSwgZG9uZTogZmFsc2UgfTtcbiAgICAgICAgICAgICAgICAgICAgY3VycmVudCA9IGN1cnJlbnQubmV4dDtcbiAgICAgICAgICAgICAgICAgICAgcmV0dXJuIHJlc3VsdDtcbiAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgZWxzZSB7XG4gICAgICAgICAgICAgICAgICAgIHJldHVybiB7IHZhbHVlOiB1bmRlZmluZWQsIGRvbmU6IHRydWUgfTtcbiAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICB9XG4gICAgICAgIH07XG4gICAgICAgIHJldHVybiBpdGVyYXRvcjtcbiAgICB9XG4gICAgWyhfYSA9IFN5bWJvbC50b1N0cmluZ1RhZywgU3ltYm9sLml0ZXJhdG9yKV0oKSB7XG4gICAgICAgIHJldHVybiB0aGlzLmVudHJpZXMoKTtcbiAgICB9XG4gICAgdHJpbU9sZChuZXdTaXplKSB7XG4gICAgICAgIGlmIChuZXdTaXplID49IHRoaXMuc2l6ZSkge1xuICAgICAgICAgICAgcmV0dXJuO1xuICAgICAgICB9XG4gICAgICAgIGlmIChuZXdTaXplID09PSAwKSB7XG4gICAgICAgICAgICB0aGlzLmNsZWFyKCk7XG4gICAgICAgICAgICByZXR1cm47XG4gICAgICAgIH1cbiAgICAgICAgbGV0IGN1cnJlbnQgPSB0aGlzLl9oZWFkO1xuICAgICAgICBsZXQgY3VycmVudFNpemUgPSB0aGlzLnNpemU7XG4gICAgICAgIHdoaWxlIChjdXJyZW50ICYmIGN1cnJlbnRTaXplID4gbmV3U2l6ZSkge1xuICAgICAgICAgICAgdGhpcy5fbWFwLmRlbGV0ZShjdXJyZW50LmtleSk7XG4gICAgICAgICAgICBjdXJyZW50ID0gY3VycmVudC5uZXh0O1xuICAgICAgICAgICAgY3VycmVudFNpemUtLTtcbiAgICAgICAgfVxuICAgICAgICB0aGlzLl9oZWFkID0gY3VycmVudDtcbiAgICAgICAgdGhpcy5fc2l6ZSA9IGN1cnJlbnRTaXplO1xuICAgICAgICBpZiAoY3VycmVudCkge1xuICAgICAgICAgICAgY3VycmVudC5wcmV2aW91cyA9IHVuZGVmaW5lZDtcbiAgICAgICAgfVxuICAgICAgICB0aGlzLl9zdGF0ZSsrO1xuICAgIH1cbiAgICBhZGRJdGVtRmlyc3QoaXRlbSkge1xuICAgICAgICAvLyBGaXJzdCB0aW1lIEluc2VydFxuICAgICAgICBpZiAoIXRoaXMuX2hlYWQgJiYgIXRoaXMuX3RhaWwpIHtcbiAgICAgICAgICAgIHRoaXMuX3RhaWwgPSBpdGVtO1xuICAgICAgICB9XG4gICAgICAgIGVsc2UgaWYgKCF0aGlzLl9oZWFkKSB7XG4gICAgICAgICAgICB0aHJvdyBuZXcgRXJyb3IoJ0ludmFsaWQgbGlzdCcpO1xuICAgICAgICB9XG4gICAgICAgIGVsc2Uge1xuICAgICAgICAgICAgaXRlbS5uZXh0ID0gdGhpcy5faGVhZDtcbiAgICAgICAgICAgIHRoaXMuX2hlYWQucHJldmlvdXMgPSBpdGVtO1xuICAgICAgICB9XG4gICAgICAgIHRoaXMuX2hlYWQgPSBpdGVtO1xuICAgICAgICB0aGlzLl9zdGF0ZSsrO1xuICAgIH1cbiAgICBhZGRJdGVtTGFzdChpdGVtKSB7XG4gICAgICAgIC8vIEZpcnN0IHRpbWUgSW5zZXJ0XG4gICAgICAgIGlmICghdGhpcy5faGVhZCAmJiAhdGhpcy5fdGFpbCkge1xuICAgICAgICAgICAgdGhpcy5faGVhZCA9IGl0ZW07XG4gICAgICAgIH1cbiAgICAgICAgZWxzZSBpZiAoIXRoaXMuX3RhaWwpIHtcbiAgICAgICAgICAgIHRocm93IG5ldyBFcnJvcignSW52YWxpZCBsaXN0Jyk7XG4gICAgICAgIH1cbiAgICAgICAgZWxzZSB7XG4gICAgICAgICAgICBpdGVtLnByZXZpb3VzID0gdGhpcy5fdGFpbDtcbiAgICAgICAgICAgIHRoaXMuX3RhaWwubmV4dCA9IGl0ZW07XG4gICAgICAgIH1cbiAgICAgICAgdGhpcy5fdGFpbCA9IGl0ZW07XG4gICAgICAgIHRoaXMuX3N0YXRlKys7XG4gICAgfVxuICAgIHJlbW92ZUl0ZW0oaXRlbSkge1xuICAgICAgICBpZiAoaXRlbSA9PT0gdGhpcy5faGVhZCAmJiBpdGVtID09PSB0aGlzLl90YWlsKSB7XG4gICAgICAgICAgICB0aGlzLl9oZWFkID0gdW5kZWZpbmVkO1xuICAgICAgICAgICAgdGhpcy5fdGFpbCA9IHVuZGVmaW5lZDtcbiAgICAgICAgfVxuICAgICAgICBlbHNlIGlmIChpdGVtID09PSB0aGlzLl9oZWFkKSB7XG4gICAgICAgICAgICAvLyBUaGlzIGNhbiBvbmx5IGhhcHBlbmVkIGlmIHNpemUgPT09IDEgd2hpY2ggaXMgaGFuZGxlXG4gICAgICAgICAgICAvLyBieSB0aGUgY2FzZSBhYm92ZS5cbiAgICAgICAgICAgIGlmICghaXRlbS5uZXh0KSB7XG4gICAgICAgICAgICAgICAgdGhyb3cgbmV3IEVycm9yKCdJbnZhbGlkIGxpc3QnKTtcbiAgICAgICAgICAgIH1cbiAgICAgICAgICAgIGl0ZW0ubmV4dC5wcmV2aW91cyA9IHVuZGVmaW5lZDtcbiAgICAgICAgICAgIHRoaXMuX2hlYWQgPSBpdGVtLm5leHQ7XG4gICAgICAgIH1cbiAgICAgICAgZWxzZSBpZiAoaXRlbSA9PT0gdGhpcy5fdGFpbCkge1xuICAgICAgICAgICAgLy8gVGhpcyBjYW4gb25seSBoYXBwZW5lZCBpZiBzaXplID09PSAxIHdoaWNoIGlzIGhhbmRsZVxuICAgICAgICAgICAgLy8gYnkgdGhlIGNhc2UgYWJvdmUuXG4gICAgICAgICAgICBpZiAoIWl0ZW0ucHJldmlvdXMpIHtcbiAgICAgICAgICAgICAgICB0aHJvdyBuZXcgRXJyb3IoJ0ludmFsaWQgbGlzdCcpO1xuICAgICAgICAgICAgfVxuICAgICAgICAgICAgaXRlbS5wcmV2aW91cy5uZXh0ID0gdW5kZWZpbmVkO1xuICAgICAgICAgICAgdGhpcy5fdGFpbCA9IGl0ZW0ucHJldmlvdXM7XG4gICAgICAgIH1cbiAgICAgICAgZWxzZSB7XG4gICAgICAgICAgICBjb25zdCBuZXh0ID0gaXRlbS5uZXh0O1xuICAgICAgICAgICAgY29uc3QgcHJldmlvdXMgPSBpdGVtLnByZXZpb3VzO1xuICAgICAgICAgICAgaWYgKCFuZXh0IHx8ICFwcmV2aW91cykge1xuICAgICAgICAgICAgICAgIHRocm93IG5ldyBFcnJvcignSW52YWxpZCBsaXN0Jyk7XG4gICAgICAgICAgICB9XG4gICAgICAgICAgICBuZXh0LnByZXZpb3VzID0gcHJldmlvdXM7XG4gICAgICAgICAgICBwcmV2aW91cy5uZXh0ID0gbmV4dDtcbiAgICAgICAgfVxuICAgICAgICBpdGVtLm5leHQgPSB1bmRlZmluZWQ7XG4gICAgICAgIGl0ZW0ucHJldmlvdXMgPSB1bmRlZmluZWQ7XG4gICAgICAgIHRoaXMuX3N0YXRlKys7XG4gICAgfVxuICAgIHRvdWNoKGl0ZW0sIHRvdWNoKSB7XG4gICAgICAgIGlmICghdGhpcy5faGVhZCB8fCAhdGhpcy5fdGFpbCkge1xuICAgICAgICAgICAgdGhyb3cgbmV3IEVycm9yKCdJbnZhbGlkIGxpc3QnKTtcbiAgICAgICAgfVxuICAgICAgICBpZiAoKHRvdWNoICE9PSBUb3VjaC5GaXJzdCAmJiB0b3VjaCAhPT0gVG91Y2guTGFzdCkpIHtcbiAgICAgICAgICAgIHJldHVybjtcbiAgICAgICAgfVxuICAgICAgICBpZiAodG91Y2ggPT09IFRvdWNoLkZpcnN0KSB7XG4gICAgICAgICAgICBpZiAoaXRlbSA9PT0gdGhpcy5faGVhZCkge1xuICAgICAgICAgICAgICAgIHJldHVybjtcbiAgICAgICAgICAgIH1cbiAgICAgICAgICAgIGNvbnN0IG5leHQgPSBpdGVtLm5leHQ7XG4gICAgICAgICAgICBjb25zdCBwcmV2aW91cyA9IGl0ZW0ucHJldmlvdXM7XG4gICAgICAgICAgICAvLyBVbmxpbmsgdGhlIGl0ZW1cbiAgICAgICAgICAgIGlmIChpdGVtID09PSB0aGlzLl90YWlsKSB7XG4gICAgICAgICAgICAgICAgLy8gcHJldmlvdXMgbXVzdCBiZSBkZWZpbmVkIHNpbmNlIGl0ZW0gd2FzIG5vdCBoZWFkIGJ1dCBpcyB0YWlsXG4gICAgICAgICAgICAgICAgLy8gU28gdGhlcmUgYXJlIG1vcmUgdGhhbiBvbiBpdGVtIGluIHRoZSBtYXBcbiAgICAgICAgICAgICAgICBwcmV2aW91cy5uZXh0ID0gdW5kZWZpbmVkO1xuICAgICAgICAgICAgICAgIHRoaXMuX3RhaWwgPSBwcmV2aW91cztcbiAgICAgICAgICAgIH1cbiAgICAgICAgICAgIGVsc2Uge1xuICAgICAgICAgICAgICAgIC8vIEJvdGggbmV4dCBhbmQgcHJldmlvdXMgYXJlIG5vdCB1bmRlZmluZWQgc2luY2UgaXRlbSB3YXMgbmVpdGhlciBoZWFkIG5vciB0YWlsLlxuICAgICAgICAgICAgICAgIG5leHQucHJldmlvdXMgPSBwcmV2aW91cztcbiAgICAgICAgICAgICAgICBwcmV2aW91cy5uZXh0ID0gbmV4dDtcbiAgICAgICAgICAgIH1cbiAgICAgICAgICAgIC8vIEluc2VydCB0aGUgbm9kZSBhdCBoZWFkXG4gICAgICAgICAgICBpdGVtLnByZXZpb3VzID0gdW5kZWZpbmVkO1xuICAgICAgICAgICAgaXRlbS5uZXh0ID0gdGhpcy5faGVhZDtcbiAgICAgICAgICAgIHRoaXMuX2hlYWQucHJldmlvdXMgPSBpdGVtO1xuICAgICAgICAgICAgdGhpcy5faGVhZCA9IGl0ZW07XG4gICAgICAgICAgICB0aGlzLl9zdGF0ZSsrO1xuICAgICAgICB9XG4gICAgICAgIGVsc2UgaWYgKHRvdWNoID09PSBUb3VjaC5MYXN0KSB7XG4gICAgICAgICAgICBpZiAoaXRlbSA9PT0gdGhpcy5fdGFpbCkge1xuICAgICAgICAgICAgICAgIHJldHVybjtcbiAgICAgICAgICAgIH1cbiAgICAgICAgICAgIGNvbnN0IG5leHQgPSBpdGVtLm5leHQ7XG4gICAgICAgICAgICBjb25zdCBwcmV2aW91cyA9IGl0ZW0ucHJldmlvdXM7XG4gICAgICAgICAgICAvLyBVbmxpbmsgdGhlIGl0ZW0uXG4gICAgICAgICAgICBpZiAoaXRlbSA9PT0gdGhpcy5faGVhZCkge1xuICAgICAgICAgICAgICAgIC8vIG5leHQgbXVzdCBiZSBkZWZpbmVkIHNpbmNlIGl0ZW0gd2FzIG5vdCB0YWlsIGJ1dCBpcyBoZWFkXG4gICAgICAgICAgICAgICAgLy8gU28gdGhlcmUgYXJlIG1vcmUgdGhhbiBvbiBpdGVtIGluIHRoZSBtYXBcbiAgICAgICAgICAgICAgICBuZXh0LnByZXZpb3VzID0gdW5kZWZpbmVkO1xuICAgICAgICAgICAgICAgIHRoaXMuX2hlYWQgPSBuZXh0O1xuICAgICAgICAgICAgfVxuICAgICAgICAgICAgZWxzZSB7XG4gICAgICAgICAgICAgICAgLy8gQm90aCBuZXh0IGFuZCBwcmV2aW91cyBhcmUgbm90IHVuZGVmaW5lZCBzaW5jZSBpdGVtIHdhcyBuZWl0aGVyIGhlYWQgbm9yIHRhaWwuXG4gICAgICAgICAgICAgICAgbmV4dC5wcmV2aW91cyA9IHByZXZpb3VzO1xuICAgICAgICAgICAgICAgIHByZXZpb3VzLm5leHQgPSBuZXh0O1xuICAgICAgICAgICAgfVxuICAgICAgICAgICAgaXRlbS5uZXh0ID0gdW5kZWZpbmVkO1xuICAgICAgICAgICAgaXRlbS5wcmV2aW91cyA9IHRoaXMuX3RhaWw7XG4gICAgICAgICAgICB0aGlzLl90YWlsLm5leHQgPSBpdGVtO1xuICAgICAgICAgICAgdGhpcy5fdGFpbCA9IGl0ZW07XG4gICAgICAgICAgICB0aGlzLl9zdGF0ZSsrO1xuICAgICAgICB9XG4gICAgfVxuICAgIHRvSlNPTigpIHtcbiAgICAgICAgY29uc3QgZGF0YSA9IFtdO1xuICAgICAgICB0aGlzLmZvckVhY2goKHZhbHVlLCBrZXkpID0+IHtcbiAgICAgICAgICAgIGRhdGEucHVzaChba2V5LCB2YWx1ZV0pO1xuICAgICAgICB9KTtcbiAgICAgICAgcmV0dXJuIGRhdGE7XG4gICAgfVxuICAgIGZyb21KU09OKGRhdGEpIHtcbiAgICAgICAgdGhpcy5jbGVhcigpO1xuICAgICAgICBmb3IgKGNvbnN0IFtrZXksIHZhbHVlXSBvZiBkYXRhKSB7XG4gICAgICAgICAgICB0aGlzLnNldChrZXksIHZhbHVlKTtcbiAgICAgICAgfVxuICAgIH1cbn1cbmV4cG9ydHMuTGlua2VkTWFwID0gTGlua2VkTWFwO1xuY2xhc3MgTFJVQ2FjaGUgZXh0ZW5kcyBMaW5rZWRNYXAge1xuICAgIGNvbnN0cnVjdG9yKGxpbWl0LCByYXRpbyA9IDEpIHtcbiAgICAgICAgc3VwZXIoKTtcbiAgICAgICAgdGhpcy5fbGltaXQgPSBsaW1pdDtcbiAgICAgICAgdGhpcy5fcmF0aW8gPSBNYXRoLm1pbihNYXRoLm1heCgwLCByYXRpbyksIDEpO1xuICAgIH1cbiAgICBnZXQgbGltaXQoKSB7XG4gICAgICAgIHJldHVybiB0aGlzLl9saW1pdDtcbiAgICB9XG4gICAgc2V0IGxpbWl0KGxpbWl0KSB7XG4gICAgICAgIHRoaXMuX2xpbWl0ID0gbGltaXQ7XG4gICAgICAgIHRoaXMuY2hlY2tUcmltKCk7XG4gICAgfVxuICAgIGdldCByYXRpbygpIHtcbiAgICAgICAgcmV0dXJuIHRoaXMuX3JhdGlvO1xuICAgIH1cbiAgICBzZXQgcmF0aW8ocmF0aW8pIHtcbiAgICAgICAgdGhpcy5fcmF0aW8gPSBNYXRoLm1pbihNYXRoLm1heCgwLCByYXRpbyksIDEpO1xuICAgICAgICB0aGlzLmNoZWNrVHJpbSgpO1xuICAgIH1cbiAgICBnZXQoa2V5LCB0b3VjaCA9IFRvdWNoLkFzTmV3KSB7XG4gICAgICAgIHJldHVybiBzdXBlci5nZXQoa2V5LCB0b3VjaCk7XG4gICAgfVxuICAgIHBlZWsoa2V5KSB7XG4gICAgICAgIHJldHVybiBzdXBlci5nZXQoa2V5LCBUb3VjaC5Ob25lKTtcbiAgICB9XG4gICAgc2V0KGtleSwgdmFsdWUpIHtcbiAgICAgICAgc3VwZXIuc2V0KGtleSwgdmFsdWUsIFRvdWNoLkxhc3QpO1xuICAgICAgICB0aGlzLmNoZWNrVHJpbSgpO1xuICAgICAgICByZXR1cm4gdGhpcztcbiAgICB9XG4gICAgY2hlY2tUcmltKCkge1xuICAgICAgICBpZiAodGhpcy5zaXplID4gdGhpcy5fbGltaXQpIHtcbiAgICAgICAgICAgIHRoaXMudHJpbU9sZChNYXRoLnJvdW5kKHRoaXMuX2xpbWl0ICogdGhpcy5fcmF0aW8pKTtcbiAgICAgICAgfVxuICAgIH1cbn1cbmV4cG9ydHMuTFJVQ2FjaGUgPSBMUlVDYWNoZTtcbiIsICJcInVzZSBzdHJpY3RcIjtcbi8qLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tXG4gKiAgQ29weXJpZ2h0IChjKSBNaWNyb3NvZnQgQ29ycG9yYXRpb24uIEFsbCByaWdodHMgcmVzZXJ2ZWQuXG4gKiAgTGljZW5zZWQgdW5kZXIgdGhlIE1JVCBMaWNlbnNlLiBTZWUgTGljZW5zZS50eHQgaW4gdGhlIHByb2plY3Qgcm9vdCBmb3IgbGljZW5zZSBpbmZvcm1hdGlvbi5cbiAqLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0qL1xuT2JqZWN0LmRlZmluZVByb3BlcnR5KGV4cG9ydHMsIFwiX19lc01vZHVsZVwiLCB7IHZhbHVlOiB0cnVlIH0pO1xuZXhwb3J0cy5EaXNwb3NhYmxlID0gdm9pZCAwO1xudmFyIERpc3Bvc2FibGU7XG4oZnVuY3Rpb24gKERpc3Bvc2FibGUpIHtcbiAgICBmdW5jdGlvbiBjcmVhdGUoZnVuYykge1xuICAgICAgICByZXR1cm4ge1xuICAgICAgICAgICAgZGlzcG9zZTogZnVuY1xuICAgICAgICB9O1xuICAgIH1cbiAgICBEaXNwb3NhYmxlLmNyZWF0ZSA9IGNyZWF0ZTtcbn0pKERpc3Bvc2FibGUgfHwgKGV4cG9ydHMuRGlzcG9zYWJsZSA9IERpc3Bvc2FibGUgPSB7fSkpO1xuIiwgIlwidXNlIHN0cmljdFwiO1xuLyogLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS1cbiAqIENvcHlyaWdodCAoYykgTWljcm9zb2Z0IENvcnBvcmF0aW9uLiBBbGwgcmlnaHRzIHJlc2VydmVkLlxuICogTGljZW5zZWQgdW5kZXIgdGhlIE1JVCBMaWNlbnNlLiBTZWUgTGljZW5zZS50eHQgaW4gdGhlIHByb2plY3Qgcm9vdCBmb3IgbGljZW5zZSBpbmZvcm1hdGlvbi5cbiAqIC0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLSAqL1xuT2JqZWN0LmRlZmluZVByb3BlcnR5KGV4cG9ydHMsIFwiX19lc01vZHVsZVwiLCB7IHZhbHVlOiB0cnVlIH0pO1xubGV0IF9yYWw7XG5mdW5jdGlvbiBSQUwoKSB7XG4gICAgaWYgKF9yYWwgPT09IHVuZGVmaW5lZCkge1xuICAgICAgICB0aHJvdyBuZXcgRXJyb3IoYE5vIHJ1bnRpbWUgYWJzdHJhY3Rpb24gbGF5ZXIgaW5zdGFsbGVkYCk7XG4gICAgfVxuICAgIHJldHVybiBfcmFsO1xufVxuKGZ1bmN0aW9uIChSQUwpIHtcbiAgICBmdW5jdGlvbiBpbnN0YWxsKHJhbCkge1xuICAgICAgICBpZiAocmFsID09PSB1bmRlZmluZWQpIHtcbiAgICAgICAgICAgIHRocm93IG5ldyBFcnJvcihgTm8gcnVudGltZSBhYnN0cmFjdGlvbiBsYXllciBwcm92aWRlZGApO1xuICAgICAgICB9XG4gICAgICAgIF9yYWwgPSByYWw7XG4gICAgfVxuICAgIFJBTC5pbnN0YWxsID0gaW5zdGFsbDtcbn0pKFJBTCB8fCAoUkFMID0ge30pKTtcbmV4cG9ydHMuZGVmYXVsdCA9IFJBTDtcbiIsICJcInVzZSBzdHJpY3RcIjtcbi8qIC0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tXG4gKiBDb3B5cmlnaHQgKGMpIE1pY3Jvc29mdCBDb3Jwb3JhdGlvbi4gQWxsIHJpZ2h0cyByZXNlcnZlZC5cbiAqIExpY2Vuc2VkIHVuZGVyIHRoZSBNSVQgTGljZW5zZS4gU2VlIExpY2Vuc2UudHh0IGluIHRoZSBwcm9qZWN0IHJvb3QgZm9yIGxpY2Vuc2UgaW5mb3JtYXRpb24uXG4gKiAtLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0gKi9cbk9iamVjdC5kZWZpbmVQcm9wZXJ0eShleHBvcnRzLCBcIl9fZXNNb2R1bGVcIiwgeyB2YWx1ZTogdHJ1ZSB9KTtcbmV4cG9ydHMuRW1pdHRlciA9IGV4cG9ydHMuRXZlbnQgPSB2b2lkIDA7XG5jb25zdCByYWxfMSA9IHJlcXVpcmUoXCIuL3JhbFwiKTtcbnZhciBFdmVudDtcbihmdW5jdGlvbiAoRXZlbnQpIHtcbiAgICBjb25zdCBfZGlzcG9zYWJsZSA9IHsgZGlzcG9zZSgpIHsgfSB9O1xuICAgIEV2ZW50Lk5vbmUgPSBmdW5jdGlvbiAoKSB7IHJldHVybiBfZGlzcG9zYWJsZTsgfTtcbn0pKEV2ZW50IHx8IChleHBvcnRzLkV2ZW50ID0gRXZlbnQgPSB7fSkpO1xuY2xhc3MgQ2FsbGJhY2tMaXN0IHtcbiAgICBhZGQoY2FsbGJhY2ssIGNvbnRleHQgPSBudWxsLCBidWNrZXQpIHtcbiAgICAgICAgaWYgKCF0aGlzLl9jYWxsYmFja3MpIHtcbiAgICAgICAgICAgIHRoaXMuX2NhbGxiYWNrcyA9IFtdO1xuICAgICAgICAgICAgdGhpcy5fY29udGV4dHMgPSBbXTtcbiAgICAgICAgfVxuICAgICAgICB0aGlzLl9jYWxsYmFja3MucHVzaChjYWxsYmFjayk7XG4gICAgICAgIHRoaXMuX2NvbnRleHRzLnB1c2goY29udGV4dCk7XG4gICAgICAgIGlmIChBcnJheS5pc0FycmF5KGJ1Y2tldCkpIHtcbiAgICAgICAgICAgIGJ1Y2tldC5wdXNoKHsgZGlzcG9zZTogKCkgPT4gdGhpcy5yZW1vdmUoY2FsbGJhY2ssIGNvbnRleHQpIH0pO1xuICAgICAgICB9XG4gICAgfVxuICAgIHJlbW92ZShjYWxsYmFjaywgY29udGV4dCA9IG51bGwpIHtcbiAgICAgICAgaWYgKCF0aGlzLl9jYWxsYmFja3MpIHtcbiAgICAgICAgICAgIHJldHVybjtcbiAgICAgICAgfVxuICAgICAgICBsZXQgZm91bmRDYWxsYmFja1dpdGhEaWZmZXJlbnRDb250ZXh0ID0gZmFsc2U7XG4gICAgICAgIGZvciAobGV0IGkgPSAwLCBsZW4gPSB0aGlzLl9jYWxsYmFja3MubGVuZ3RoOyBpIDwgbGVuOyBpKyspIHtcbiAgICAgICAgICAgIGlmICh0aGlzLl9jYWxsYmFja3NbaV0gPT09IGNhbGxiYWNrKSB7XG4gICAgICAgICAgICAgICAgaWYgKHRoaXMuX2NvbnRleHRzW2ldID09PSBjb250ZXh0KSB7XG4gICAgICAgICAgICAgICAgICAgIC8vIGNhbGxiYWNrICYgY29udGV4dCBtYXRjaCA9PiByZW1vdmUgaXRcbiAgICAgICAgICAgICAgICAgICAgdGhpcy5fY2FsbGJhY2tzLnNwbGljZShpLCAxKTtcbiAgICAgICAgICAgICAgICAgICAgdGhpcy5fY29udGV4dHMuc3BsaWNlKGksIDEpO1xuICAgICAgICAgICAgICAgICAgICByZXR1cm47XG4gICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgIGVsc2Uge1xuICAgICAgICAgICAgICAgICAgICBmb3VuZENhbGxiYWNrV2l0aERpZmZlcmVudENvbnRleHQgPSB0cnVlO1xuICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgIH1cbiAgICAgICAgfVxuICAgICAgICBpZiAoZm91bmRDYWxsYmFja1dpdGhEaWZmZXJlbnRDb250ZXh0KSB7XG4gICAgICAgICAgICB0aHJvdyBuZXcgRXJyb3IoJ1doZW4gYWRkaW5nIGEgbGlzdGVuZXIgd2l0aCBhIGNvbnRleHQsIHlvdSBzaG91bGQgcmVtb3ZlIGl0IHdpdGggdGhlIHNhbWUgY29udGV4dCcpO1xuICAgICAgICB9XG4gICAgfVxuICAgIGludm9rZSguLi5hcmdzKSB7XG4gICAgICAgIGlmICghdGhpcy5fY2FsbGJhY2tzKSB7XG4gICAgICAgICAgICByZXR1cm4gW107XG4gICAgICAgIH1cbiAgICAgICAgY29uc3QgcmV0ID0gW10sIGNhbGxiYWNrcyA9IHRoaXMuX2NhbGxiYWNrcy5zbGljZSgwKSwgY29udGV4dHMgPSB0aGlzLl9jb250ZXh0cy5zbGljZSgwKTtcbiAgICAgICAgZm9yIChsZXQgaSA9IDAsIGxlbiA9IGNhbGxiYWNrcy5sZW5ndGg7IGkgPCBsZW47IGkrKykge1xuICAgICAgICAgICAgdHJ5IHtcbiAgICAgICAgICAgICAgICByZXQucHVzaChjYWxsYmFja3NbaV0uYXBwbHkoY29udGV4dHNbaV0sIGFyZ3MpKTtcbiAgICAgICAgICAgIH1cbiAgICAgICAgICAgIGNhdGNoIChlKSB7XG4gICAgICAgICAgICAgICAgLy8gZXNsaW50LWRpc2FibGUtbmV4dC1saW5lIG5vLWNvbnNvbGVcbiAgICAgICAgICAgICAgICAoMCwgcmFsXzEuZGVmYXVsdCkoKS5jb25zb2xlLmVycm9yKGUpO1xuICAgICAgICAgICAgfVxuICAgICAgICB9XG4gICAgICAgIHJldHVybiByZXQ7XG4gICAgfVxuICAgIGlzRW1wdHkoKSB7XG4gICAgICAgIHJldHVybiAhdGhpcy5fY2FsbGJhY2tzIHx8IHRoaXMuX2NhbGxiYWNrcy5sZW5ndGggPT09IDA7XG4gICAgfVxuICAgIGRpc3Bvc2UoKSB7XG4gICAgICAgIHRoaXMuX2NhbGxiYWNrcyA9IHVuZGVmaW5lZDtcbiAgICAgICAgdGhpcy5fY29udGV4dHMgPSB1bmRlZmluZWQ7XG4gICAgfVxufVxuY2xhc3MgRW1pdHRlciB7XG4gICAgY29uc3RydWN0b3IoX29wdGlvbnMpIHtcbiAgICAgICAgdGhpcy5fb3B0aW9ucyA9IF9vcHRpb25zO1xuICAgIH1cbiAgICAvKipcbiAgICAgKiBGb3IgdGhlIHB1YmxpYyB0byBhbGxvdyB0byBzdWJzY3JpYmVcbiAgICAgKiB0byBldmVudHMgZnJvbSB0aGlzIEVtaXR0ZXJcbiAgICAgKi9cbiAgICBnZXQgZXZlbnQoKSB7XG4gICAgICAgIGlmICghdGhpcy5fZXZlbnQpIHtcbiAgICAgICAgICAgIHRoaXMuX2V2ZW50ID0gKGxpc3RlbmVyLCB0aGlzQXJncywgZGlzcG9zYWJsZXMpID0+IHtcbiAgICAgICAgICAgICAgICBpZiAoIXRoaXMuX2NhbGxiYWNrcykge1xuICAgICAgICAgICAgICAgICAgICB0aGlzLl9jYWxsYmFja3MgPSBuZXcgQ2FsbGJhY2tMaXN0KCk7XG4gICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgIGlmICh0aGlzLl9vcHRpb25zICYmIHRoaXMuX29wdGlvbnMub25GaXJzdExpc3RlbmVyQWRkICYmIHRoaXMuX2NhbGxiYWNrcy5pc0VtcHR5KCkpIHtcbiAgICAgICAgICAgICAgICAgICAgdGhpcy5fb3B0aW9ucy5vbkZpcnN0TGlzdGVuZXJBZGQodGhpcyk7XG4gICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgIHRoaXMuX2NhbGxiYWNrcy5hZGQobGlzdGVuZXIsIHRoaXNBcmdzKTtcbiAgICAgICAgICAgICAgICBjb25zdCByZXN1bHQgPSB7XG4gICAgICAgICAgICAgICAgICAgIGRpc3Bvc2U6ICgpID0+IHtcbiAgICAgICAgICAgICAgICAgICAgICAgIGlmICghdGhpcy5fY2FsbGJhY2tzKSB7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgLy8gZGlzcG9zYWJsZSBpcyBkaXNwb3NlZCBhZnRlciBlbWl0dGVyIGlzIGRpc3Bvc2VkLlxuICAgICAgICAgICAgICAgICAgICAgICAgICAgIHJldHVybjtcbiAgICAgICAgICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICAgICAgICAgIHRoaXMuX2NhbGxiYWNrcy5yZW1vdmUobGlzdGVuZXIsIHRoaXNBcmdzKTtcbiAgICAgICAgICAgICAgICAgICAgICAgIHJlc3VsdC5kaXNwb3NlID0gRW1pdHRlci5fbm9vcDtcbiAgICAgICAgICAgICAgICAgICAgICAgIGlmICh0aGlzLl9vcHRpb25zICYmIHRoaXMuX29wdGlvbnMub25MYXN0TGlzdGVuZXJSZW1vdmUgJiYgdGhpcy5fY2FsbGJhY2tzLmlzRW1wdHkoKSkge1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgIHRoaXMuX29wdGlvbnMub25MYXN0TGlzdGVuZXJSZW1vdmUodGhpcyk7XG4gICAgICAgICAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICB9O1xuICAgICAgICAgICAgICAgIGlmIChBcnJheS5pc0FycmF5KGRpc3Bvc2FibGVzKSkge1xuICAgICAgICAgICAgICAgICAgICBkaXNwb3NhYmxlcy5wdXNoKHJlc3VsdCk7XG4gICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgIHJldHVybiByZXN1bHQ7XG4gICAgICAgICAgICB9O1xuICAgICAgICB9XG4gICAgICAgIHJldHVybiB0aGlzLl9ldmVudDtcbiAgICB9XG4gICAgLyoqXG4gICAgICogVG8gYmUga2VwdCBwcml2YXRlIHRvIGZpcmUgYW4gZXZlbnQgdG9cbiAgICAgKiBzdWJzY3JpYmVyc1xuICAgICAqL1xuICAgIGZpcmUoZXZlbnQpIHtcbiAgICAgICAgaWYgKHRoaXMuX2NhbGxiYWNrcykge1xuICAgICAgICAgICAgdGhpcy5fY2FsbGJhY2tzLmludm9rZS5jYWxsKHRoaXMuX2NhbGxiYWNrcywgZXZlbnQpO1xuICAgICAgICB9XG4gICAgfVxuICAgIGRpc3Bvc2UoKSB7XG4gICAgICAgIGlmICh0aGlzLl9jYWxsYmFja3MpIHtcbiAgICAgICAgICAgIHRoaXMuX2NhbGxiYWNrcy5kaXNwb3NlKCk7XG4gICAgICAgICAgICB0aGlzLl9jYWxsYmFja3MgPSB1bmRlZmluZWQ7XG4gICAgICAgIH1cbiAgICB9XG59XG5leHBvcnRzLkVtaXR0ZXIgPSBFbWl0dGVyO1xuRW1pdHRlci5fbm9vcCA9IGZ1bmN0aW9uICgpIHsgfTtcbiIsICJcInVzZSBzdHJpY3RcIjtcbi8qLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tXG4gKiAgQ29weXJpZ2h0IChjKSBNaWNyb3NvZnQgQ29ycG9yYXRpb24uIEFsbCByaWdodHMgcmVzZXJ2ZWQuXG4gKiAgTGljZW5zZWQgdW5kZXIgdGhlIE1JVCBMaWNlbnNlLiBTZWUgTGljZW5zZS50eHQgaW4gdGhlIHByb2plY3Qgcm9vdCBmb3IgbGljZW5zZSBpbmZvcm1hdGlvbi5cbiAqLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0qL1xuT2JqZWN0LmRlZmluZVByb3BlcnR5KGV4cG9ydHMsIFwiX19lc01vZHVsZVwiLCB7IHZhbHVlOiB0cnVlIH0pO1xuZXhwb3J0cy5DYW5jZWxsYXRpb25Ub2tlblNvdXJjZSA9IGV4cG9ydHMuQ2FuY2VsbGF0aW9uVG9rZW4gPSB2b2lkIDA7XG5jb25zdCByYWxfMSA9IHJlcXVpcmUoXCIuL3JhbFwiKTtcbmNvbnN0IElzID0gcmVxdWlyZShcIi4vaXNcIik7XG5jb25zdCBldmVudHNfMSA9IHJlcXVpcmUoXCIuL2V2ZW50c1wiKTtcbnZhciBDYW5jZWxsYXRpb25Ub2tlbjtcbihmdW5jdGlvbiAoQ2FuY2VsbGF0aW9uVG9rZW4pIHtcbiAgICBDYW5jZWxsYXRpb25Ub2tlbi5Ob25lID0gT2JqZWN0LmZyZWV6ZSh7XG4gICAgICAgIGlzQ2FuY2VsbGF0aW9uUmVxdWVzdGVkOiBmYWxzZSxcbiAgICAgICAgb25DYW5jZWxsYXRpb25SZXF1ZXN0ZWQ6IGV2ZW50c18xLkV2ZW50Lk5vbmVcbiAgICB9KTtcbiAgICBDYW5jZWxsYXRpb25Ub2tlbi5DYW5jZWxsZWQgPSBPYmplY3QuZnJlZXplKHtcbiAgICAgICAgaXNDYW5jZWxsYXRpb25SZXF1ZXN0ZWQ6IHRydWUsXG4gICAgICAgIG9uQ2FuY2VsbGF0aW9uUmVxdWVzdGVkOiBldmVudHNfMS5FdmVudC5Ob25lXG4gICAgfSk7XG4gICAgZnVuY3Rpb24gaXModmFsdWUpIHtcbiAgICAgICAgY29uc3QgY2FuZGlkYXRlID0gdmFsdWU7XG4gICAgICAgIHJldHVybiBjYW5kaWRhdGUgJiYgKGNhbmRpZGF0ZSA9PT0gQ2FuY2VsbGF0aW9uVG9rZW4uTm9uZVxuICAgICAgICAgICAgfHwgY2FuZGlkYXRlID09PSBDYW5jZWxsYXRpb25Ub2tlbi5DYW5jZWxsZWRcbiAgICAgICAgICAgIHx8IChJcy5ib29sZWFuKGNhbmRpZGF0ZS5pc0NhbmNlbGxhdGlvblJlcXVlc3RlZCkgJiYgISFjYW5kaWRhdGUub25DYW5jZWxsYXRpb25SZXF1ZXN0ZWQpKTtcbiAgICB9XG4gICAgQ2FuY2VsbGF0aW9uVG9rZW4uaXMgPSBpcztcbn0pKENhbmNlbGxhdGlvblRva2VuIHx8IChleHBvcnRzLkNhbmNlbGxhdGlvblRva2VuID0gQ2FuY2VsbGF0aW9uVG9rZW4gPSB7fSkpO1xuY29uc3Qgc2hvcnRjdXRFdmVudCA9IE9iamVjdC5mcmVlemUoZnVuY3Rpb24gKGNhbGxiYWNrLCBjb250ZXh0KSB7XG4gICAgY29uc3QgaGFuZGxlID0gKDAsIHJhbF8xLmRlZmF1bHQpKCkudGltZXIuc2V0VGltZW91dChjYWxsYmFjay5iaW5kKGNvbnRleHQpLCAwKTtcbiAgICByZXR1cm4geyBkaXNwb3NlKCkgeyBoYW5kbGUuZGlzcG9zZSgpOyB9IH07XG59KTtcbmNsYXNzIE11dGFibGVUb2tlbiB7XG4gICAgY29uc3RydWN0b3IoKSB7XG4gICAgICAgIHRoaXMuX2lzQ2FuY2VsbGVkID0gZmFsc2U7XG4gICAgfVxuICAgIGNhbmNlbCgpIHtcbiAgICAgICAgaWYgKCF0aGlzLl9pc0NhbmNlbGxlZCkge1xuICAgICAgICAgICAgdGhpcy5faXNDYW5jZWxsZWQgPSB0cnVlO1xuICAgICAgICAgICAgaWYgKHRoaXMuX2VtaXR0ZXIpIHtcbiAgICAgICAgICAgICAgICB0aGlzLl9lbWl0dGVyLmZpcmUodW5kZWZpbmVkKTtcbiAgICAgICAgICAgICAgICB0aGlzLmRpc3Bvc2UoKTtcbiAgICAgICAgICAgIH1cbiAgICAgICAgfVxuICAgIH1cbiAgICBnZXQgaXNDYW5jZWxsYXRpb25SZXF1ZXN0ZWQoKSB7XG4gICAgICAgIHJldHVybiB0aGlzLl9pc0NhbmNlbGxlZDtcbiAgICB9XG4gICAgZ2V0IG9uQ2FuY2VsbGF0aW9uUmVxdWVzdGVkKCkge1xuICAgICAgICBpZiAodGhpcy5faXNDYW5jZWxsZWQpIHtcbiAgICAgICAgICAgIHJldHVybiBzaG9ydGN1dEV2ZW50O1xuICAgICAgICB9XG4gICAgICAgIGlmICghdGhpcy5fZW1pdHRlcikge1xuICAgICAgICAgICAgdGhpcy5fZW1pdHRlciA9IG5ldyBldmVudHNfMS5FbWl0dGVyKCk7XG4gICAgICAgIH1cbiAgICAgICAgcmV0dXJuIHRoaXMuX2VtaXR0ZXIuZXZlbnQ7XG4gICAgfVxuICAgIGRpc3Bvc2UoKSB7XG4gICAgICAgIGlmICh0aGlzLl9lbWl0dGVyKSB7XG4gICAgICAgICAgICB0aGlzLl9lbWl0dGVyLmRpc3Bvc2UoKTtcbiAgICAgICAgICAgIHRoaXMuX2VtaXR0ZXIgPSB1bmRlZmluZWQ7XG4gICAgICAgIH1cbiAgICB9XG59XG5jbGFzcyBDYW5jZWxsYXRpb25Ub2tlblNvdXJjZSB7XG4gICAgZ2V0IHRva2VuKCkge1xuICAgICAgICBpZiAoIXRoaXMuX3Rva2VuKSB7XG4gICAgICAgICAgICAvLyBiZSBsYXp5IGFuZCBjcmVhdGUgdGhlIHRva2VuIG9ubHkgd2hlblxuICAgICAgICAgICAgLy8gYWN0dWFsbHkgbmVlZGVkXG4gICAgICAgICAgICB0aGlzLl90b2tlbiA9IG5ldyBNdXRhYmxlVG9rZW4oKTtcbiAgICAgICAgfVxuICAgICAgICByZXR1cm4gdGhpcy5fdG9rZW47XG4gICAgfVxuICAgIGNhbmNlbCgpIHtcbiAgICAgICAgaWYgKCF0aGlzLl90b2tlbikge1xuICAgICAgICAgICAgLy8gc2F2ZSBhbiBvYmplY3QgYnkgcmV0dXJuaW5nIHRoZSBkZWZhdWx0XG4gICAgICAgICAgICAvLyBjYW5jZWxsZWQgdG9rZW4gd2hlbiBjYW5jZWxsYXRpb24gaGFwcGVuc1xuICAgICAgICAgICAgLy8gYmVmb3JlIHNvbWVvbmUgYXNrcyBmb3IgdGhlIHRva2VuXG4gICAgICAgICAgICB0aGlzLl90b2tlbiA9IENhbmNlbGxhdGlvblRva2VuLkNhbmNlbGxlZDtcbiAgICAgICAgfVxuICAgICAgICBlbHNlIHtcbiAgICAgICAgICAgIHRoaXMuX3Rva2VuLmNhbmNlbCgpO1xuICAgICAgICB9XG4gICAgfVxuICAgIGRpc3Bvc2UoKSB7XG4gICAgICAgIGlmICghdGhpcy5fdG9rZW4pIHtcbiAgICAgICAgICAgIC8vIGVuc3VyZSB0byBpbml0aWFsaXplIHdpdGggYW4gZW1wdHkgdG9rZW4gaWYgd2UgaGFkIG5vbmVcbiAgICAgICAgICAgIHRoaXMuX3Rva2VuID0gQ2FuY2VsbGF0aW9uVG9rZW4uTm9uZTtcbiAgICAgICAgfVxuICAgICAgICBlbHNlIGlmICh0aGlzLl90b2tlbiBpbnN0YW5jZW9mIE11dGFibGVUb2tlbikge1xuICAgICAgICAgICAgLy8gYWN0dWFsbHkgZGlzcG9zZVxuICAgICAgICAgICAgdGhpcy5fdG9rZW4uZGlzcG9zZSgpO1xuICAgICAgICB9XG4gICAgfVxufVxuZXhwb3J0cy5DYW5jZWxsYXRpb25Ub2tlblNvdXJjZSA9IENhbmNlbGxhdGlvblRva2VuU291cmNlO1xuIiwgIlwidXNlIHN0cmljdFwiO1xuLyogLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS1cbiAqIENvcHlyaWdodCAoYykgTWljcm9zb2Z0IENvcnBvcmF0aW9uLiBBbGwgcmlnaHRzIHJlc2VydmVkLlxuICogTGljZW5zZWQgdW5kZXIgdGhlIE1JVCBMaWNlbnNlLiBTZWUgTGljZW5zZS50eHQgaW4gdGhlIHByb2plY3Qgcm9vdCBmb3IgbGljZW5zZSBpbmZvcm1hdGlvbi5cbiAqIC0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLSAqL1xuT2JqZWN0LmRlZmluZVByb3BlcnR5KGV4cG9ydHMsIFwiX19lc01vZHVsZVwiLCB7IHZhbHVlOiB0cnVlIH0pO1xuZXhwb3J0cy5TaGFyZWRBcnJheVJlY2VpdmVyU3RyYXRlZ3kgPSBleHBvcnRzLlNoYXJlZEFycmF5U2VuZGVyU3RyYXRlZ3kgPSB2b2lkIDA7XG5jb25zdCBjYW5jZWxsYXRpb25fMSA9IHJlcXVpcmUoXCIuL2NhbmNlbGxhdGlvblwiKTtcbnZhciBDYW5jZWxsYXRpb25TdGF0ZTtcbihmdW5jdGlvbiAoQ2FuY2VsbGF0aW9uU3RhdGUpIHtcbiAgICBDYW5jZWxsYXRpb25TdGF0ZS5Db250aW51ZSA9IDA7XG4gICAgQ2FuY2VsbGF0aW9uU3RhdGUuQ2FuY2VsbGVkID0gMTtcbn0pKENhbmNlbGxhdGlvblN0YXRlIHx8IChDYW5jZWxsYXRpb25TdGF0ZSA9IHt9KSk7XG5jbGFzcyBTaGFyZWRBcnJheVNlbmRlclN0cmF0ZWd5IHtcbiAgICBjb25zdHJ1Y3RvcigpIHtcbiAgICAgICAgdGhpcy5idWZmZXJzID0gbmV3IE1hcCgpO1xuICAgIH1cbiAgICBlbmFibGVDYW5jZWxsYXRpb24ocmVxdWVzdCkge1xuICAgICAgICBpZiAocmVxdWVzdC5pZCA9PT0gbnVsbCkge1xuICAgICAgICAgICAgcmV0dXJuO1xuICAgICAgICB9XG4gICAgICAgIGNvbnN0IGJ1ZmZlciA9IG5ldyBTaGFyZWRBcnJheUJ1ZmZlcig0KTtcbiAgICAgICAgY29uc3QgZGF0YSA9IG5ldyBJbnQzMkFycmF5KGJ1ZmZlciwgMCwgMSk7XG4gICAgICAgIGRhdGFbMF0gPSBDYW5jZWxsYXRpb25TdGF0ZS5Db250aW51ZTtcbiAgICAgICAgdGhpcy5idWZmZXJzLnNldChyZXF1ZXN0LmlkLCBidWZmZXIpO1xuICAgICAgICByZXF1ZXN0LiRjYW5jZWxsYXRpb25EYXRhID0gYnVmZmVyO1xuICAgIH1cbiAgICBhc3luYyBzZW5kQ2FuY2VsbGF0aW9uKF9jb25uLCBpZCkge1xuICAgICAgICBjb25zdCBidWZmZXIgPSB0aGlzLmJ1ZmZlcnMuZ2V0KGlkKTtcbiAgICAgICAgaWYgKGJ1ZmZlciA9PT0gdW5kZWZpbmVkKSB7XG4gICAgICAgICAgICByZXR1cm47XG4gICAgICAgIH1cbiAgICAgICAgY29uc3QgZGF0YSA9IG5ldyBJbnQzMkFycmF5KGJ1ZmZlciwgMCwgMSk7XG4gICAgICAgIEF0b21pY3Muc3RvcmUoZGF0YSwgMCwgQ2FuY2VsbGF0aW9uU3RhdGUuQ2FuY2VsbGVkKTtcbiAgICB9XG4gICAgY2xlYW51cChpZCkge1xuICAgICAgICB0aGlzLmJ1ZmZlcnMuZGVsZXRlKGlkKTtcbiAgICB9XG4gICAgZGlzcG9zZSgpIHtcbiAgICAgICAgdGhpcy5idWZmZXJzLmNsZWFyKCk7XG4gICAgfVxufVxuZXhwb3J0cy5TaGFyZWRBcnJheVNlbmRlclN0cmF0ZWd5ID0gU2hhcmVkQXJyYXlTZW5kZXJTdHJhdGVneTtcbmNsYXNzIFNoYXJlZEFycmF5QnVmZmVyQ2FuY2VsbGF0aW9uVG9rZW4ge1xuICAgIGNvbnN0cnVjdG9yKGJ1ZmZlcikge1xuICAgICAgICB0aGlzLmRhdGEgPSBuZXcgSW50MzJBcnJheShidWZmZXIsIDAsIDEpO1xuICAgIH1cbiAgICBnZXQgaXNDYW5jZWxsYXRpb25SZXF1ZXN0ZWQoKSB7XG4gICAgICAgIHJldHVybiBBdG9taWNzLmxvYWQodGhpcy5kYXRhLCAwKSA9PT0gQ2FuY2VsbGF0aW9uU3RhdGUuQ2FuY2VsbGVkO1xuICAgIH1cbiAgICBnZXQgb25DYW5jZWxsYXRpb25SZXF1ZXN0ZWQoKSB7XG4gICAgICAgIHRocm93IG5ldyBFcnJvcihgQ2FuY2VsbGF0aW9uIG92ZXIgU2hhcmVkQXJyYXlCdWZmZXIgZG9lc24ndCBzdXBwb3J0IGNhbmNlbGxhdGlvbiBldmVudHNgKTtcbiAgICB9XG59XG5jbGFzcyBTaGFyZWRBcnJheUJ1ZmZlckNhbmNlbGxhdGlvblRva2VuU291cmNlIHtcbiAgICBjb25zdHJ1Y3RvcihidWZmZXIpIHtcbiAgICAgICAgdGhpcy50b2tlbiA9IG5ldyBTaGFyZWRBcnJheUJ1ZmZlckNhbmNlbGxhdGlvblRva2VuKGJ1ZmZlcik7XG4gICAgfVxuICAgIGNhbmNlbCgpIHtcbiAgICB9XG4gICAgZGlzcG9zZSgpIHtcbiAgICB9XG59XG5jbGFzcyBTaGFyZWRBcnJheVJlY2VpdmVyU3RyYXRlZ3kge1xuICAgIGNvbnN0cnVjdG9yKCkge1xuICAgICAgICB0aGlzLmtpbmQgPSAncmVxdWVzdCc7XG4gICAgfVxuICAgIGNyZWF0ZUNhbmNlbGxhdGlvblRva2VuU291cmNlKHJlcXVlc3QpIHtcbiAgICAgICAgY29uc3QgYnVmZmVyID0gcmVxdWVzdC4kY2FuY2VsbGF0aW9uRGF0YTtcbiAgICAgICAgaWYgKGJ1ZmZlciA9PT0gdW5kZWZpbmVkKSB7XG4gICAgICAgICAgICByZXR1cm4gbmV3IGNhbmNlbGxhdGlvbl8xLkNhbmNlbGxhdGlvblRva2VuU291cmNlKCk7XG4gICAgICAgIH1cbiAgICAgICAgcmV0dXJuIG5ldyBTaGFyZWRBcnJheUJ1ZmZlckNhbmNlbGxhdGlvblRva2VuU291cmNlKGJ1ZmZlcik7XG4gICAgfVxufVxuZXhwb3J0cy5TaGFyZWRBcnJheVJlY2VpdmVyU3RyYXRlZ3kgPSBTaGFyZWRBcnJheVJlY2VpdmVyU3RyYXRlZ3k7XG4iLCAiXCJ1c2Ugc3RyaWN0XCI7XG4vKiAtLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLVxuICogQ29weXJpZ2h0IChjKSBNaWNyb3NvZnQgQ29ycG9yYXRpb24uIEFsbCByaWdodHMgcmVzZXJ2ZWQuXG4gKiBMaWNlbnNlZCB1bmRlciB0aGUgTUlUIExpY2Vuc2UuIFNlZSBMaWNlbnNlLnR4dCBpbiB0aGUgcHJvamVjdCByb290IGZvciBsaWNlbnNlIGluZm9ybWF0aW9uLlxuICogLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tICovXG5PYmplY3QuZGVmaW5lUHJvcGVydHkoZXhwb3J0cywgXCJfX2VzTW9kdWxlXCIsIHsgdmFsdWU6IHRydWUgfSk7XG5leHBvcnRzLlNlbWFwaG9yZSA9IHZvaWQgMDtcbmNvbnN0IHJhbF8xID0gcmVxdWlyZShcIi4vcmFsXCIpO1xuY2xhc3MgU2VtYXBob3JlIHtcbiAgICBjb25zdHJ1Y3RvcihjYXBhY2l0eSA9IDEpIHtcbiAgICAgICAgaWYgKGNhcGFjaXR5IDw9IDApIHtcbiAgICAgICAgICAgIHRocm93IG5ldyBFcnJvcignQ2FwYWNpdHkgbXVzdCBiZSBncmVhdGVyIHRoYW4gMCcpO1xuICAgICAgICB9XG4gICAgICAgIHRoaXMuX2NhcGFjaXR5ID0gY2FwYWNpdHk7XG4gICAgICAgIHRoaXMuX2FjdGl2ZSA9IDA7XG4gICAgICAgIHRoaXMuX3dhaXRpbmcgPSBbXTtcbiAgICB9XG4gICAgbG9jayh0aHVuaykge1xuICAgICAgICByZXR1cm4gbmV3IFByb21pc2UoKHJlc29sdmUsIHJlamVjdCkgPT4ge1xuICAgICAgICAgICAgdGhpcy5fd2FpdGluZy5wdXNoKHsgdGh1bmssIHJlc29sdmUsIHJlamVjdCB9KTtcbiAgICAgICAgICAgIHRoaXMucnVuTmV4dCgpO1xuICAgICAgICB9KTtcbiAgICB9XG4gICAgZ2V0IGFjdGl2ZSgpIHtcbiAgICAgICAgcmV0dXJuIHRoaXMuX2FjdGl2ZTtcbiAgICB9XG4gICAgcnVuTmV4dCgpIHtcbiAgICAgICAgaWYgKHRoaXMuX3dhaXRpbmcubGVuZ3RoID09PSAwIHx8IHRoaXMuX2FjdGl2ZSA9PT0gdGhpcy5fY2FwYWNpdHkpIHtcbiAgICAgICAgICAgIHJldHVybjtcbiAgICAgICAgfVxuICAgICAgICAoMCwgcmFsXzEuZGVmYXVsdCkoKS50aW1lci5zZXRJbW1lZGlhdGUoKCkgPT4gdGhpcy5kb1J1bk5leHQoKSk7XG4gICAgfVxuICAgIGRvUnVuTmV4dCgpIHtcbiAgICAgICAgaWYgKHRoaXMuX3dhaXRpbmcubGVuZ3RoID09PSAwIHx8IHRoaXMuX2FjdGl2ZSA9PT0gdGhpcy5fY2FwYWNpdHkpIHtcbiAgICAgICAgICAgIHJldHVybjtcbiAgICAgICAgfVxuICAgICAgICBjb25zdCBuZXh0ID0gdGhpcy5fd2FpdGluZy5zaGlmdCgpO1xuICAgICAgICB0aGlzLl9hY3RpdmUrKztcbiAgICAgICAgaWYgKHRoaXMuX2FjdGl2ZSA+IHRoaXMuX2NhcGFjaXR5KSB7XG4gICAgICAgICAgICB0aHJvdyBuZXcgRXJyb3IoYFRvIG1hbnkgdGh1bmtzIGFjdGl2ZWApO1xuICAgICAgICB9XG4gICAgICAgIHRyeSB7XG4gICAgICAgICAgICBjb25zdCByZXN1bHQgPSBuZXh0LnRodW5rKCk7XG4gICAgICAgICAgICBpZiAocmVzdWx0IGluc3RhbmNlb2YgUHJvbWlzZSkge1xuICAgICAgICAgICAgICAgIHJlc3VsdC50aGVuKCh2YWx1ZSkgPT4ge1xuICAgICAgICAgICAgICAgICAgICB0aGlzLl9hY3RpdmUtLTtcbiAgICAgICAgICAgICAgICAgICAgbmV4dC5yZXNvbHZlKHZhbHVlKTtcbiAgICAgICAgICAgICAgICAgICAgdGhpcy5ydW5OZXh0KCk7XG4gICAgICAgICAgICAgICAgfSwgKGVycikgPT4ge1xuICAgICAgICAgICAgICAgICAgICB0aGlzLl9hY3RpdmUtLTtcbiAgICAgICAgICAgICAgICAgICAgbmV4dC5yZWplY3QoZXJyKTtcbiAgICAgICAgICAgICAgICAgICAgdGhpcy5ydW5OZXh0KCk7XG4gICAgICAgICAgICAgICAgfSk7XG4gICAgICAgICAgICB9XG4gICAgICAgICAgICBlbHNlIHtcbiAgICAgICAgICAgICAgICB0aGlzLl9hY3RpdmUtLTtcbiAgICAgICAgICAgICAgICBuZXh0LnJlc29sdmUocmVzdWx0KTtcbiAgICAgICAgICAgICAgICB0aGlzLnJ1bk5leHQoKTtcbiAgICAgICAgICAgIH1cbiAgICAgICAgfVxuICAgICAgICBjYXRjaCAoZXJyKSB7XG4gICAgICAgICAgICB0aGlzLl9hY3RpdmUtLTtcbiAgICAgICAgICAgIG5leHQucmVqZWN0KGVycik7XG4gICAgICAgICAgICB0aGlzLnJ1bk5leHQoKTtcbiAgICAgICAgfVxuICAgIH1cbn1cbmV4cG9ydHMuU2VtYXBob3JlID0gU2VtYXBob3JlO1xuIiwgIlwidXNlIHN0cmljdFwiO1xuLyogLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS1cbiAqIENvcHlyaWdodCAoYykgTWljcm9zb2Z0IENvcnBvcmF0aW9uLiBBbGwgcmlnaHRzIHJlc2VydmVkLlxuICogTGljZW5zZWQgdW5kZXIgdGhlIE1JVCBMaWNlbnNlLiBTZWUgTGljZW5zZS50eHQgaW4gdGhlIHByb2plY3Qgcm9vdCBmb3IgbGljZW5zZSBpbmZvcm1hdGlvbi5cbiAqIC0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLSAqL1xuT2JqZWN0LmRlZmluZVByb3BlcnR5KGV4cG9ydHMsIFwiX19lc01vZHVsZVwiLCB7IHZhbHVlOiB0cnVlIH0pO1xuZXhwb3J0cy5SZWFkYWJsZVN0cmVhbU1lc3NhZ2VSZWFkZXIgPSBleHBvcnRzLkFic3RyYWN0TWVzc2FnZVJlYWRlciA9IGV4cG9ydHMuTWVzc2FnZVJlYWRlciA9IHZvaWQgMDtcbmNvbnN0IHJhbF8xID0gcmVxdWlyZShcIi4vcmFsXCIpO1xuY29uc3QgSXMgPSByZXF1aXJlKFwiLi9pc1wiKTtcbmNvbnN0IGV2ZW50c18xID0gcmVxdWlyZShcIi4vZXZlbnRzXCIpO1xuY29uc3Qgc2VtYXBob3JlXzEgPSByZXF1aXJlKFwiLi9zZW1hcGhvcmVcIik7XG52YXIgTWVzc2FnZVJlYWRlcjtcbihmdW5jdGlvbiAoTWVzc2FnZVJlYWRlcikge1xuICAgIGZ1bmN0aW9uIGlzKHZhbHVlKSB7XG4gICAgICAgIGxldCBjYW5kaWRhdGUgPSB2YWx1ZTtcbiAgICAgICAgcmV0dXJuIGNhbmRpZGF0ZSAmJiBJcy5mdW5jKGNhbmRpZGF0ZS5saXN0ZW4pICYmIElzLmZ1bmMoY2FuZGlkYXRlLmRpc3Bvc2UpICYmXG4gICAgICAgICAgICBJcy5mdW5jKGNhbmRpZGF0ZS5vbkVycm9yKSAmJiBJcy5mdW5jKGNhbmRpZGF0ZS5vbkNsb3NlKSAmJiBJcy5mdW5jKGNhbmRpZGF0ZS5vblBhcnRpYWxNZXNzYWdlKTtcbiAgICB9XG4gICAgTWVzc2FnZVJlYWRlci5pcyA9IGlzO1xufSkoTWVzc2FnZVJlYWRlciB8fCAoZXhwb3J0cy5NZXNzYWdlUmVhZGVyID0gTWVzc2FnZVJlYWRlciA9IHt9KSk7XG5jbGFzcyBBYnN0cmFjdE1lc3NhZ2VSZWFkZXIge1xuICAgIGNvbnN0cnVjdG9yKCkge1xuICAgICAgICB0aGlzLmVycm9yRW1pdHRlciA9IG5ldyBldmVudHNfMS5FbWl0dGVyKCk7XG4gICAgICAgIHRoaXMuY2xvc2VFbWl0dGVyID0gbmV3IGV2ZW50c18xLkVtaXR0ZXIoKTtcbiAgICAgICAgdGhpcy5wYXJ0aWFsTWVzc2FnZUVtaXR0ZXIgPSBuZXcgZXZlbnRzXzEuRW1pdHRlcigpO1xuICAgIH1cbiAgICBkaXNwb3NlKCkge1xuICAgICAgICB0aGlzLmVycm9yRW1pdHRlci5kaXNwb3NlKCk7XG4gICAgICAgIHRoaXMuY2xvc2VFbWl0dGVyLmRpc3Bvc2UoKTtcbiAgICB9XG4gICAgZ2V0IG9uRXJyb3IoKSB7XG4gICAgICAgIHJldHVybiB0aGlzLmVycm9yRW1pdHRlci5ldmVudDtcbiAgICB9XG4gICAgZmlyZUVycm9yKGVycm9yKSB7XG4gICAgICAgIHRoaXMuZXJyb3JFbWl0dGVyLmZpcmUodGhpcy5hc0Vycm9yKGVycm9yKSk7XG4gICAgfVxuICAgIGdldCBvbkNsb3NlKCkge1xuICAgICAgICByZXR1cm4gdGhpcy5jbG9zZUVtaXR0ZXIuZXZlbnQ7XG4gICAgfVxuICAgIGZpcmVDbG9zZSgpIHtcbiAgICAgICAgdGhpcy5jbG9zZUVtaXR0ZXIuZmlyZSh1bmRlZmluZWQpO1xuICAgIH1cbiAgICBnZXQgb25QYXJ0aWFsTWVzc2FnZSgpIHtcbiAgICAgICAgcmV0dXJuIHRoaXMucGFydGlhbE1lc3NhZ2VFbWl0dGVyLmV2ZW50O1xuICAgIH1cbiAgICBmaXJlUGFydGlhbE1lc3NhZ2UoaW5mbykge1xuICAgICAgICB0aGlzLnBhcnRpYWxNZXNzYWdlRW1pdHRlci5maXJlKGluZm8pO1xuICAgIH1cbiAgICBhc0Vycm9yKGVycm9yKSB7XG4gICAgICAgIGlmIChlcnJvciBpbnN0YW5jZW9mIEVycm9yKSB7XG4gICAgICAgICAgICByZXR1cm4gZXJyb3I7XG4gICAgICAgIH1cbiAgICAgICAgZWxzZSB7XG4gICAgICAgICAgICByZXR1cm4gbmV3IEVycm9yKGBSZWFkZXIgcmVjZWl2ZWQgZXJyb3IuIFJlYXNvbjogJHtJcy5zdHJpbmcoZXJyb3IubWVzc2FnZSkgPyBlcnJvci5tZXNzYWdlIDogJ3Vua25vd24nfWApO1xuICAgICAgICB9XG4gICAgfVxufVxuZXhwb3J0cy5BYnN0cmFjdE1lc3NhZ2VSZWFkZXIgPSBBYnN0cmFjdE1lc3NhZ2VSZWFkZXI7XG52YXIgUmVzb2x2ZWRNZXNzYWdlUmVhZGVyT3B0aW9ucztcbihmdW5jdGlvbiAoUmVzb2x2ZWRNZXNzYWdlUmVhZGVyT3B0aW9ucykge1xuICAgIGZ1bmN0aW9uIGZyb21PcHRpb25zKG9wdGlvbnMpIHtcbiAgICAgICAgbGV0IGNoYXJzZXQ7XG4gICAgICAgIGxldCByZXN1bHQ7XG4gICAgICAgIGxldCBjb250ZW50RGVjb2RlcjtcbiAgICAgICAgY29uc3QgY29udGVudERlY29kZXJzID0gbmV3IE1hcCgpO1xuICAgICAgICBsZXQgY29udGVudFR5cGVEZWNvZGVyO1xuICAgICAgICBjb25zdCBjb250ZW50VHlwZURlY29kZXJzID0gbmV3IE1hcCgpO1xuICAgICAgICBpZiAob3B0aW9ucyA9PT0gdW5kZWZpbmVkIHx8IHR5cGVvZiBvcHRpb25zID09PSAnc3RyaW5nJykge1xuICAgICAgICAgICAgY2hhcnNldCA9IG9wdGlvbnMgPz8gJ3V0Zi04JztcbiAgICAgICAgfVxuICAgICAgICBlbHNlIHtcbiAgICAgICAgICAgIGNoYXJzZXQgPSBvcHRpb25zLmNoYXJzZXQgPz8gJ3V0Zi04JztcbiAgICAgICAgICAgIGlmIChvcHRpb25zLmNvbnRlbnREZWNvZGVyICE9PSB1bmRlZmluZWQpIHtcbiAgICAgICAgICAgICAgICBjb250ZW50RGVjb2RlciA9IG9wdGlvbnMuY29udGVudERlY29kZXI7XG4gICAgICAgICAgICAgICAgY29udGVudERlY29kZXJzLnNldChjb250ZW50RGVjb2Rlci5uYW1lLCBjb250ZW50RGVjb2Rlcik7XG4gICAgICAgICAgICB9XG4gICAgICAgICAgICBpZiAob3B0aW9ucy5jb250ZW50RGVjb2RlcnMgIT09IHVuZGVmaW5lZCkge1xuICAgICAgICAgICAgICAgIGZvciAoY29uc3QgZGVjb2RlciBvZiBvcHRpb25zLmNvbnRlbnREZWNvZGVycykge1xuICAgICAgICAgICAgICAgICAgICBjb250ZW50RGVjb2RlcnMuc2V0KGRlY29kZXIubmFtZSwgZGVjb2Rlcik7XG4gICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgfVxuICAgICAgICAgICAgaWYgKG9wdGlvbnMuY29udGVudFR5cGVEZWNvZGVyICE9PSB1bmRlZmluZWQpIHtcbiAgICAgICAgICAgICAgICBjb250ZW50VHlwZURlY29kZXIgPSBvcHRpb25zLmNvbnRlbnRUeXBlRGVjb2RlcjtcbiAgICAgICAgICAgICAgICBjb250ZW50VHlwZURlY29kZXJzLnNldChjb250ZW50VHlwZURlY29kZXIubmFtZSwgY29udGVudFR5cGVEZWNvZGVyKTtcbiAgICAgICAgICAgIH1cbiAgICAgICAgICAgIGlmIChvcHRpb25zLmNvbnRlbnRUeXBlRGVjb2RlcnMgIT09IHVuZGVmaW5lZCkge1xuICAgICAgICAgICAgICAgIGZvciAoY29uc3QgZGVjb2RlciBvZiBvcHRpb25zLmNvbnRlbnRUeXBlRGVjb2RlcnMpIHtcbiAgICAgICAgICAgICAgICAgICAgY29udGVudFR5cGVEZWNvZGVycy5zZXQoZGVjb2Rlci5uYW1lLCBkZWNvZGVyKTtcbiAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICB9XG4gICAgICAgIH1cbiAgICAgICAgaWYgKGNvbnRlbnRUeXBlRGVjb2RlciA9PT0gdW5kZWZpbmVkKSB7XG4gICAgICAgICAgICBjb250ZW50VHlwZURlY29kZXIgPSAoMCwgcmFsXzEuZGVmYXVsdCkoKS5hcHBsaWNhdGlvbkpzb24uZGVjb2RlcjtcbiAgICAgICAgICAgIGNvbnRlbnRUeXBlRGVjb2RlcnMuc2V0KGNvbnRlbnRUeXBlRGVjb2Rlci5uYW1lLCBjb250ZW50VHlwZURlY29kZXIpO1xuICAgICAgICB9XG4gICAgICAgIHJldHVybiB7IGNoYXJzZXQsIGNvbnRlbnREZWNvZGVyLCBjb250ZW50RGVjb2RlcnMsIGNvbnRlbnRUeXBlRGVjb2RlciwgY29udGVudFR5cGVEZWNvZGVycyB9O1xuICAgIH1cbiAgICBSZXNvbHZlZE1lc3NhZ2VSZWFkZXJPcHRpb25zLmZyb21PcHRpb25zID0gZnJvbU9wdGlvbnM7XG59KShSZXNvbHZlZE1lc3NhZ2VSZWFkZXJPcHRpb25zIHx8IChSZXNvbHZlZE1lc3NhZ2VSZWFkZXJPcHRpb25zID0ge30pKTtcbmNsYXNzIFJlYWRhYmxlU3RyZWFtTWVzc2FnZVJlYWRlciBleHRlbmRzIEFic3RyYWN0TWVzc2FnZVJlYWRlciB7XG4gICAgY29uc3RydWN0b3IocmVhZGFibGUsIG9wdGlvbnMpIHtcbiAgICAgICAgc3VwZXIoKTtcbiAgICAgICAgdGhpcy5yZWFkYWJsZSA9IHJlYWRhYmxlO1xuICAgICAgICB0aGlzLm9wdGlvbnMgPSBSZXNvbHZlZE1lc3NhZ2VSZWFkZXJPcHRpb25zLmZyb21PcHRpb25zKG9wdGlvbnMpO1xuICAgICAgICB0aGlzLmJ1ZmZlciA9ICgwLCByYWxfMS5kZWZhdWx0KSgpLm1lc3NhZ2VCdWZmZXIuY3JlYXRlKHRoaXMub3B0aW9ucy5jaGFyc2V0KTtcbiAgICAgICAgdGhpcy5fcGFydGlhbE1lc3NhZ2VUaW1lb3V0ID0gMTAwMDA7XG4gICAgICAgIHRoaXMubmV4dE1lc3NhZ2VMZW5ndGggPSAtMTtcbiAgICAgICAgdGhpcy5tZXNzYWdlVG9rZW4gPSAwO1xuICAgICAgICB0aGlzLnJlYWRTZW1hcGhvcmUgPSBuZXcgc2VtYXBob3JlXzEuU2VtYXBob3JlKDEpO1xuICAgIH1cbiAgICBzZXQgcGFydGlhbE1lc3NhZ2VUaW1lb3V0KHRpbWVvdXQpIHtcbiAgICAgICAgdGhpcy5fcGFydGlhbE1lc3NhZ2VUaW1lb3V0ID0gdGltZW91dDtcbiAgICB9XG4gICAgZ2V0IHBhcnRpYWxNZXNzYWdlVGltZW91dCgpIHtcbiAgICAgICAgcmV0dXJuIHRoaXMuX3BhcnRpYWxNZXNzYWdlVGltZW91dDtcbiAgICB9XG4gICAgbGlzdGVuKGNhbGxiYWNrKSB7XG4gICAgICAgIHRoaXMubmV4dE1lc3NhZ2VMZW5ndGggPSAtMTtcbiAgICAgICAgdGhpcy5tZXNzYWdlVG9rZW4gPSAwO1xuICAgICAgICB0aGlzLnBhcnRpYWxNZXNzYWdlVGltZXIgPSB1bmRlZmluZWQ7XG4gICAgICAgIHRoaXMuY2FsbGJhY2sgPSBjYWxsYmFjaztcbiAgICAgICAgY29uc3QgcmVzdWx0ID0gdGhpcy5yZWFkYWJsZS5vbkRhdGEoKGRhdGEpID0+IHtcbiAgICAgICAgICAgIHRoaXMub25EYXRhKGRhdGEpO1xuICAgICAgICB9KTtcbiAgICAgICAgdGhpcy5yZWFkYWJsZS5vbkVycm9yKChlcnJvcikgPT4gdGhpcy5maXJlRXJyb3IoZXJyb3IpKTtcbiAgICAgICAgdGhpcy5yZWFkYWJsZS5vbkNsb3NlKCgpID0+IHRoaXMuZmlyZUNsb3NlKCkpO1xuICAgICAgICByZXR1cm4gcmVzdWx0O1xuICAgIH1cbiAgICBvbkRhdGEoZGF0YSkge1xuICAgICAgICB0cnkge1xuICAgICAgICAgICAgdGhpcy5idWZmZXIuYXBwZW5kKGRhdGEpO1xuICAgICAgICAgICAgd2hpbGUgKHRydWUpIHtcbiAgICAgICAgICAgICAgICBpZiAodGhpcy5uZXh0TWVzc2FnZUxlbmd0aCA9PT0gLTEpIHtcbiAgICAgICAgICAgICAgICAgICAgY29uc3QgaGVhZGVycyA9IHRoaXMuYnVmZmVyLnRyeVJlYWRIZWFkZXJzKHRydWUpO1xuICAgICAgICAgICAgICAgICAgICBpZiAoIWhlYWRlcnMpIHtcbiAgICAgICAgICAgICAgICAgICAgICAgIHJldHVybjtcbiAgICAgICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgICAgICBjb25zdCBjb250ZW50TGVuZ3RoID0gaGVhZGVycy5nZXQoJ2NvbnRlbnQtbGVuZ3RoJyk7XG4gICAgICAgICAgICAgICAgICAgIGlmICghY29udGVudExlbmd0aCkge1xuICAgICAgICAgICAgICAgICAgICAgICAgdGhpcy5maXJlRXJyb3IobmV3IEVycm9yKGBIZWFkZXIgbXVzdCBwcm92aWRlIGEgQ29udGVudC1MZW5ndGggcHJvcGVydHkuXFxuJHtKU09OLnN0cmluZ2lmeShPYmplY3QuZnJvbUVudHJpZXMoaGVhZGVycykpfWApKTtcbiAgICAgICAgICAgICAgICAgICAgICAgIHJldHVybjtcbiAgICAgICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgICAgICBjb25zdCBsZW5ndGggPSBwYXJzZUludChjb250ZW50TGVuZ3RoKTtcbiAgICAgICAgICAgICAgICAgICAgaWYgKGlzTmFOKGxlbmd0aCkpIHtcbiAgICAgICAgICAgICAgICAgICAgICAgIHRoaXMuZmlyZUVycm9yKG5ldyBFcnJvcihgQ29udGVudC1MZW5ndGggdmFsdWUgbXVzdCBiZSBhIG51bWJlci4gR290ICR7Y29udGVudExlbmd0aH1gKSk7XG4gICAgICAgICAgICAgICAgICAgICAgICByZXR1cm47XG4gICAgICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICAgICAgdGhpcy5uZXh0TWVzc2FnZUxlbmd0aCA9IGxlbmd0aDtcbiAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgY29uc3QgYm9keSA9IHRoaXMuYnVmZmVyLnRyeVJlYWRCb2R5KHRoaXMubmV4dE1lc3NhZ2VMZW5ndGgpO1xuICAgICAgICAgICAgICAgIGlmIChib2R5ID09PSB1bmRlZmluZWQpIHtcbiAgICAgICAgICAgICAgICAgICAgLyoqIFdlIGhhdmVuJ3QgcmVjZWl2ZWQgdGhlIGZ1bGwgbWVzc2FnZSB5ZXQuICovXG4gICAgICAgICAgICAgICAgICAgIHRoaXMuc2V0UGFydGlhbE1lc3NhZ2VUaW1lcigpO1xuICAgICAgICAgICAgICAgICAgICByZXR1cm47XG4gICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgIHRoaXMuY2xlYXJQYXJ0aWFsTWVzc2FnZVRpbWVyKCk7XG4gICAgICAgICAgICAgICAgdGhpcy5uZXh0TWVzc2FnZUxlbmd0aCA9IC0xO1xuICAgICAgICAgICAgICAgIC8vIE1ha2Ugc3VyZSB0aGF0IHdlIGNvbnZlcnQgb25lIHJlY2VpdmVkIG1lc3NhZ2UgYWZ0ZXIgdGhlXG4gICAgICAgICAgICAgICAgLy8gb3RoZXIuIE90aGVyd2lzZSBpdCBjb3VsZCBoYXBwZW4gdGhhdCBhIGRlY29kaW5nIG9mIGEgc2Vjb25kXG4gICAgICAgICAgICAgICAgLy8gc21hbGxlciBtZXNzYWdlIGZpbmlzaGVkIGJlZm9yZSB0aGUgZGVjb2Rpbmcgb2YgYSBmaXJzdCBsYXJnZXJcbiAgICAgICAgICAgICAgICAvLyBtZXNzYWdlIGFuZCB0aGVuIHdlIHdvdWxkIGRlbGl2ZXIgdGhlIHNlY29uZCBtZXNzYWdlIGZpcnN0LlxuICAgICAgICAgICAgICAgIHRoaXMucmVhZFNlbWFwaG9yZS5sb2NrKGFzeW5jICgpID0+IHtcbiAgICAgICAgICAgICAgICAgICAgY29uc3QgYnl0ZXMgPSB0aGlzLm9wdGlvbnMuY29udGVudERlY29kZXIgIT09IHVuZGVmaW5lZFxuICAgICAgICAgICAgICAgICAgICAgICAgPyBhd2FpdCB0aGlzLm9wdGlvbnMuY29udGVudERlY29kZXIuZGVjb2RlKGJvZHkpXG4gICAgICAgICAgICAgICAgICAgICAgICA6IGJvZHk7XG4gICAgICAgICAgICAgICAgICAgIGNvbnN0IG1lc3NhZ2UgPSBhd2FpdCB0aGlzLm9wdGlvbnMuY29udGVudFR5cGVEZWNvZGVyLmRlY29kZShieXRlcywgdGhpcy5vcHRpb25zKTtcbiAgICAgICAgICAgICAgICAgICAgdGhpcy5jYWxsYmFjayhtZXNzYWdlKTtcbiAgICAgICAgICAgICAgICB9KS5jYXRjaCgoZXJyb3IpID0+IHtcbiAgICAgICAgICAgICAgICAgICAgdGhpcy5maXJlRXJyb3IoZXJyb3IpO1xuICAgICAgICAgICAgICAgIH0pO1xuICAgICAgICAgICAgfVxuICAgICAgICB9XG4gICAgICAgIGNhdGNoIChlcnJvcikge1xuICAgICAgICAgICAgdGhpcy5maXJlRXJyb3IoZXJyb3IpO1xuICAgICAgICB9XG4gICAgfVxuICAgIGNsZWFyUGFydGlhbE1lc3NhZ2VUaW1lcigpIHtcbiAgICAgICAgaWYgKHRoaXMucGFydGlhbE1lc3NhZ2VUaW1lcikge1xuICAgICAgICAgICAgdGhpcy5wYXJ0aWFsTWVzc2FnZVRpbWVyLmRpc3Bvc2UoKTtcbiAgICAgICAgICAgIHRoaXMucGFydGlhbE1lc3NhZ2VUaW1lciA9IHVuZGVmaW5lZDtcbiAgICAgICAgfVxuICAgIH1cbiAgICBzZXRQYXJ0aWFsTWVzc2FnZVRpbWVyKCkge1xuICAgICAgICB0aGlzLmNsZWFyUGFydGlhbE1lc3NhZ2VUaW1lcigpO1xuICAgICAgICBpZiAodGhpcy5fcGFydGlhbE1lc3NhZ2VUaW1lb3V0IDw9IDApIHtcbiAgICAgICAgICAgIHJldHVybjtcbiAgICAgICAgfVxuICAgICAgICB0aGlzLnBhcnRpYWxNZXNzYWdlVGltZXIgPSAoMCwgcmFsXzEuZGVmYXVsdCkoKS50aW1lci5zZXRUaW1lb3V0KCh0b2tlbiwgdGltZW91dCkgPT4ge1xuICAgICAgICAgICAgdGhpcy5wYXJ0aWFsTWVzc2FnZVRpbWVyID0gdW5kZWZpbmVkO1xuICAgICAgICAgICAgaWYgKHRva2VuID09PSB0aGlzLm1lc3NhZ2VUb2tlbikge1xuICAgICAgICAgICAgICAgIHRoaXMuZmlyZVBhcnRpYWxNZXNzYWdlKHsgbWVzc2FnZVRva2VuOiB0b2tlbiwgd2FpdGluZ1RpbWU6IHRpbWVvdXQgfSk7XG4gICAgICAgICAgICAgICAgdGhpcy5zZXRQYXJ0aWFsTWVzc2FnZVRpbWVyKCk7XG4gICAgICAgICAgICB9XG4gICAgICAgIH0sIHRoaXMuX3BhcnRpYWxNZXNzYWdlVGltZW91dCwgdGhpcy5tZXNzYWdlVG9rZW4sIHRoaXMuX3BhcnRpYWxNZXNzYWdlVGltZW91dCk7XG4gICAgfVxufVxuZXhwb3J0cy5SZWFkYWJsZVN0cmVhbU1lc3NhZ2VSZWFkZXIgPSBSZWFkYWJsZVN0cmVhbU1lc3NhZ2VSZWFkZXI7XG4iLCAiXCJ1c2Ugc3RyaWN0XCI7XG4vKiAtLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLVxuICogQ29weXJpZ2h0IChjKSBNaWNyb3NvZnQgQ29ycG9yYXRpb24uIEFsbCByaWdodHMgcmVzZXJ2ZWQuXG4gKiBMaWNlbnNlZCB1bmRlciB0aGUgTUlUIExpY2Vuc2UuIFNlZSBMaWNlbnNlLnR4dCBpbiB0aGUgcHJvamVjdCByb290IGZvciBsaWNlbnNlIGluZm9ybWF0aW9uLlxuICogLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tICovXG5PYmplY3QuZGVmaW5lUHJvcGVydHkoZXhwb3J0cywgXCJfX2VzTW9kdWxlXCIsIHsgdmFsdWU6IHRydWUgfSk7XG5leHBvcnRzLldyaXRlYWJsZVN0cmVhbU1lc3NhZ2VXcml0ZXIgPSBleHBvcnRzLkFic3RyYWN0TWVzc2FnZVdyaXRlciA9IGV4cG9ydHMuTWVzc2FnZVdyaXRlciA9IHZvaWQgMDtcbmNvbnN0IHJhbF8xID0gcmVxdWlyZShcIi4vcmFsXCIpO1xuY29uc3QgSXMgPSByZXF1aXJlKFwiLi9pc1wiKTtcbmNvbnN0IHNlbWFwaG9yZV8xID0gcmVxdWlyZShcIi4vc2VtYXBob3JlXCIpO1xuY29uc3QgZXZlbnRzXzEgPSByZXF1aXJlKFwiLi9ldmVudHNcIik7XG5jb25zdCBDb250ZW50TGVuZ3RoID0gJ0NvbnRlbnQtTGVuZ3RoOiAnO1xuY29uc3QgQ1JMRiA9ICdcXHJcXG4nO1xudmFyIE1lc3NhZ2VXcml0ZXI7XG4oZnVuY3Rpb24gKE1lc3NhZ2VXcml0ZXIpIHtcbiAgICBmdW5jdGlvbiBpcyh2YWx1ZSkge1xuICAgICAgICBsZXQgY2FuZGlkYXRlID0gdmFsdWU7XG4gICAgICAgIHJldHVybiBjYW5kaWRhdGUgJiYgSXMuZnVuYyhjYW5kaWRhdGUuZGlzcG9zZSkgJiYgSXMuZnVuYyhjYW5kaWRhdGUub25DbG9zZSkgJiZcbiAgICAgICAgICAgIElzLmZ1bmMoY2FuZGlkYXRlLm9uRXJyb3IpICYmIElzLmZ1bmMoY2FuZGlkYXRlLndyaXRlKTtcbiAgICB9XG4gICAgTWVzc2FnZVdyaXRlci5pcyA9IGlzO1xufSkoTWVzc2FnZVdyaXRlciB8fCAoZXhwb3J0cy5NZXNzYWdlV3JpdGVyID0gTWVzc2FnZVdyaXRlciA9IHt9KSk7XG5jbGFzcyBBYnN0cmFjdE1lc3NhZ2VXcml0ZXIge1xuICAgIGNvbnN0cnVjdG9yKCkge1xuICAgICAgICB0aGlzLmVycm9yRW1pdHRlciA9IG5ldyBldmVudHNfMS5FbWl0dGVyKCk7XG4gICAgICAgIHRoaXMuY2xvc2VFbWl0dGVyID0gbmV3IGV2ZW50c18xLkVtaXR0ZXIoKTtcbiAgICB9XG4gICAgZGlzcG9zZSgpIHtcbiAgICAgICAgdGhpcy5lcnJvckVtaXR0ZXIuZGlzcG9zZSgpO1xuICAgICAgICB0aGlzLmNsb3NlRW1pdHRlci5kaXNwb3NlKCk7XG4gICAgfVxuICAgIGdldCBvbkVycm9yKCkge1xuICAgICAgICByZXR1cm4gdGhpcy5lcnJvckVtaXR0ZXIuZXZlbnQ7XG4gICAgfVxuICAgIGZpcmVFcnJvcihlcnJvciwgbWVzc2FnZSwgY291bnQpIHtcbiAgICAgICAgdGhpcy5lcnJvckVtaXR0ZXIuZmlyZShbdGhpcy5hc0Vycm9yKGVycm9yKSwgbWVzc2FnZSwgY291bnRdKTtcbiAgICB9XG4gICAgZ2V0IG9uQ2xvc2UoKSB7XG4gICAgICAgIHJldHVybiB0aGlzLmNsb3NlRW1pdHRlci5ldmVudDtcbiAgICB9XG4gICAgZmlyZUNsb3NlKCkge1xuICAgICAgICB0aGlzLmNsb3NlRW1pdHRlci5maXJlKHVuZGVmaW5lZCk7XG4gICAgfVxuICAgIGFzRXJyb3IoZXJyb3IpIHtcbiAgICAgICAgaWYgKGVycm9yIGluc3RhbmNlb2YgRXJyb3IpIHtcbiAgICAgICAgICAgIHJldHVybiBlcnJvcjtcbiAgICAgICAgfVxuICAgICAgICBlbHNlIHtcbiAgICAgICAgICAgIHJldHVybiBuZXcgRXJyb3IoYFdyaXRlciByZWNlaXZlZCBlcnJvci4gUmVhc29uOiAke0lzLnN0cmluZyhlcnJvci5tZXNzYWdlKSA/IGVycm9yLm1lc3NhZ2UgOiAndW5rbm93bid9YCk7XG4gICAgICAgIH1cbiAgICB9XG59XG5leHBvcnRzLkFic3RyYWN0TWVzc2FnZVdyaXRlciA9IEFic3RyYWN0TWVzc2FnZVdyaXRlcjtcbnZhciBSZXNvbHZlZE1lc3NhZ2VXcml0ZXJPcHRpb25zO1xuKGZ1bmN0aW9uIChSZXNvbHZlZE1lc3NhZ2VXcml0ZXJPcHRpb25zKSB7XG4gICAgZnVuY3Rpb24gZnJvbU9wdGlvbnMob3B0aW9ucykge1xuICAgICAgICBpZiAob3B0aW9ucyA9PT0gdW5kZWZpbmVkIHx8IHR5cGVvZiBvcHRpb25zID09PSAnc3RyaW5nJykge1xuICAgICAgICAgICAgcmV0dXJuIHsgY2hhcnNldDogb3B0aW9ucyA/PyAndXRmLTgnLCBjb250ZW50VHlwZUVuY29kZXI6ICgwLCByYWxfMS5kZWZhdWx0KSgpLmFwcGxpY2F0aW9uSnNvbi5lbmNvZGVyIH07XG4gICAgICAgIH1cbiAgICAgICAgZWxzZSB7XG4gICAgICAgICAgICByZXR1cm4geyBjaGFyc2V0OiBvcHRpb25zLmNoYXJzZXQgPz8gJ3V0Zi04JywgY29udGVudEVuY29kZXI6IG9wdGlvbnMuY29udGVudEVuY29kZXIsIGNvbnRlbnRUeXBlRW5jb2Rlcjogb3B0aW9ucy5jb250ZW50VHlwZUVuY29kZXIgPz8gKDAsIHJhbF8xLmRlZmF1bHQpKCkuYXBwbGljYXRpb25Kc29uLmVuY29kZXIgfTtcbiAgICAgICAgfVxuICAgIH1cbiAgICBSZXNvbHZlZE1lc3NhZ2VXcml0ZXJPcHRpb25zLmZyb21PcHRpb25zID0gZnJvbU9wdGlvbnM7XG59KShSZXNvbHZlZE1lc3NhZ2VXcml0ZXJPcHRpb25zIHx8IChSZXNvbHZlZE1lc3NhZ2VXcml0ZXJPcHRpb25zID0ge30pKTtcbmNsYXNzIFdyaXRlYWJsZVN0cmVhbU1lc3NhZ2VXcml0ZXIgZXh0ZW5kcyBBYnN0cmFjdE1lc3NhZ2VXcml0ZXIge1xuICAgIGNvbnN0cnVjdG9yKHdyaXRhYmxlLCBvcHRpb25zKSB7XG4gICAgICAgIHN1cGVyKCk7XG4gICAgICAgIHRoaXMud3JpdGFibGUgPSB3cml0YWJsZTtcbiAgICAgICAgdGhpcy5vcHRpb25zID0gUmVzb2x2ZWRNZXNzYWdlV3JpdGVyT3B0aW9ucy5mcm9tT3B0aW9ucyhvcHRpb25zKTtcbiAgICAgICAgdGhpcy5lcnJvckNvdW50ID0gMDtcbiAgICAgICAgdGhpcy53cml0ZVNlbWFwaG9yZSA9IG5ldyBzZW1hcGhvcmVfMS5TZW1hcGhvcmUoMSk7XG4gICAgICAgIHRoaXMud3JpdGFibGUub25FcnJvcigoZXJyb3IpID0+IHRoaXMuZmlyZUVycm9yKGVycm9yKSk7XG4gICAgICAgIHRoaXMud3JpdGFibGUub25DbG9zZSgoKSA9PiB0aGlzLmZpcmVDbG9zZSgpKTtcbiAgICB9XG4gICAgYXN5bmMgd3JpdGUobXNnKSB7XG4gICAgICAgIHJldHVybiB0aGlzLndyaXRlU2VtYXBob3JlLmxvY2soYXN5bmMgKCkgPT4ge1xuICAgICAgICAgICAgY29uc3QgcGF5bG9hZCA9IHRoaXMub3B0aW9ucy5jb250ZW50VHlwZUVuY29kZXIuZW5jb2RlKG1zZywgdGhpcy5vcHRpb25zKS50aGVuKChidWZmZXIpID0+IHtcbiAgICAgICAgICAgICAgICBpZiAodGhpcy5vcHRpb25zLmNvbnRlbnRFbmNvZGVyICE9PSB1bmRlZmluZWQpIHtcbiAgICAgICAgICAgICAgICAgICAgcmV0dXJuIHRoaXMub3B0aW9ucy5jb250ZW50RW5jb2Rlci5lbmNvZGUoYnVmZmVyKTtcbiAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgZWxzZSB7XG4gICAgICAgICAgICAgICAgICAgIHJldHVybiBidWZmZXI7XG4gICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgfSk7XG4gICAgICAgICAgICByZXR1cm4gcGF5bG9hZC50aGVuKChidWZmZXIpID0+IHtcbiAgICAgICAgICAgICAgICBjb25zdCBoZWFkZXJzID0gW107XG4gICAgICAgICAgICAgICAgaGVhZGVycy5wdXNoKENvbnRlbnRMZW5ndGgsIGJ1ZmZlci5ieXRlTGVuZ3RoLnRvU3RyaW5nKCksIENSTEYpO1xuICAgICAgICAgICAgICAgIGhlYWRlcnMucHVzaChDUkxGKTtcbiAgICAgICAgICAgICAgICByZXR1cm4gdGhpcy5kb1dyaXRlKG1zZywgaGVhZGVycywgYnVmZmVyKTtcbiAgICAgICAgICAgIH0sIChlcnJvcikgPT4ge1xuICAgICAgICAgICAgICAgIHRoaXMuZmlyZUVycm9yKGVycm9yKTtcbiAgICAgICAgICAgICAgICB0aHJvdyBlcnJvcjtcbiAgICAgICAgICAgIH0pO1xuICAgICAgICB9KTtcbiAgICB9XG4gICAgYXN5bmMgZG9Xcml0ZShtc2csIGhlYWRlcnMsIGRhdGEpIHtcbiAgICAgICAgdHJ5IHtcbiAgICAgICAgICAgIGF3YWl0IHRoaXMud3JpdGFibGUud3JpdGUoaGVhZGVycy5qb2luKCcnKSwgJ2FzY2lpJyk7XG4gICAgICAgICAgICByZXR1cm4gdGhpcy53cml0YWJsZS53cml0ZShkYXRhKTtcbiAgICAgICAgfVxuICAgICAgICBjYXRjaCAoZXJyb3IpIHtcbiAgICAgICAgICAgIHRoaXMuaGFuZGxlRXJyb3IoZXJyb3IsIG1zZyk7XG4gICAgICAgICAgICByZXR1cm4gUHJvbWlzZS5yZWplY3QoZXJyb3IpO1xuICAgICAgICB9XG4gICAgfVxuICAgIGhhbmRsZUVycm9yKGVycm9yLCBtc2cpIHtcbiAgICAgICAgdGhpcy5lcnJvckNvdW50Kys7XG4gICAgICAgIHRoaXMuZmlyZUVycm9yKGVycm9yLCBtc2csIHRoaXMuZXJyb3JDb3VudCk7XG4gICAgfVxuICAgIGVuZCgpIHtcbiAgICAgICAgdGhpcy53cml0YWJsZS5lbmQoKTtcbiAgICB9XG59XG5leHBvcnRzLldyaXRlYWJsZVN0cmVhbU1lc3NhZ2VXcml0ZXIgPSBXcml0ZWFibGVTdHJlYW1NZXNzYWdlV3JpdGVyO1xuIiwgIlwidXNlIHN0cmljdFwiO1xuLyotLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS1cbiAqICBDb3B5cmlnaHQgKGMpIE1pY3Jvc29mdCBDb3Jwb3JhdGlvbi4gQWxsIHJpZ2h0cyByZXNlcnZlZC5cbiAqICBMaWNlbnNlZCB1bmRlciB0aGUgTUlUIExpY2Vuc2UuIFNlZSBMaWNlbnNlLnR4dCBpbiB0aGUgcHJvamVjdCByb290IGZvciBsaWNlbnNlIGluZm9ybWF0aW9uLlxuICotLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLSovXG5PYmplY3QuZGVmaW5lUHJvcGVydHkoZXhwb3J0cywgXCJfX2VzTW9kdWxlXCIsIHsgdmFsdWU6IHRydWUgfSk7XG5leHBvcnRzLkFic3RyYWN0TWVzc2FnZUJ1ZmZlciA9IHZvaWQgMDtcbmNvbnN0IENSID0gMTM7XG5jb25zdCBMRiA9IDEwO1xuY29uc3QgQ1JMRiA9ICdcXHJcXG4nO1xuY2xhc3MgQWJzdHJhY3RNZXNzYWdlQnVmZmVyIHtcbiAgICBjb25zdHJ1Y3RvcihlbmNvZGluZyA9ICd1dGYtOCcpIHtcbiAgICAgICAgdGhpcy5fZW5jb2RpbmcgPSBlbmNvZGluZztcbiAgICAgICAgdGhpcy5fY2h1bmtzID0gW107XG4gICAgICAgIHRoaXMuX3RvdGFsTGVuZ3RoID0gMDtcbiAgICB9XG4gICAgZ2V0IGVuY29kaW5nKCkge1xuICAgICAgICByZXR1cm4gdGhpcy5fZW5jb2Rpbmc7XG4gICAgfVxuICAgIGFwcGVuZChjaHVuaykge1xuICAgICAgICBjb25zdCB0b0FwcGVuZCA9IHR5cGVvZiBjaHVuayA9PT0gJ3N0cmluZycgPyB0aGlzLmZyb21TdHJpbmcoY2h1bmssIHRoaXMuX2VuY29kaW5nKSA6IGNodW5rO1xuICAgICAgICB0aGlzLl9jaHVua3MucHVzaCh0b0FwcGVuZCk7XG4gICAgICAgIHRoaXMuX3RvdGFsTGVuZ3RoICs9IHRvQXBwZW5kLmJ5dGVMZW5ndGg7XG4gICAgfVxuICAgIHRyeVJlYWRIZWFkZXJzKGxvd2VyQ2FzZUtleXMgPSBmYWxzZSkge1xuICAgICAgICBpZiAodGhpcy5fY2h1bmtzLmxlbmd0aCA9PT0gMCkge1xuICAgICAgICAgICAgcmV0dXJuIHVuZGVmaW5lZDtcbiAgICAgICAgfVxuICAgICAgICBsZXQgc3RhdGUgPSAwO1xuICAgICAgICBsZXQgY2h1bmtJbmRleCA9IDA7XG4gICAgICAgIGxldCBvZmZzZXQgPSAwO1xuICAgICAgICBsZXQgY2h1bmtCeXRlc1JlYWQgPSAwO1xuICAgICAgICByb3c6IHdoaWxlIChjaHVua0luZGV4IDwgdGhpcy5fY2h1bmtzLmxlbmd0aCkge1xuICAgICAgICAgICAgY29uc3QgY2h1bmsgPSB0aGlzLl9jaHVua3NbY2h1bmtJbmRleF07XG4gICAgICAgICAgICBvZmZzZXQgPSAwO1xuICAgICAgICAgICAgY29sdW1uOiB3aGlsZSAob2Zmc2V0IDwgY2h1bmsubGVuZ3RoKSB7XG4gICAgICAgICAgICAgICAgY29uc3QgdmFsdWUgPSBjaHVua1tvZmZzZXRdO1xuICAgICAgICAgICAgICAgIHN3aXRjaCAodmFsdWUpIHtcbiAgICAgICAgICAgICAgICAgICAgY2FzZSBDUjpcbiAgICAgICAgICAgICAgICAgICAgICAgIHN3aXRjaCAoc3RhdGUpIHtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICBjYXNlIDA6XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIHN0YXRlID0gMTtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgYnJlYWs7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgY2FzZSAyOlxuICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICBzdGF0ZSA9IDM7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIGJyZWFrO1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgIGRlZmF1bHQ6XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIHN0YXRlID0gMDtcbiAgICAgICAgICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICAgICAgICAgIGJyZWFrO1xuICAgICAgICAgICAgICAgICAgICBjYXNlIExGOlxuICAgICAgICAgICAgICAgICAgICAgICAgc3dpdGNoIChzdGF0ZSkge1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgIGNhc2UgMTpcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgc3RhdGUgPSAyO1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICBicmVhaztcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICBjYXNlIDM6XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIHN0YXRlID0gNDtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgb2Zmc2V0Kys7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIGJyZWFrIHJvdztcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICBkZWZhdWx0OlxuICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICBzdGF0ZSA9IDA7XG4gICAgICAgICAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgICAgICAgICBicmVhaztcbiAgICAgICAgICAgICAgICAgICAgZGVmYXVsdDpcbiAgICAgICAgICAgICAgICAgICAgICAgIHN0YXRlID0gMDtcbiAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgb2Zmc2V0Kys7XG4gICAgICAgICAgICB9XG4gICAgICAgICAgICBjaHVua0J5dGVzUmVhZCArPSBjaHVuay5ieXRlTGVuZ3RoO1xuICAgICAgICAgICAgY2h1bmtJbmRleCsrO1xuICAgICAgICB9XG4gICAgICAgIGlmIChzdGF0ZSAhPT0gNCkge1xuICAgICAgICAgICAgcmV0dXJuIHVuZGVmaW5lZDtcbiAgICAgICAgfVxuICAgICAgICAvLyBUaGUgYnVmZmVyIGNvbnRhaW5zIHRoZSB0d28gQ1JMRiBhdCB0aGUgZW5kLiBTbyB3ZSB3aWxsXG4gICAgICAgIC8vIGhhdmUgdHdvIGVtcHR5IGxpbmVzIGFmdGVyIHRoZSBzcGxpdCBhdCB0aGUgZW5kIGFzIHdlbGwuXG4gICAgICAgIGNvbnN0IGJ1ZmZlciA9IHRoaXMuX3JlYWQoY2h1bmtCeXRlc1JlYWQgKyBvZmZzZXQpO1xuICAgICAgICBjb25zdCByZXN1bHQgPSBuZXcgTWFwKCk7XG4gICAgICAgIGNvbnN0IGhlYWRlcnMgPSB0aGlzLnRvU3RyaW5nKGJ1ZmZlciwgJ2FzY2lpJykuc3BsaXQoQ1JMRik7XG4gICAgICAgIGlmIChoZWFkZXJzLmxlbmd0aCA8IDIpIHtcbiAgICAgICAgICAgIHJldHVybiByZXN1bHQ7XG4gICAgICAgIH1cbiAgICAgICAgZm9yIChsZXQgaSA9IDA7IGkgPCBoZWFkZXJzLmxlbmd0aCAtIDI7IGkrKykge1xuICAgICAgICAgICAgY29uc3QgaGVhZGVyID0gaGVhZGVyc1tpXTtcbiAgICAgICAgICAgIGNvbnN0IGluZGV4ID0gaGVhZGVyLmluZGV4T2YoJzonKTtcbiAgICAgICAgICAgIGlmIChpbmRleCA9PT0gLTEpIHtcbiAgICAgICAgICAgICAgICB0aHJvdyBuZXcgRXJyb3IoYE1lc3NhZ2UgaGVhZGVyIG11c3Qgc2VwYXJhdGUga2V5IGFuZCB2YWx1ZSB1c2luZyAnOidcXG4ke2hlYWRlcn1gKTtcbiAgICAgICAgICAgIH1cbiAgICAgICAgICAgIGNvbnN0IGtleSA9IGhlYWRlci5zdWJzdHIoMCwgaW5kZXgpO1xuICAgICAgICAgICAgY29uc3QgdmFsdWUgPSBoZWFkZXIuc3Vic3RyKGluZGV4ICsgMSkudHJpbSgpO1xuICAgICAgICAgICAgcmVzdWx0LnNldChsb3dlckNhc2VLZXlzID8ga2V5LnRvTG93ZXJDYXNlKCkgOiBrZXksIHZhbHVlKTtcbiAgICAgICAgfVxuICAgICAgICByZXR1cm4gcmVzdWx0O1xuICAgIH1cbiAgICB0cnlSZWFkQm9keShsZW5ndGgpIHtcbiAgICAgICAgaWYgKHRoaXMuX3RvdGFsTGVuZ3RoIDwgbGVuZ3RoKSB7XG4gICAgICAgICAgICByZXR1cm4gdW5kZWZpbmVkO1xuICAgICAgICB9XG4gICAgICAgIHJldHVybiB0aGlzLl9yZWFkKGxlbmd0aCk7XG4gICAgfVxuICAgIGdldCBudW1iZXJPZkJ5dGVzKCkge1xuICAgICAgICByZXR1cm4gdGhpcy5fdG90YWxMZW5ndGg7XG4gICAgfVxuICAgIF9yZWFkKGJ5dGVDb3VudCkge1xuICAgICAgICBpZiAoYnl0ZUNvdW50ID09PSAwKSB7XG4gICAgICAgICAgICByZXR1cm4gdGhpcy5lbXB0eUJ1ZmZlcigpO1xuICAgICAgICB9XG4gICAgICAgIGlmIChieXRlQ291bnQgPiB0aGlzLl90b3RhbExlbmd0aCkge1xuICAgICAgICAgICAgdGhyb3cgbmV3IEVycm9yKGBDYW5ub3QgcmVhZCBzbyBtYW55IGJ5dGVzIWApO1xuICAgICAgICB9XG4gICAgICAgIGlmICh0aGlzLl9jaHVua3NbMF0uYnl0ZUxlbmd0aCA9PT0gYnl0ZUNvdW50KSB7XG4gICAgICAgICAgICAvLyBzdXBlciBmYXN0IHBhdGgsIHByZWNpc2VseSBmaXJzdCBjaHVuayBtdXN0IGJlIHJldHVybmVkXG4gICAgICAgICAgICBjb25zdCBjaHVuayA9IHRoaXMuX2NodW5rc1swXTtcbiAgICAgICAgICAgIHRoaXMuX2NodW5rcy5zaGlmdCgpO1xuICAgICAgICAgICAgdGhpcy5fdG90YWxMZW5ndGggLT0gYnl0ZUNvdW50O1xuICAgICAgICAgICAgcmV0dXJuIHRoaXMuYXNOYXRpdmUoY2h1bmspO1xuICAgICAgICB9XG4gICAgICAgIGlmICh0aGlzLl9jaHVua3NbMF0uYnl0ZUxlbmd0aCA+IGJ5dGVDb3VudCkge1xuICAgICAgICAgICAgLy8gZmFzdCBwYXRoLCB0aGUgcmVhZGluZyBpcyBlbnRpcmVseSB3aXRoaW4gdGhlIGZpcnN0IGNodW5rXG4gICAgICAgICAgICBjb25zdCBjaHVuayA9IHRoaXMuX2NodW5rc1swXTtcbiAgICAgICAgICAgIGNvbnN0IHJlc3VsdCA9IHRoaXMuYXNOYXRpdmUoY2h1bmssIGJ5dGVDb3VudCk7XG4gICAgICAgICAgICB0aGlzLl9jaHVua3NbMF0gPSBjaHVuay5zbGljZShieXRlQ291bnQpO1xuICAgICAgICAgICAgdGhpcy5fdG90YWxMZW5ndGggLT0gYnl0ZUNvdW50O1xuICAgICAgICAgICAgcmV0dXJuIHJlc3VsdDtcbiAgICAgICAgfVxuICAgICAgICBjb25zdCByZXN1bHQgPSB0aGlzLmFsbG9jTmF0aXZlKGJ5dGVDb3VudCk7XG4gICAgICAgIGxldCByZXN1bHRPZmZzZXQgPSAwO1xuICAgICAgICBsZXQgY2h1bmtJbmRleCA9IDA7XG4gICAgICAgIHdoaWxlIChieXRlQ291bnQgPiAwKSB7XG4gICAgICAgICAgICBjb25zdCBjaHVuayA9IHRoaXMuX2NodW5rc1tjaHVua0luZGV4XTtcbiAgICAgICAgICAgIGlmIChjaHVuay5ieXRlTGVuZ3RoID4gYnl0ZUNvdW50KSB7XG4gICAgICAgICAgICAgICAgLy8gdGhpcyBjaHVuayB3aWxsIHN1cnZpdmVcbiAgICAgICAgICAgICAgICBjb25zdCBjaHVua1BhcnQgPSBjaHVuay5zbGljZSgwLCBieXRlQ291bnQpO1xuICAgICAgICAgICAgICAgIHJlc3VsdC5zZXQoY2h1bmtQYXJ0LCByZXN1bHRPZmZzZXQpO1xuICAgICAgICAgICAgICAgIHJlc3VsdE9mZnNldCArPSBieXRlQ291bnQ7XG4gICAgICAgICAgICAgICAgdGhpcy5fY2h1bmtzW2NodW5rSW5kZXhdID0gY2h1bmsuc2xpY2UoYnl0ZUNvdW50KTtcbiAgICAgICAgICAgICAgICB0aGlzLl90b3RhbExlbmd0aCAtPSBieXRlQ291bnQ7XG4gICAgICAgICAgICAgICAgYnl0ZUNvdW50IC09IGJ5dGVDb3VudDtcbiAgICAgICAgICAgIH1cbiAgICAgICAgICAgIGVsc2Uge1xuICAgICAgICAgICAgICAgIC8vIHRoaXMgY2h1bmsgd2lsbCBiZSBlbnRpcmVseSByZWFkXG4gICAgICAgICAgICAgICAgcmVzdWx0LnNldChjaHVuaywgcmVzdWx0T2Zmc2V0KTtcbiAgICAgICAgICAgICAgICByZXN1bHRPZmZzZXQgKz0gY2h1bmsuYnl0ZUxlbmd0aDtcbiAgICAgICAgICAgICAgICB0aGlzLl9jaHVua3Muc2hpZnQoKTtcbiAgICAgICAgICAgICAgICB0aGlzLl90b3RhbExlbmd0aCAtPSBjaHVuay5ieXRlTGVuZ3RoO1xuICAgICAgICAgICAgICAgIGJ5dGVDb3VudCAtPSBjaHVuay5ieXRlTGVuZ3RoO1xuICAgICAgICAgICAgfVxuICAgICAgICB9XG4gICAgICAgIHJldHVybiByZXN1bHQ7XG4gICAgfVxufVxuZXhwb3J0cy5BYnN0cmFjdE1lc3NhZ2VCdWZmZXIgPSBBYnN0cmFjdE1lc3NhZ2VCdWZmZXI7XG4iLCAiXCJ1c2Ugc3RyaWN0XCI7XG4vKiAtLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLVxuICogQ29weXJpZ2h0IChjKSBNaWNyb3NvZnQgQ29ycG9yYXRpb24uIEFsbCByaWdodHMgcmVzZXJ2ZWQuXG4gKiBMaWNlbnNlZCB1bmRlciB0aGUgTUlUIExpY2Vuc2UuIFNlZSBMaWNlbnNlLnR4dCBpbiB0aGUgcHJvamVjdCByb290IGZvciBsaWNlbnNlIGluZm9ybWF0aW9uLlxuICogLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tICovXG5PYmplY3QuZGVmaW5lUHJvcGVydHkoZXhwb3J0cywgXCJfX2VzTW9kdWxlXCIsIHsgdmFsdWU6IHRydWUgfSk7XG5leHBvcnRzLmNyZWF0ZU1lc3NhZ2VDb25uZWN0aW9uID0gZXhwb3J0cy5Db25uZWN0aW9uT3B0aW9ucyA9IGV4cG9ydHMuTWVzc2FnZVN0cmF0ZWd5ID0gZXhwb3J0cy5DYW5jZWxsYXRpb25TdHJhdGVneSA9IGV4cG9ydHMuQ2FuY2VsbGF0aW9uU2VuZGVyU3RyYXRlZ3kgPSBleHBvcnRzLkNhbmNlbGxhdGlvblJlY2VpdmVyU3RyYXRlZ3kgPSBleHBvcnRzLlJlcXVlc3RDYW5jZWxsYXRpb25SZWNlaXZlclN0cmF0ZWd5ID0gZXhwb3J0cy5JZENhbmNlbGxhdGlvblJlY2VpdmVyU3RyYXRlZ3kgPSBleHBvcnRzLkNvbm5lY3Rpb25TdHJhdGVneSA9IGV4cG9ydHMuQ29ubmVjdGlvbkVycm9yID0gZXhwb3J0cy5Db25uZWN0aW9uRXJyb3JzID0gZXhwb3J0cy5Mb2dUcmFjZU5vdGlmaWNhdGlvbiA9IGV4cG9ydHMuU2V0VHJhY2VOb3RpZmljYXRpb24gPSBleHBvcnRzLlRyYWNlRm9ybWF0ID0gZXhwb3J0cy5UcmFjZVZhbHVlcyA9IGV4cG9ydHMuVHJhY2UgPSBleHBvcnRzLk51bGxMb2dnZXIgPSBleHBvcnRzLlByb2dyZXNzVHlwZSA9IGV4cG9ydHMuUHJvZ3Jlc3NUb2tlbiA9IHZvaWQgMDtcbmNvbnN0IHJhbF8xID0gcmVxdWlyZShcIi4vcmFsXCIpO1xuY29uc3QgSXMgPSByZXF1aXJlKFwiLi9pc1wiKTtcbmNvbnN0IG1lc3NhZ2VzXzEgPSByZXF1aXJlKFwiLi9tZXNzYWdlc1wiKTtcbmNvbnN0IGxpbmtlZE1hcF8xID0gcmVxdWlyZShcIi4vbGlua2VkTWFwXCIpO1xuY29uc3QgZXZlbnRzXzEgPSByZXF1aXJlKFwiLi9ldmVudHNcIik7XG5jb25zdCBjYW5jZWxsYXRpb25fMSA9IHJlcXVpcmUoXCIuL2NhbmNlbGxhdGlvblwiKTtcbnZhciBDYW5jZWxOb3RpZmljYXRpb247XG4oZnVuY3Rpb24gKENhbmNlbE5vdGlmaWNhdGlvbikge1xuICAgIENhbmNlbE5vdGlmaWNhdGlvbi50eXBlID0gbmV3IG1lc3NhZ2VzXzEuTm90aWZpY2F0aW9uVHlwZSgnJC9jYW5jZWxSZXF1ZXN0Jyk7XG59KShDYW5jZWxOb3RpZmljYXRpb24gfHwgKENhbmNlbE5vdGlmaWNhdGlvbiA9IHt9KSk7XG52YXIgUHJvZ3Jlc3NUb2tlbjtcbihmdW5jdGlvbiAoUHJvZ3Jlc3NUb2tlbikge1xuICAgIGZ1bmN0aW9uIGlzKHZhbHVlKSB7XG4gICAgICAgIHJldHVybiB0eXBlb2YgdmFsdWUgPT09ICdzdHJpbmcnIHx8IHR5cGVvZiB2YWx1ZSA9PT0gJ251bWJlcic7XG4gICAgfVxuICAgIFByb2dyZXNzVG9rZW4uaXMgPSBpcztcbn0pKFByb2dyZXNzVG9rZW4gfHwgKGV4cG9ydHMuUHJvZ3Jlc3NUb2tlbiA9IFByb2dyZXNzVG9rZW4gPSB7fSkpO1xudmFyIFByb2dyZXNzTm90aWZpY2F0aW9uO1xuKGZ1bmN0aW9uIChQcm9ncmVzc05vdGlmaWNhdGlvbikge1xuICAgIFByb2dyZXNzTm90aWZpY2F0aW9uLnR5cGUgPSBuZXcgbWVzc2FnZXNfMS5Ob3RpZmljYXRpb25UeXBlKCckL3Byb2dyZXNzJyk7XG59KShQcm9ncmVzc05vdGlmaWNhdGlvbiB8fCAoUHJvZ3Jlc3NOb3RpZmljYXRpb24gPSB7fSkpO1xuY2xhc3MgUHJvZ3Jlc3NUeXBlIHtcbiAgICBjb25zdHJ1Y3RvcigpIHtcbiAgICB9XG59XG5leHBvcnRzLlByb2dyZXNzVHlwZSA9IFByb2dyZXNzVHlwZTtcbnZhciBTdGFyUmVxdWVzdEhhbmRsZXI7XG4oZnVuY3Rpb24gKFN0YXJSZXF1ZXN0SGFuZGxlcikge1xuICAgIGZ1bmN0aW9uIGlzKHZhbHVlKSB7XG4gICAgICAgIHJldHVybiBJcy5mdW5jKHZhbHVlKTtcbiAgICB9XG4gICAgU3RhclJlcXVlc3RIYW5kbGVyLmlzID0gaXM7XG59KShTdGFyUmVxdWVzdEhhbmRsZXIgfHwgKFN0YXJSZXF1ZXN0SGFuZGxlciA9IHt9KSk7XG5leHBvcnRzLk51bGxMb2dnZXIgPSBPYmplY3QuZnJlZXplKHtcbiAgICBlcnJvcjogKCkgPT4geyB9LFxuICAgIHdhcm46ICgpID0+IHsgfSxcbiAgICBpbmZvOiAoKSA9PiB7IH0sXG4gICAgbG9nOiAoKSA9PiB7IH1cbn0pO1xudmFyIFRyYWNlO1xuKGZ1bmN0aW9uIChUcmFjZSkge1xuICAgIFRyYWNlW1RyYWNlW1wiT2ZmXCJdID0gMF0gPSBcIk9mZlwiO1xuICAgIFRyYWNlW1RyYWNlW1wiTWVzc2FnZXNcIl0gPSAxXSA9IFwiTWVzc2FnZXNcIjtcbiAgICBUcmFjZVtUcmFjZVtcIkNvbXBhY3RcIl0gPSAyXSA9IFwiQ29tcGFjdFwiO1xuICAgIFRyYWNlW1RyYWNlW1wiVmVyYm9zZVwiXSA9IDNdID0gXCJWZXJib3NlXCI7XG59KShUcmFjZSB8fCAoZXhwb3J0cy5UcmFjZSA9IFRyYWNlID0ge30pKTtcbnZhciBUcmFjZVZhbHVlcztcbihmdW5jdGlvbiAoVHJhY2VWYWx1ZXMpIHtcbiAgICAvKipcbiAgICAgKiBUdXJuIHRyYWNpbmcgb2ZmLlxuICAgICAqL1xuICAgIFRyYWNlVmFsdWVzLk9mZiA9ICdvZmYnO1xuICAgIC8qKlxuICAgICAqIFRyYWNlIG1lc3NhZ2VzIG9ubHkuXG4gICAgICovXG4gICAgVHJhY2VWYWx1ZXMuTWVzc2FnZXMgPSAnbWVzc2FnZXMnO1xuICAgIC8qKlxuICAgICAqIENvbXBhY3QgbWVzc2FnZSB0cmFjaW5nLlxuICAgICAqL1xuICAgIFRyYWNlVmFsdWVzLkNvbXBhY3QgPSAnY29tcGFjdCc7XG4gICAgLyoqXG4gICAgICogVmVyYm9zZSBtZXNzYWdlIHRyYWNpbmcuXG4gICAgICovXG4gICAgVHJhY2VWYWx1ZXMuVmVyYm9zZSA9ICd2ZXJib3NlJztcbn0pKFRyYWNlVmFsdWVzIHx8IChleHBvcnRzLlRyYWNlVmFsdWVzID0gVHJhY2VWYWx1ZXMgPSB7fSkpO1xuKGZ1bmN0aW9uIChUcmFjZSkge1xuICAgIGZ1bmN0aW9uIGZyb21TdHJpbmcodmFsdWUpIHtcbiAgICAgICAgaWYgKCFJcy5zdHJpbmcodmFsdWUpKSB7XG4gICAgICAgICAgICByZXR1cm4gVHJhY2UuT2ZmO1xuICAgICAgICB9XG4gICAgICAgIHZhbHVlID0gdmFsdWUudG9Mb3dlckNhc2UoKTtcbiAgICAgICAgc3dpdGNoICh2YWx1ZSkge1xuICAgICAgICAgICAgY2FzZSAnb2ZmJzpcbiAgICAgICAgICAgICAgICByZXR1cm4gVHJhY2UuT2ZmO1xuICAgICAgICAgICAgY2FzZSAnbWVzc2FnZXMnOlxuICAgICAgICAgICAgICAgIHJldHVybiBUcmFjZS5NZXNzYWdlcztcbiAgICAgICAgICAgIGNhc2UgJ2NvbXBhY3QnOlxuICAgICAgICAgICAgICAgIHJldHVybiBUcmFjZS5Db21wYWN0O1xuICAgICAgICAgICAgY2FzZSAndmVyYm9zZSc6XG4gICAgICAgICAgICAgICAgcmV0dXJuIFRyYWNlLlZlcmJvc2U7XG4gICAgICAgICAgICBkZWZhdWx0OlxuICAgICAgICAgICAgICAgIHJldHVybiBUcmFjZS5PZmY7XG4gICAgICAgIH1cbiAgICB9XG4gICAgVHJhY2UuZnJvbVN0cmluZyA9IGZyb21TdHJpbmc7XG4gICAgZnVuY3Rpb24gdG9TdHJpbmcodmFsdWUpIHtcbiAgICAgICAgc3dpdGNoICh2YWx1ZSkge1xuICAgICAgICAgICAgY2FzZSBUcmFjZS5PZmY6XG4gICAgICAgICAgICAgICAgcmV0dXJuICdvZmYnO1xuICAgICAgICAgICAgY2FzZSBUcmFjZS5NZXNzYWdlczpcbiAgICAgICAgICAgICAgICByZXR1cm4gJ21lc3NhZ2VzJztcbiAgICAgICAgICAgIGNhc2UgVHJhY2UuQ29tcGFjdDpcbiAgICAgICAgICAgICAgICByZXR1cm4gJ2NvbXBhY3QnO1xuICAgICAgICAgICAgY2FzZSBUcmFjZS5WZXJib3NlOlxuICAgICAgICAgICAgICAgIHJldHVybiAndmVyYm9zZSc7XG4gICAgICAgICAgICBkZWZhdWx0OlxuICAgICAgICAgICAgICAgIHJldHVybiAnb2ZmJztcbiAgICAgICAgfVxuICAgIH1cbiAgICBUcmFjZS50b1N0cmluZyA9IHRvU3RyaW5nO1xufSkoVHJhY2UgfHwgKGV4cG9ydHMuVHJhY2UgPSBUcmFjZSA9IHt9KSk7XG52YXIgVHJhY2VGb3JtYXQ7XG4oZnVuY3Rpb24gKFRyYWNlRm9ybWF0KSB7XG4gICAgVHJhY2VGb3JtYXRbXCJUZXh0XCJdID0gXCJ0ZXh0XCI7XG4gICAgVHJhY2VGb3JtYXRbXCJKU09OXCJdID0gXCJqc29uXCI7XG59KShUcmFjZUZvcm1hdCB8fCAoZXhwb3J0cy5UcmFjZUZvcm1hdCA9IFRyYWNlRm9ybWF0ID0ge30pKTtcbihmdW5jdGlvbiAoVHJhY2VGb3JtYXQpIHtcbiAgICBmdW5jdGlvbiBmcm9tU3RyaW5nKHZhbHVlKSB7XG4gICAgICAgIGlmICghSXMuc3RyaW5nKHZhbHVlKSkge1xuICAgICAgICAgICAgcmV0dXJuIFRyYWNlRm9ybWF0LlRleHQ7XG4gICAgICAgIH1cbiAgICAgICAgdmFsdWUgPSB2YWx1ZS50b0xvd2VyQ2FzZSgpO1xuICAgICAgICBpZiAodmFsdWUgPT09ICdqc29uJykge1xuICAgICAgICAgICAgcmV0dXJuIFRyYWNlRm9ybWF0LkpTT047XG4gICAgICAgIH1cbiAgICAgICAgZWxzZSB7XG4gICAgICAgICAgICByZXR1cm4gVHJhY2VGb3JtYXQuVGV4dDtcbiAgICAgICAgfVxuICAgIH1cbiAgICBUcmFjZUZvcm1hdC5mcm9tU3RyaW5nID0gZnJvbVN0cmluZztcbn0pKFRyYWNlRm9ybWF0IHx8IChleHBvcnRzLlRyYWNlRm9ybWF0ID0gVHJhY2VGb3JtYXQgPSB7fSkpO1xudmFyIFNldFRyYWNlTm90aWZpY2F0aW9uO1xuKGZ1bmN0aW9uIChTZXRUcmFjZU5vdGlmaWNhdGlvbikge1xuICAgIFNldFRyYWNlTm90aWZpY2F0aW9uLnR5cGUgPSBuZXcgbWVzc2FnZXNfMS5Ob3RpZmljYXRpb25UeXBlKCckL3NldFRyYWNlJyk7XG59KShTZXRUcmFjZU5vdGlmaWNhdGlvbiB8fCAoZXhwb3J0cy5TZXRUcmFjZU5vdGlmaWNhdGlvbiA9IFNldFRyYWNlTm90aWZpY2F0aW9uID0ge30pKTtcbnZhciBMb2dUcmFjZU5vdGlmaWNhdGlvbjtcbihmdW5jdGlvbiAoTG9nVHJhY2VOb3RpZmljYXRpb24pIHtcbiAgICBMb2dUcmFjZU5vdGlmaWNhdGlvbi50eXBlID0gbmV3IG1lc3NhZ2VzXzEuTm90aWZpY2F0aW9uVHlwZSgnJC9sb2dUcmFjZScpO1xufSkoTG9nVHJhY2VOb3RpZmljYXRpb24gfHwgKGV4cG9ydHMuTG9nVHJhY2VOb3RpZmljYXRpb24gPSBMb2dUcmFjZU5vdGlmaWNhdGlvbiA9IHt9KSk7XG52YXIgQ29ubmVjdGlvbkVycm9ycztcbihmdW5jdGlvbiAoQ29ubmVjdGlvbkVycm9ycykge1xuICAgIC8qKlxuICAgICAqIFRoZSBjb25uZWN0aW9uIGlzIGNsb3NlZC5cbiAgICAgKi9cbiAgICBDb25uZWN0aW9uRXJyb3JzW0Nvbm5lY3Rpb25FcnJvcnNbXCJDbG9zZWRcIl0gPSAxXSA9IFwiQ2xvc2VkXCI7XG4gICAgLyoqXG4gICAgICogVGhlIGNvbm5lY3Rpb24gZ290IGRpc3Bvc2VkLlxuICAgICAqL1xuICAgIENvbm5lY3Rpb25FcnJvcnNbQ29ubmVjdGlvbkVycm9yc1tcIkRpc3Bvc2VkXCJdID0gMl0gPSBcIkRpc3Bvc2VkXCI7XG4gICAgLyoqXG4gICAgICogVGhlIGNvbm5lY3Rpb24gaXMgYWxyZWFkeSBpbiBsaXN0ZW5pbmcgbW9kZS5cbiAgICAgKi9cbiAgICBDb25uZWN0aW9uRXJyb3JzW0Nvbm5lY3Rpb25FcnJvcnNbXCJBbHJlYWR5TGlzdGVuaW5nXCJdID0gM10gPSBcIkFscmVhZHlMaXN0ZW5pbmdcIjtcbn0pKENvbm5lY3Rpb25FcnJvcnMgfHwgKGV4cG9ydHMuQ29ubmVjdGlvbkVycm9ycyA9IENvbm5lY3Rpb25FcnJvcnMgPSB7fSkpO1xuY2xhc3MgQ29ubmVjdGlvbkVycm9yIGV4dGVuZHMgRXJyb3Ige1xuICAgIGNvbnN0cnVjdG9yKGNvZGUsIG1lc3NhZ2UpIHtcbiAgICAgICAgc3VwZXIobWVzc2FnZSk7XG4gICAgICAgIHRoaXMuY29kZSA9IGNvZGU7XG4gICAgICAgIE9iamVjdC5zZXRQcm90b3R5cGVPZih0aGlzLCBDb25uZWN0aW9uRXJyb3IucHJvdG90eXBlKTtcbiAgICB9XG59XG5leHBvcnRzLkNvbm5lY3Rpb25FcnJvciA9IENvbm5lY3Rpb25FcnJvcjtcbnZhciBDb25uZWN0aW9uU3RyYXRlZ3k7XG4oZnVuY3Rpb24gKENvbm5lY3Rpb25TdHJhdGVneSkge1xuICAgIGZ1bmN0aW9uIGlzKHZhbHVlKSB7XG4gICAgICAgIGNvbnN0IGNhbmRpZGF0ZSA9IHZhbHVlO1xuICAgICAgICByZXR1cm4gY2FuZGlkYXRlICYmIElzLmZ1bmMoY2FuZGlkYXRlLmNhbmNlbFVuZGlzcGF0Y2hlZCk7XG4gICAgfVxuICAgIENvbm5lY3Rpb25TdHJhdGVneS5pcyA9IGlzO1xufSkoQ29ubmVjdGlvblN0cmF0ZWd5IHx8IChleHBvcnRzLkNvbm5lY3Rpb25TdHJhdGVneSA9IENvbm5lY3Rpb25TdHJhdGVneSA9IHt9KSk7XG52YXIgSWRDYW5jZWxsYXRpb25SZWNlaXZlclN0cmF0ZWd5O1xuKGZ1bmN0aW9uIChJZENhbmNlbGxhdGlvblJlY2VpdmVyU3RyYXRlZ3kpIHtcbiAgICBmdW5jdGlvbiBpcyh2YWx1ZSkge1xuICAgICAgICBjb25zdCBjYW5kaWRhdGUgPSB2YWx1ZTtcbiAgICAgICAgcmV0dXJuIGNhbmRpZGF0ZSAmJiAoY2FuZGlkYXRlLmtpbmQgPT09IHVuZGVmaW5lZCB8fCBjYW5kaWRhdGUua2luZCA9PT0gJ2lkJykgJiYgSXMuZnVuYyhjYW5kaWRhdGUuY3JlYXRlQ2FuY2VsbGF0aW9uVG9rZW5Tb3VyY2UpICYmIChjYW5kaWRhdGUuZGlzcG9zZSA9PT0gdW5kZWZpbmVkIHx8IElzLmZ1bmMoY2FuZGlkYXRlLmRpc3Bvc2UpKTtcbiAgICB9XG4gICAgSWRDYW5jZWxsYXRpb25SZWNlaXZlclN0cmF0ZWd5LmlzID0gaXM7XG59KShJZENhbmNlbGxhdGlvblJlY2VpdmVyU3RyYXRlZ3kgfHwgKGV4cG9ydHMuSWRDYW5jZWxsYXRpb25SZWNlaXZlclN0cmF0ZWd5ID0gSWRDYW5jZWxsYXRpb25SZWNlaXZlclN0cmF0ZWd5ID0ge30pKTtcbnZhciBSZXF1ZXN0Q2FuY2VsbGF0aW9uUmVjZWl2ZXJTdHJhdGVneTtcbihmdW5jdGlvbiAoUmVxdWVzdENhbmNlbGxhdGlvblJlY2VpdmVyU3RyYXRlZ3kpIHtcbiAgICBmdW5jdGlvbiBpcyh2YWx1ZSkge1xuICAgICAgICBjb25zdCBjYW5kaWRhdGUgPSB2YWx1ZTtcbiAgICAgICAgcmV0dXJuIGNhbmRpZGF0ZSAmJiBjYW5kaWRhdGUua2luZCA9PT0gJ3JlcXVlc3QnICYmIElzLmZ1bmMoY2FuZGlkYXRlLmNyZWF0ZUNhbmNlbGxhdGlvblRva2VuU291cmNlKSAmJiAoY2FuZGlkYXRlLmRpc3Bvc2UgPT09IHVuZGVmaW5lZCB8fCBJcy5mdW5jKGNhbmRpZGF0ZS5kaXNwb3NlKSk7XG4gICAgfVxuICAgIFJlcXVlc3RDYW5jZWxsYXRpb25SZWNlaXZlclN0cmF0ZWd5LmlzID0gaXM7XG59KShSZXF1ZXN0Q2FuY2VsbGF0aW9uUmVjZWl2ZXJTdHJhdGVneSB8fCAoZXhwb3J0cy5SZXF1ZXN0Q2FuY2VsbGF0aW9uUmVjZWl2ZXJTdHJhdGVneSA9IFJlcXVlc3RDYW5jZWxsYXRpb25SZWNlaXZlclN0cmF0ZWd5ID0ge30pKTtcbnZhciBDYW5jZWxsYXRpb25SZWNlaXZlclN0cmF0ZWd5O1xuKGZ1bmN0aW9uIChDYW5jZWxsYXRpb25SZWNlaXZlclN0cmF0ZWd5KSB7XG4gICAgQ2FuY2VsbGF0aW9uUmVjZWl2ZXJTdHJhdGVneS5NZXNzYWdlID0gT2JqZWN0LmZyZWV6ZSh7XG4gICAgICAgIGNyZWF0ZUNhbmNlbGxhdGlvblRva2VuU291cmNlKF8pIHtcbiAgICAgICAgICAgIHJldHVybiBuZXcgY2FuY2VsbGF0aW9uXzEuQ2FuY2VsbGF0aW9uVG9rZW5Tb3VyY2UoKTtcbiAgICAgICAgfVxuICAgIH0pO1xuICAgIGZ1bmN0aW9uIGlzKHZhbHVlKSB7XG4gICAgICAgIHJldHVybiBJZENhbmNlbGxhdGlvblJlY2VpdmVyU3RyYXRlZ3kuaXModmFsdWUpIHx8IFJlcXVlc3RDYW5jZWxsYXRpb25SZWNlaXZlclN0cmF0ZWd5LmlzKHZhbHVlKTtcbiAgICB9XG4gICAgQ2FuY2VsbGF0aW9uUmVjZWl2ZXJTdHJhdGVneS5pcyA9IGlzO1xufSkoQ2FuY2VsbGF0aW9uUmVjZWl2ZXJTdHJhdGVneSB8fCAoZXhwb3J0cy5DYW5jZWxsYXRpb25SZWNlaXZlclN0cmF0ZWd5ID0gQ2FuY2VsbGF0aW9uUmVjZWl2ZXJTdHJhdGVneSA9IHt9KSk7XG52YXIgQ2FuY2VsbGF0aW9uU2VuZGVyU3RyYXRlZ3k7XG4oZnVuY3Rpb24gKENhbmNlbGxhdGlvblNlbmRlclN0cmF0ZWd5KSB7XG4gICAgQ2FuY2VsbGF0aW9uU2VuZGVyU3RyYXRlZ3kuTWVzc2FnZSA9IE9iamVjdC5mcmVlemUoe1xuICAgICAgICBzZW5kQ2FuY2VsbGF0aW9uKGNvbm4sIGlkKSB7XG4gICAgICAgICAgICByZXR1cm4gY29ubi5zZW5kTm90aWZpY2F0aW9uKENhbmNlbE5vdGlmaWNhdGlvbi50eXBlLCB7IGlkIH0pO1xuICAgICAgICB9LFxuICAgICAgICBjbGVhbnVwKF8pIHsgfVxuICAgIH0pO1xuICAgIGZ1bmN0aW9uIGlzKHZhbHVlKSB7XG4gICAgICAgIGNvbnN0IGNhbmRpZGF0ZSA9IHZhbHVlO1xuICAgICAgICByZXR1cm4gY2FuZGlkYXRlICYmIElzLmZ1bmMoY2FuZGlkYXRlLnNlbmRDYW5jZWxsYXRpb24pICYmIElzLmZ1bmMoY2FuZGlkYXRlLmNsZWFudXApO1xuICAgIH1cbiAgICBDYW5jZWxsYXRpb25TZW5kZXJTdHJhdGVneS5pcyA9IGlzO1xufSkoQ2FuY2VsbGF0aW9uU2VuZGVyU3RyYXRlZ3kgfHwgKGV4cG9ydHMuQ2FuY2VsbGF0aW9uU2VuZGVyU3RyYXRlZ3kgPSBDYW5jZWxsYXRpb25TZW5kZXJTdHJhdGVneSA9IHt9KSk7XG52YXIgQ2FuY2VsbGF0aW9uU3RyYXRlZ3k7XG4oZnVuY3Rpb24gKENhbmNlbGxhdGlvblN0cmF0ZWd5KSB7XG4gICAgQ2FuY2VsbGF0aW9uU3RyYXRlZ3kuTWVzc2FnZSA9IE9iamVjdC5mcmVlemUoe1xuICAgICAgICByZWNlaXZlcjogQ2FuY2VsbGF0aW9uUmVjZWl2ZXJTdHJhdGVneS5NZXNzYWdlLFxuICAgICAgICBzZW5kZXI6IENhbmNlbGxhdGlvblNlbmRlclN0cmF0ZWd5Lk1lc3NhZ2VcbiAgICB9KTtcbiAgICBmdW5jdGlvbiBpcyh2YWx1ZSkge1xuICAgICAgICBjb25zdCBjYW5kaWRhdGUgPSB2YWx1ZTtcbiAgICAgICAgcmV0dXJuIGNhbmRpZGF0ZSAmJiBDYW5jZWxsYXRpb25SZWNlaXZlclN0cmF0ZWd5LmlzKGNhbmRpZGF0ZS5yZWNlaXZlcikgJiYgQ2FuY2VsbGF0aW9uU2VuZGVyU3RyYXRlZ3kuaXMoY2FuZGlkYXRlLnNlbmRlcik7XG4gICAgfVxuICAgIENhbmNlbGxhdGlvblN0cmF0ZWd5LmlzID0gaXM7XG59KShDYW5jZWxsYXRpb25TdHJhdGVneSB8fCAoZXhwb3J0cy5DYW5jZWxsYXRpb25TdHJhdGVneSA9IENhbmNlbGxhdGlvblN0cmF0ZWd5ID0ge30pKTtcbnZhciBNZXNzYWdlU3RyYXRlZ3k7XG4oZnVuY3Rpb24gKE1lc3NhZ2VTdHJhdGVneSkge1xuICAgIGZ1bmN0aW9uIGlzKHZhbHVlKSB7XG4gICAgICAgIGNvbnN0IGNhbmRpZGF0ZSA9IHZhbHVlO1xuICAgICAgICByZXR1cm4gY2FuZGlkYXRlICYmIElzLmZ1bmMoY2FuZGlkYXRlLmhhbmRsZU1lc3NhZ2UpO1xuICAgIH1cbiAgICBNZXNzYWdlU3RyYXRlZ3kuaXMgPSBpcztcbn0pKE1lc3NhZ2VTdHJhdGVneSB8fCAoZXhwb3J0cy5NZXNzYWdlU3RyYXRlZ3kgPSBNZXNzYWdlU3RyYXRlZ3kgPSB7fSkpO1xudmFyIENvbm5lY3Rpb25PcHRpb25zO1xuKGZ1bmN0aW9uIChDb25uZWN0aW9uT3B0aW9ucykge1xuICAgIGZ1bmN0aW9uIGlzKHZhbHVlKSB7XG4gICAgICAgIGNvbnN0IGNhbmRpZGF0ZSA9IHZhbHVlO1xuICAgICAgICByZXR1cm4gY2FuZGlkYXRlICYmIChDYW5jZWxsYXRpb25TdHJhdGVneS5pcyhjYW5kaWRhdGUuY2FuY2VsbGF0aW9uU3RyYXRlZ3kpIHx8IENvbm5lY3Rpb25TdHJhdGVneS5pcyhjYW5kaWRhdGUuY29ubmVjdGlvblN0cmF0ZWd5KSB8fCBNZXNzYWdlU3RyYXRlZ3kuaXMoY2FuZGlkYXRlLm1lc3NhZ2VTdHJhdGVneSkpO1xuICAgIH1cbiAgICBDb25uZWN0aW9uT3B0aW9ucy5pcyA9IGlzO1xufSkoQ29ubmVjdGlvbk9wdGlvbnMgfHwgKGV4cG9ydHMuQ29ubmVjdGlvbk9wdGlvbnMgPSBDb25uZWN0aW9uT3B0aW9ucyA9IHt9KSk7XG52YXIgQ29ubmVjdGlvblN0YXRlO1xuKGZ1bmN0aW9uIChDb25uZWN0aW9uU3RhdGUpIHtcbiAgICBDb25uZWN0aW9uU3RhdGVbQ29ubmVjdGlvblN0YXRlW1wiTmV3XCJdID0gMV0gPSBcIk5ld1wiO1xuICAgIENvbm5lY3Rpb25TdGF0ZVtDb25uZWN0aW9uU3RhdGVbXCJMaXN0ZW5pbmdcIl0gPSAyXSA9IFwiTGlzdGVuaW5nXCI7XG4gICAgQ29ubmVjdGlvblN0YXRlW0Nvbm5lY3Rpb25TdGF0ZVtcIkNsb3NlZFwiXSA9IDNdID0gXCJDbG9zZWRcIjtcbiAgICBDb25uZWN0aW9uU3RhdGVbQ29ubmVjdGlvblN0YXRlW1wiRGlzcG9zZWRcIl0gPSA0XSA9IFwiRGlzcG9zZWRcIjtcbn0pKENvbm5lY3Rpb25TdGF0ZSB8fCAoQ29ubmVjdGlvblN0YXRlID0ge30pKTtcbmZ1bmN0aW9uIGNyZWF0ZU1lc3NhZ2VDb25uZWN0aW9uKG1lc3NhZ2VSZWFkZXIsIG1lc3NhZ2VXcml0ZXIsIF9sb2dnZXIsIG9wdGlvbnMpIHtcbiAgICBjb25zdCBsb2dnZXIgPSBfbG9nZ2VyICE9PSB1bmRlZmluZWQgPyBfbG9nZ2VyIDogZXhwb3J0cy5OdWxsTG9nZ2VyO1xuICAgIGxldCBzZXF1ZW5jZU51bWJlciA9IDA7XG4gICAgbGV0IG5vdGlmaWNhdGlvblNlcXVlbmNlTnVtYmVyID0gMDtcbiAgICBsZXQgdW5rbm93blJlc3BvbnNlU2VxdWVuY2VOdW1iZXIgPSAwO1xuICAgIGNvbnN0IHZlcnNpb24gPSAnMi4wJztcbiAgICBsZXQgc3RhclJlcXVlc3RIYW5kbGVyID0gdW5kZWZpbmVkO1xuICAgIGNvbnN0IHJlcXVlc3RIYW5kbGVycyA9IG5ldyBNYXAoKTtcbiAgICBsZXQgc3Rhck5vdGlmaWNhdGlvbkhhbmRsZXIgPSB1bmRlZmluZWQ7XG4gICAgY29uc3Qgbm90aWZpY2F0aW9uSGFuZGxlcnMgPSBuZXcgTWFwKCk7XG4gICAgY29uc3QgcHJvZ3Jlc3NIYW5kbGVycyA9IG5ldyBNYXAoKTtcbiAgICBsZXQgdGltZXI7XG4gICAgbGV0IG1lc3NhZ2VRdWV1ZSA9IG5ldyBsaW5rZWRNYXBfMS5MaW5rZWRNYXAoKTtcbiAgICBsZXQgcmVzcG9uc2VQcm9taXNlcyA9IG5ldyBNYXAoKTtcbiAgICBsZXQga25vd25DYW5jZWxlZFJlcXVlc3RzID0gbmV3IFNldCgpO1xuICAgIGxldCByZXF1ZXN0VG9rZW5zID0gbmV3IE1hcCgpO1xuICAgIGxldCB0cmFjZSA9IFRyYWNlLk9mZjtcbiAgICBsZXQgdHJhY2VGb3JtYXQgPSBUcmFjZUZvcm1hdC5UZXh0O1xuICAgIGxldCB0cmFjZXI7XG4gICAgbGV0IHN0YXRlID0gQ29ubmVjdGlvblN0YXRlLk5ldztcbiAgICBjb25zdCBlcnJvckVtaXR0ZXIgPSBuZXcgZXZlbnRzXzEuRW1pdHRlcigpO1xuICAgIGNvbnN0IGNsb3NlRW1pdHRlciA9IG5ldyBldmVudHNfMS5FbWl0dGVyKCk7XG4gICAgY29uc3QgdW5oYW5kbGVkTm90aWZpY2F0aW9uRW1pdHRlciA9IG5ldyBldmVudHNfMS5FbWl0dGVyKCk7XG4gICAgY29uc3QgdW5oYW5kbGVkUHJvZ3Jlc3NFbWl0dGVyID0gbmV3IGV2ZW50c18xLkVtaXR0ZXIoKTtcbiAgICBjb25zdCBkaXNwb3NlRW1pdHRlciA9IG5ldyBldmVudHNfMS5FbWl0dGVyKCk7XG4gICAgY29uc3QgY2FuY2VsbGF0aW9uU3RyYXRlZ3kgPSAob3B0aW9ucyAmJiBvcHRpb25zLmNhbmNlbGxhdGlvblN0cmF0ZWd5KSA/IG9wdGlvbnMuY2FuY2VsbGF0aW9uU3RyYXRlZ3kgOiBDYW5jZWxsYXRpb25TdHJhdGVneS5NZXNzYWdlO1xuICAgIGZ1bmN0aW9uIGNyZWF0ZVJlcXVlc3RRdWV1ZUtleShpZCkge1xuICAgICAgICBpZiAoaWQgPT09IG51bGwpIHtcbiAgICAgICAgICAgIHRocm93IG5ldyBFcnJvcihgQ2FuJ3Qgc2VuZCByZXF1ZXN0cyB3aXRoIGlkIG51bGwgc2luY2UgdGhlIHJlc3BvbnNlIGNhbid0IGJlIGNvcnJlbGF0ZWQuYCk7XG4gICAgICAgIH1cbiAgICAgICAgcmV0dXJuICdyZXEtJyArIGlkLnRvU3RyaW5nKCk7XG4gICAgfVxuICAgIGZ1bmN0aW9uIGNyZWF0ZVJlc3BvbnNlUXVldWVLZXkoaWQpIHtcbiAgICAgICAgaWYgKGlkID09PSBudWxsKSB7XG4gICAgICAgICAgICByZXR1cm4gJ3Jlcy11bmtub3duLScgKyAoKyt1bmtub3duUmVzcG9uc2VTZXF1ZW5jZU51bWJlcikudG9TdHJpbmcoKTtcbiAgICAgICAgfVxuICAgICAgICBlbHNlIHtcbiAgICAgICAgICAgIHJldHVybiAncmVzLScgKyBpZC50b1N0cmluZygpO1xuICAgICAgICB9XG4gICAgfVxuICAgIGZ1bmN0aW9uIGNyZWF0ZU5vdGlmaWNhdGlvblF1ZXVlS2V5KCkge1xuICAgICAgICByZXR1cm4gJ25vdC0nICsgKCsrbm90aWZpY2F0aW9uU2VxdWVuY2VOdW1iZXIpLnRvU3RyaW5nKCk7XG4gICAgfVxuICAgIGZ1bmN0aW9uIGFkZE1lc3NhZ2VUb1F1ZXVlKHF1ZXVlLCBtZXNzYWdlKSB7XG4gICAgICAgIGlmIChtZXNzYWdlc18xLk1lc3NhZ2UuaXNSZXF1ZXN0KG1lc3NhZ2UpKSB7XG4gICAgICAgICAgICBxdWV1ZS5zZXQoY3JlYXRlUmVxdWVzdFF1ZXVlS2V5KG1lc3NhZ2UuaWQpLCBtZXNzYWdlKTtcbiAgICAgICAgfVxuICAgICAgICBlbHNlIGlmIChtZXNzYWdlc18xLk1lc3NhZ2UuaXNSZXNwb25zZShtZXNzYWdlKSkge1xuICAgICAgICAgICAgcXVldWUuc2V0KGNyZWF0ZVJlc3BvbnNlUXVldWVLZXkobWVzc2FnZS5pZCksIG1lc3NhZ2UpO1xuICAgICAgICB9XG4gICAgICAgIGVsc2Uge1xuICAgICAgICAgICAgcXVldWUuc2V0KGNyZWF0ZU5vdGlmaWNhdGlvblF1ZXVlS2V5KCksIG1lc3NhZ2UpO1xuICAgICAgICB9XG4gICAgfVxuICAgIGZ1bmN0aW9uIGNhbmNlbFVuZGlzcGF0Y2hlZChfbWVzc2FnZSkge1xuICAgICAgICByZXR1cm4gdW5kZWZpbmVkO1xuICAgIH1cbiAgICBmdW5jdGlvbiBpc0xpc3RlbmluZygpIHtcbiAgICAgICAgcmV0dXJuIHN0YXRlID09PSBDb25uZWN0aW9uU3RhdGUuTGlzdGVuaW5nO1xuICAgIH1cbiAgICBmdW5jdGlvbiBpc0Nsb3NlZCgpIHtcbiAgICAgICAgcmV0dXJuIHN0YXRlID09PSBDb25uZWN0aW9uU3RhdGUuQ2xvc2VkO1xuICAgIH1cbiAgICBmdW5jdGlvbiBpc0Rpc3Bvc2VkKCkge1xuICAgICAgICByZXR1cm4gc3RhdGUgPT09IENvbm5lY3Rpb25TdGF0ZS5EaXNwb3NlZDtcbiAgICB9XG4gICAgZnVuY3Rpb24gY2xvc2VIYW5kbGVyKCkge1xuICAgICAgICBpZiAoc3RhdGUgPT09IENvbm5lY3Rpb25TdGF0ZS5OZXcgfHwgc3RhdGUgPT09IENvbm5lY3Rpb25TdGF0ZS5MaXN0ZW5pbmcpIHtcbiAgICAgICAgICAgIHN0YXRlID0gQ29ubmVjdGlvblN0YXRlLkNsb3NlZDtcbiAgICAgICAgICAgIGNsb3NlRW1pdHRlci5maXJlKHVuZGVmaW5lZCk7XG4gICAgICAgIH1cbiAgICAgICAgLy8gSWYgdGhlIGNvbm5lY3Rpb24gaXMgZGlzcG9zZWQgZG9uJ3Qgc2VudCBjbG9zZSBldmVudHMuXG4gICAgfVxuICAgIGZ1bmN0aW9uIHJlYWRFcnJvckhhbmRsZXIoZXJyb3IpIHtcbiAgICAgICAgZXJyb3JFbWl0dGVyLmZpcmUoW2Vycm9yLCB1bmRlZmluZWQsIHVuZGVmaW5lZF0pO1xuICAgIH1cbiAgICBmdW5jdGlvbiB3cml0ZUVycm9ySGFuZGxlcihkYXRhKSB7XG4gICAgICAgIGVycm9yRW1pdHRlci5maXJlKGRhdGEpO1xuICAgIH1cbiAgICBtZXNzYWdlUmVhZGVyLm9uQ2xvc2UoY2xvc2VIYW5kbGVyKTtcbiAgICBtZXNzYWdlUmVhZGVyLm9uRXJyb3IocmVhZEVycm9ySGFuZGxlcik7XG4gICAgbWVzc2FnZVdyaXRlci5vbkNsb3NlKGNsb3NlSGFuZGxlcik7XG4gICAgbWVzc2FnZVdyaXRlci5vbkVycm9yKHdyaXRlRXJyb3JIYW5kbGVyKTtcbiAgICBmdW5jdGlvbiB0cmlnZ2VyTWVzc2FnZVF1ZXVlKCkge1xuICAgICAgICBpZiAodGltZXIgfHwgbWVzc2FnZVF1ZXVlLnNpemUgPT09IDApIHtcbiAgICAgICAgICAgIHJldHVybjtcbiAgICAgICAgfVxuICAgICAgICB0aW1lciA9ICgwLCByYWxfMS5kZWZhdWx0KSgpLnRpbWVyLnNldEltbWVkaWF0ZSgoKSA9PiB7XG4gICAgICAgICAgICB0aW1lciA9IHVuZGVmaW5lZDtcbiAgICAgICAgICAgIHByb2Nlc3NNZXNzYWdlUXVldWUoKTtcbiAgICAgICAgfSk7XG4gICAgfVxuICAgIGZ1bmN0aW9uIGhhbmRsZU1lc3NhZ2UobWVzc2FnZSkge1xuICAgICAgICBpZiAobWVzc2FnZXNfMS5NZXNzYWdlLmlzUmVxdWVzdChtZXNzYWdlKSkge1xuICAgICAgICAgICAgaGFuZGxlUmVxdWVzdChtZXNzYWdlKTtcbiAgICAgICAgfVxuICAgICAgICBlbHNlIGlmIChtZXNzYWdlc18xLk1lc3NhZ2UuaXNOb3RpZmljYXRpb24obWVzc2FnZSkpIHtcbiAgICAgICAgICAgIGhhbmRsZU5vdGlmaWNhdGlvbihtZXNzYWdlKTtcbiAgICAgICAgfVxuICAgICAgICBlbHNlIGlmIChtZXNzYWdlc18xLk1lc3NhZ2UuaXNSZXNwb25zZShtZXNzYWdlKSkge1xuICAgICAgICAgICAgaGFuZGxlUmVzcG9uc2UobWVzc2FnZSk7XG4gICAgICAgIH1cbiAgICAgICAgZWxzZSB7XG4gICAgICAgICAgICBoYW5kbGVJbnZhbGlkTWVzc2FnZShtZXNzYWdlKTtcbiAgICAgICAgfVxuICAgIH1cbiAgICBmdW5jdGlvbiBwcm9jZXNzTWVzc2FnZVF1ZXVlKCkge1xuICAgICAgICBpZiAobWVzc2FnZVF1ZXVlLnNpemUgPT09IDApIHtcbiAgICAgICAgICAgIHJldHVybjtcbiAgICAgICAgfVxuICAgICAgICBjb25zdCBtZXNzYWdlID0gbWVzc2FnZVF1ZXVlLnNoaWZ0KCk7XG4gICAgICAgIHRyeSB7XG4gICAgICAgICAgICBjb25zdCBtZXNzYWdlU3RyYXRlZ3kgPSBvcHRpb25zPy5tZXNzYWdlU3RyYXRlZ3k7XG4gICAgICAgICAgICBpZiAoTWVzc2FnZVN0cmF0ZWd5LmlzKG1lc3NhZ2VTdHJhdGVneSkpIHtcbiAgICAgICAgICAgICAgICBtZXNzYWdlU3RyYXRlZ3kuaGFuZGxlTWVzc2FnZShtZXNzYWdlLCBoYW5kbGVNZXNzYWdlKTtcbiAgICAgICAgICAgIH1cbiAgICAgICAgICAgIGVsc2Uge1xuICAgICAgICAgICAgICAgIGhhbmRsZU1lc3NhZ2UobWVzc2FnZSk7XG4gICAgICAgICAgICB9XG4gICAgICAgIH1cbiAgICAgICAgZmluYWxseSB7XG4gICAgICAgICAgICB0cmlnZ2VyTWVzc2FnZVF1ZXVlKCk7XG4gICAgICAgIH1cbiAgICB9XG4gICAgY29uc3QgY2FsbGJhY2sgPSAobWVzc2FnZSkgPT4ge1xuICAgICAgICB0cnkge1xuICAgICAgICAgICAgLy8gV2UgaGF2ZSByZWNlaXZlZCBhIGNhbmNlbGxhdGlvbiBtZXNzYWdlLiBDaGVjayBpZiB0aGUgbWVzc2FnZSBpcyBzdGlsbCBpbiB0aGUgcXVldWVcbiAgICAgICAgICAgIC8vIGFuZCBjYW5jZWwgaXQgaWYgYWxsb3dlZCB0byBkbyBzby5cbiAgICAgICAgICAgIGlmIChtZXNzYWdlc18xLk1lc3NhZ2UuaXNOb3RpZmljYXRpb24obWVzc2FnZSkgJiYgbWVzc2FnZS5tZXRob2QgPT09IENhbmNlbE5vdGlmaWNhdGlvbi50eXBlLm1ldGhvZCkge1xuICAgICAgICAgICAgICAgIGNvbnN0IGNhbmNlbElkID0gbWVzc2FnZS5wYXJhbXMuaWQ7XG4gICAgICAgICAgICAgICAgY29uc3Qga2V5ID0gY3JlYXRlUmVxdWVzdFF1ZXVlS2V5KGNhbmNlbElkKTtcbiAgICAgICAgICAgICAgICBjb25zdCB0b0NhbmNlbCA9IG1lc3NhZ2VRdWV1ZS5nZXQoa2V5KTtcbiAgICAgICAgICAgICAgICBpZiAobWVzc2FnZXNfMS5NZXNzYWdlLmlzUmVxdWVzdCh0b0NhbmNlbCkpIHtcbiAgICAgICAgICAgICAgICAgICAgY29uc3Qgc3RyYXRlZ3kgPSBvcHRpb25zPy5jb25uZWN0aW9uU3RyYXRlZ3k7XG4gICAgICAgICAgICAgICAgICAgIGNvbnN0IHJlc3BvbnNlID0gKHN0cmF0ZWd5ICYmIHN0cmF0ZWd5LmNhbmNlbFVuZGlzcGF0Y2hlZCkgPyBzdHJhdGVneS5jYW5jZWxVbmRpc3BhdGNoZWQodG9DYW5jZWwsIGNhbmNlbFVuZGlzcGF0Y2hlZCkgOiBjYW5jZWxVbmRpc3BhdGNoZWQodG9DYW5jZWwpO1xuICAgICAgICAgICAgICAgICAgICBpZiAocmVzcG9uc2UgJiYgKHJlc3BvbnNlLmVycm9yICE9PSB1bmRlZmluZWQgfHwgcmVzcG9uc2UucmVzdWx0ICE9PSB1bmRlZmluZWQpKSB7XG4gICAgICAgICAgICAgICAgICAgICAgICBtZXNzYWdlUXVldWUuZGVsZXRlKGtleSk7XG4gICAgICAgICAgICAgICAgICAgICAgICByZXF1ZXN0VG9rZW5zLmRlbGV0ZShjYW5jZWxJZCk7XG4gICAgICAgICAgICAgICAgICAgICAgICByZXNwb25zZS5pZCA9IHRvQ2FuY2VsLmlkO1xuICAgICAgICAgICAgICAgICAgICAgICAgdHJhY2VTZW5kaW5nUmVzcG9uc2UocmVzcG9uc2UsIG1lc3NhZ2UubWV0aG9kLCBEYXRlLm5vdygpKTtcbiAgICAgICAgICAgICAgICAgICAgICAgIG1lc3NhZ2VXcml0ZXIud3JpdGUocmVzcG9uc2UpLmNhdGNoKCgpID0+IGxvZ2dlci5lcnJvcihgU2VuZGluZyByZXNwb25zZSBmb3IgY2FuY2VsZWQgbWVzc2FnZSBmYWlsZWQuYCkpO1xuICAgICAgICAgICAgICAgICAgICAgICAgcmV0dXJuO1xuICAgICAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgIGNvbnN0IGNhbmNlbGxhdGlvblRva2VuID0gcmVxdWVzdFRva2Vucy5nZXQoY2FuY2VsSWQpO1xuICAgICAgICAgICAgICAgIC8vIFRoZSByZXF1ZXN0IGlzIGFscmVhZHkgcnVubmluZy4gQ2FuY2VsIHRoZSB0b2tlblxuICAgICAgICAgICAgICAgIGlmIChjYW5jZWxsYXRpb25Ub2tlbiAhPT0gdW5kZWZpbmVkKSB7XG4gICAgICAgICAgICAgICAgICAgIGNhbmNlbGxhdGlvblRva2VuLmNhbmNlbCgpO1xuICAgICAgICAgICAgICAgICAgICB0cmFjZVJlY2VpdmVkTm90aWZpY2F0aW9uKG1lc3NhZ2UpO1xuICAgICAgICAgICAgICAgICAgICByZXR1cm47XG4gICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgIGVsc2Uge1xuICAgICAgICAgICAgICAgICAgICAvLyBSZW1lbWJlciB0aGUgY2FuY2VsIGJ1dCBzdGlsbCBxdWV1ZSB0aGUgbWVzc2FnZSB0b1xuICAgICAgICAgICAgICAgICAgICAvLyBjbGVhbiB1cCBzdGF0ZSBpbiBwcm9jZXNzIG1lc3NhZ2UuXG4gICAgICAgICAgICAgICAgICAgIGtub3duQ2FuY2VsZWRSZXF1ZXN0cy5hZGQoY2FuY2VsSWQpO1xuICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgIH1cbiAgICAgICAgICAgIGFkZE1lc3NhZ2VUb1F1ZXVlKG1lc3NhZ2VRdWV1ZSwgbWVzc2FnZSk7XG4gICAgICAgIH1cbiAgICAgICAgZmluYWxseSB7XG4gICAgICAgICAgICB0cmlnZ2VyTWVzc2FnZVF1ZXVlKCk7XG4gICAgICAgIH1cbiAgICB9O1xuICAgIGZ1bmN0aW9uIGhhbmRsZVJlcXVlc3QocmVxdWVzdE1lc3NhZ2UpIHtcbiAgICAgICAgaWYgKGlzRGlzcG9zZWQoKSkge1xuICAgICAgICAgICAgLy8gd2UgcmV0dXJuIGhlcmUgc2lsZW50bHkgc2luY2Ugd2UgZmlyZWQgYW4gZXZlbnQgd2hlbiB0aGVcbiAgICAgICAgICAgIC8vIGNvbm5lY3Rpb24gZ290IGRpc3Bvc2VkLlxuICAgICAgICAgICAgcmV0dXJuO1xuICAgICAgICB9XG4gICAgICAgIGZ1bmN0aW9uIHJlcGx5KHJlc3VsdE9yRXJyb3IsIG1ldGhvZCwgc3RhcnRUaW1lKSB7XG4gICAgICAgICAgICBjb25zdCBtZXNzYWdlID0ge1xuICAgICAgICAgICAgICAgIGpzb25ycGM6IHZlcnNpb24sXG4gICAgICAgICAgICAgICAgaWQ6IHJlcXVlc3RNZXNzYWdlLmlkXG4gICAgICAgICAgICB9O1xuICAgICAgICAgICAgaWYgKHJlc3VsdE9yRXJyb3IgaW5zdGFuY2VvZiBtZXNzYWdlc18xLlJlc3BvbnNlRXJyb3IpIHtcbiAgICAgICAgICAgICAgICBtZXNzYWdlLmVycm9yID0gcmVzdWx0T3JFcnJvci50b0pzb24oKTtcbiAgICAgICAgICAgIH1cbiAgICAgICAgICAgIGVsc2Uge1xuICAgICAgICAgICAgICAgIG1lc3NhZ2UucmVzdWx0ID0gcmVzdWx0T3JFcnJvciA9PT0gdW5kZWZpbmVkID8gbnVsbCA6IHJlc3VsdE9yRXJyb3I7XG4gICAgICAgICAgICB9XG4gICAgICAgICAgICB0cmFjZVNlbmRpbmdSZXNwb25zZShtZXNzYWdlLCBtZXRob2QsIHN0YXJ0VGltZSk7XG4gICAgICAgICAgICBtZXNzYWdlV3JpdGVyLndyaXRlKG1lc3NhZ2UpLmNhdGNoKCgpID0+IGxvZ2dlci5lcnJvcihgU2VuZGluZyByZXNwb25zZSBmYWlsZWQuYCkpO1xuICAgICAgICB9XG4gICAgICAgIGZ1bmN0aW9uIHJlcGx5RXJyb3IoZXJyb3IsIG1ldGhvZCwgc3RhcnRUaW1lKSB7XG4gICAgICAgICAgICBjb25zdCBtZXNzYWdlID0ge1xuICAgICAgICAgICAgICAgIGpzb25ycGM6IHZlcnNpb24sXG4gICAgICAgICAgICAgICAgaWQ6IHJlcXVlc3RNZXNzYWdlLmlkLFxuICAgICAgICAgICAgICAgIGVycm9yOiBlcnJvci50b0pzb24oKVxuICAgICAgICAgICAgfTtcbiAgICAgICAgICAgIHRyYWNlU2VuZGluZ1Jlc3BvbnNlKG1lc3NhZ2UsIG1ldGhvZCwgc3RhcnRUaW1lKTtcbiAgICAgICAgICAgIG1lc3NhZ2VXcml0ZXIud3JpdGUobWVzc2FnZSkuY2F0Y2goKCkgPT4gbG9nZ2VyLmVycm9yKGBTZW5kaW5nIHJlc3BvbnNlIGZhaWxlZC5gKSk7XG4gICAgICAgIH1cbiAgICAgICAgZnVuY3Rpb24gcmVwbHlTdWNjZXNzKHJlc3VsdCwgbWV0aG9kLCBzdGFydFRpbWUpIHtcbiAgICAgICAgICAgIC8vIFRoZSBKU09OIFJQQyBkZWZpbmVzIHRoYXQgYSByZXNwb25zZSBtdXN0IGVpdGhlciBoYXZlIGEgcmVzdWx0IG9yIGFuIGVycm9yXG4gICAgICAgICAgICAvLyBTbyB3ZSBjYW4ndCB0cmVhdCB1bmRlZmluZWQgYXMgYSB2YWxpZCByZXNwb25zZSByZXN1bHQuXG4gICAgICAgICAgICBpZiAocmVzdWx0ID09PSB1bmRlZmluZWQpIHtcbiAgICAgICAgICAgICAgICByZXN1bHQgPSBudWxsO1xuICAgICAgICAgICAgfVxuICAgICAgICAgICAgY29uc3QgbWVzc2FnZSA9IHtcbiAgICAgICAgICAgICAgICBqc29ucnBjOiB2ZXJzaW9uLFxuICAgICAgICAgICAgICAgIGlkOiByZXF1ZXN0TWVzc2FnZS5pZCxcbiAgICAgICAgICAgICAgICByZXN1bHQ6IHJlc3VsdFxuICAgICAgICAgICAgfTtcbiAgICAgICAgICAgIHRyYWNlU2VuZGluZ1Jlc3BvbnNlKG1lc3NhZ2UsIG1ldGhvZCwgc3RhcnRUaW1lKTtcbiAgICAgICAgICAgIG1lc3NhZ2VXcml0ZXIud3JpdGUobWVzc2FnZSkuY2F0Y2goKCkgPT4gbG9nZ2VyLmVycm9yKGBTZW5kaW5nIHJlc3BvbnNlIGZhaWxlZC5gKSk7XG4gICAgICAgIH1cbiAgICAgICAgdHJhY2VSZWNlaXZlZFJlcXVlc3QocmVxdWVzdE1lc3NhZ2UpO1xuICAgICAgICBjb25zdCBlbGVtZW50ID0gcmVxdWVzdEhhbmRsZXJzLmdldChyZXF1ZXN0TWVzc2FnZS5tZXRob2QpO1xuICAgICAgICBsZXQgdHlwZTtcbiAgICAgICAgbGV0IHJlcXVlc3RIYW5kbGVyO1xuICAgICAgICBpZiAoZWxlbWVudCkge1xuICAgICAgICAgICAgdHlwZSA9IGVsZW1lbnQudHlwZTtcbiAgICAgICAgICAgIHJlcXVlc3RIYW5kbGVyID0gZWxlbWVudC5oYW5kbGVyO1xuICAgICAgICB9XG4gICAgICAgIGNvbnN0IHN0YXJ0VGltZSA9IERhdGUubm93KCk7XG4gICAgICAgIGlmIChyZXF1ZXN0SGFuZGxlciB8fCBzdGFyUmVxdWVzdEhhbmRsZXIpIHtcbiAgICAgICAgICAgIGNvbnN0IHRva2VuS2V5ID0gcmVxdWVzdE1lc3NhZ2UuaWQgPz8gU3RyaW5nKERhdGUubm93KCkpOyAvL1xuICAgICAgICAgICAgY29uc3QgY2FuY2VsbGF0aW9uU291cmNlID0gSWRDYW5jZWxsYXRpb25SZWNlaXZlclN0cmF0ZWd5LmlzKGNhbmNlbGxhdGlvblN0cmF0ZWd5LnJlY2VpdmVyKVxuICAgICAgICAgICAgICAgID8gY2FuY2VsbGF0aW9uU3RyYXRlZ3kucmVjZWl2ZXIuY3JlYXRlQ2FuY2VsbGF0aW9uVG9rZW5Tb3VyY2UodG9rZW5LZXkpXG4gICAgICAgICAgICAgICAgOiBjYW5jZWxsYXRpb25TdHJhdGVneS5yZWNlaXZlci5jcmVhdGVDYW5jZWxsYXRpb25Ub2tlblNvdXJjZShyZXF1ZXN0TWVzc2FnZSk7XG4gICAgICAgICAgICBpZiAocmVxdWVzdE1lc3NhZ2UuaWQgIT09IG51bGwgJiYga25vd25DYW5jZWxlZFJlcXVlc3RzLmhhcyhyZXF1ZXN0TWVzc2FnZS5pZCkpIHtcbiAgICAgICAgICAgICAgICBjYW5jZWxsYXRpb25Tb3VyY2UuY2FuY2VsKCk7XG4gICAgICAgICAgICB9XG4gICAgICAgICAgICBpZiAocmVxdWVzdE1lc3NhZ2UuaWQgIT09IG51bGwpIHtcbiAgICAgICAgICAgICAgICByZXF1ZXN0VG9rZW5zLnNldCh0b2tlbktleSwgY2FuY2VsbGF0aW9uU291cmNlKTtcbiAgICAgICAgICAgIH1cbiAgICAgICAgICAgIHRyeSB7XG4gICAgICAgICAgICAgICAgbGV0IGhhbmRsZXJSZXN1bHQ7XG4gICAgICAgICAgICAgICAgaWYgKHJlcXVlc3RIYW5kbGVyKSB7XG4gICAgICAgICAgICAgICAgICAgIGlmIChyZXF1ZXN0TWVzc2FnZS5wYXJhbXMgPT09IHVuZGVmaW5lZCkge1xuICAgICAgICAgICAgICAgICAgICAgICAgaWYgKHR5cGUgIT09IHVuZGVmaW5lZCAmJiB0eXBlLm51bWJlck9mUGFyYW1zICE9PSAwKSB7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgcmVwbHlFcnJvcihuZXcgbWVzc2FnZXNfMS5SZXNwb25zZUVycm9yKG1lc3NhZ2VzXzEuRXJyb3JDb2Rlcy5JbnZhbGlkUGFyYW1zLCBgUmVxdWVzdCAke3JlcXVlc3RNZXNzYWdlLm1ldGhvZH0gZGVmaW5lcyAke3R5cGUubnVtYmVyT2ZQYXJhbXN9IHBhcmFtcyBidXQgcmVjZWl2ZWQgbm9uZS5gKSwgcmVxdWVzdE1lc3NhZ2UubWV0aG9kLCBzdGFydFRpbWUpO1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgIHJldHVybjtcbiAgICAgICAgICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICAgICAgICAgIGhhbmRsZXJSZXN1bHQgPSByZXF1ZXN0SGFuZGxlcihjYW5jZWxsYXRpb25Tb3VyY2UudG9rZW4pO1xuICAgICAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgICAgIGVsc2UgaWYgKEFycmF5LmlzQXJyYXkocmVxdWVzdE1lc3NhZ2UucGFyYW1zKSkge1xuICAgICAgICAgICAgICAgICAgICAgICAgaWYgKHR5cGUgIT09IHVuZGVmaW5lZCAmJiB0eXBlLnBhcmFtZXRlclN0cnVjdHVyZXMgPT09IG1lc3NhZ2VzXzEuUGFyYW1ldGVyU3RydWN0dXJlcy5ieU5hbWUpIHtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICByZXBseUVycm9yKG5ldyBtZXNzYWdlc18xLlJlc3BvbnNlRXJyb3IobWVzc2FnZXNfMS5FcnJvckNvZGVzLkludmFsaWRQYXJhbXMsIGBSZXF1ZXN0ICR7cmVxdWVzdE1lc3NhZ2UubWV0aG9kfSBkZWZpbmVzIHBhcmFtZXRlcnMgYnkgbmFtZSBidXQgcmVjZWl2ZWQgcGFyYW1ldGVycyBieSBwb3NpdGlvbmApLCByZXF1ZXN0TWVzc2FnZS5tZXRob2QsIHN0YXJ0VGltZSk7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgcmV0dXJuO1xuICAgICAgICAgICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgICAgICAgICAgaGFuZGxlclJlc3VsdCA9IHJlcXVlc3RIYW5kbGVyKC4uLnJlcXVlc3RNZXNzYWdlLnBhcmFtcywgY2FuY2VsbGF0aW9uU291cmNlLnRva2VuKTtcbiAgICAgICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgICAgICBlbHNlIHtcbiAgICAgICAgICAgICAgICAgICAgICAgIGlmICh0eXBlICE9PSB1bmRlZmluZWQgJiYgdHlwZS5wYXJhbWV0ZXJTdHJ1Y3R1cmVzID09PSBtZXNzYWdlc18xLlBhcmFtZXRlclN0cnVjdHVyZXMuYnlQb3NpdGlvbikge1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgIHJlcGx5RXJyb3IobmV3IG1lc3NhZ2VzXzEuUmVzcG9uc2VFcnJvcihtZXNzYWdlc18xLkVycm9yQ29kZXMuSW52YWxpZFBhcmFtcywgYFJlcXVlc3QgJHtyZXF1ZXN0TWVzc2FnZS5tZXRob2R9IGRlZmluZXMgcGFyYW1ldGVycyBieSBwb3NpdGlvbiBidXQgcmVjZWl2ZWQgcGFyYW1ldGVycyBieSBuYW1lYCksIHJlcXVlc3RNZXNzYWdlLm1ldGhvZCwgc3RhcnRUaW1lKTtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICByZXR1cm47XG4gICAgICAgICAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgICAgICAgICBoYW5kbGVyUmVzdWx0ID0gcmVxdWVzdEhhbmRsZXIocmVxdWVzdE1lc3NhZ2UucGFyYW1zLCBjYW5jZWxsYXRpb25Tb3VyY2UudG9rZW4pO1xuICAgICAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgIGVsc2UgaWYgKHN0YXJSZXF1ZXN0SGFuZGxlcikge1xuICAgICAgICAgICAgICAgICAgICBoYW5kbGVyUmVzdWx0ID0gc3RhclJlcXVlc3RIYW5kbGVyKHJlcXVlc3RNZXNzYWdlLm1ldGhvZCwgcmVxdWVzdE1lc3NhZ2UucGFyYW1zLCBjYW5jZWxsYXRpb25Tb3VyY2UudG9rZW4pO1xuICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICBjb25zdCBwcm9taXNlID0gaGFuZGxlclJlc3VsdDtcbiAgICAgICAgICAgICAgICBpZiAoIWhhbmRsZXJSZXN1bHQpIHtcbiAgICAgICAgICAgICAgICAgICAgcmVxdWVzdFRva2Vucy5kZWxldGUodG9rZW5LZXkpO1xuICAgICAgICAgICAgICAgICAgICByZXBseVN1Y2Nlc3MoaGFuZGxlclJlc3VsdCwgcmVxdWVzdE1lc3NhZ2UubWV0aG9kLCBzdGFydFRpbWUpO1xuICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICBlbHNlIGlmIChwcm9taXNlLnRoZW4pIHtcbiAgICAgICAgICAgICAgICAgICAgcHJvbWlzZS50aGVuKChyZXN1bHRPckVycm9yKSA9PiB7XG4gICAgICAgICAgICAgICAgICAgICAgICByZXF1ZXN0VG9rZW5zLmRlbGV0ZSh0b2tlbktleSk7XG4gICAgICAgICAgICAgICAgICAgICAgICByZXBseShyZXN1bHRPckVycm9yLCByZXF1ZXN0TWVzc2FnZS5tZXRob2QsIHN0YXJ0VGltZSk7XG4gICAgICAgICAgICAgICAgICAgIH0sIGVycm9yID0+IHtcbiAgICAgICAgICAgICAgICAgICAgICAgIHJlcXVlc3RUb2tlbnMuZGVsZXRlKHRva2VuS2V5KTtcbiAgICAgICAgICAgICAgICAgICAgICAgIGlmIChlcnJvciBpbnN0YW5jZW9mIG1lc3NhZ2VzXzEuUmVzcG9uc2VFcnJvcikge1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgIHJlcGx5RXJyb3IoZXJyb3IsIHJlcXVlc3RNZXNzYWdlLm1ldGhvZCwgc3RhcnRUaW1lKTtcbiAgICAgICAgICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICAgICAgICAgIGVsc2UgaWYgKGVycm9yICYmIElzLnN0cmluZyhlcnJvci5tZXNzYWdlKSkge1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgIHJlcGx5RXJyb3IobmV3IG1lc3NhZ2VzXzEuUmVzcG9uc2VFcnJvcihtZXNzYWdlc18xLkVycm9yQ29kZXMuSW50ZXJuYWxFcnJvciwgYFJlcXVlc3QgJHtyZXF1ZXN0TWVzc2FnZS5tZXRob2R9IGZhaWxlZCB3aXRoIG1lc3NhZ2U6ICR7ZXJyb3IubWVzc2FnZX1gKSwgcmVxdWVzdE1lc3NhZ2UubWV0aG9kLCBzdGFydFRpbWUpO1xuICAgICAgICAgICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgICAgICAgICAgZWxzZSB7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgcmVwbHlFcnJvcihuZXcgbWVzc2FnZXNfMS5SZXNwb25zZUVycm9yKG1lc3NhZ2VzXzEuRXJyb3JDb2Rlcy5JbnRlcm5hbEVycm9yLCBgUmVxdWVzdCAke3JlcXVlc3RNZXNzYWdlLm1ldGhvZH0gZmFpbGVkIHVuZXhwZWN0ZWRseSB3aXRob3V0IHByb3ZpZGluZyBhbnkgZGV0YWlscy5gKSwgcmVxdWVzdE1lc3NhZ2UubWV0aG9kLCBzdGFydFRpbWUpO1xuICAgICAgICAgICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgICAgICB9KTtcbiAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgZWxzZSB7XG4gICAgICAgICAgICAgICAgICAgIHJlcXVlc3RUb2tlbnMuZGVsZXRlKHRva2VuS2V5KTtcbiAgICAgICAgICAgICAgICAgICAgcmVwbHkoaGFuZGxlclJlc3VsdCwgcmVxdWVzdE1lc3NhZ2UubWV0aG9kLCBzdGFydFRpbWUpO1xuICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgIH1cbiAgICAgICAgICAgIGNhdGNoIChlcnJvcikge1xuICAgICAgICAgICAgICAgIHJlcXVlc3RUb2tlbnMuZGVsZXRlKHRva2VuS2V5KTtcbiAgICAgICAgICAgICAgICBpZiAoZXJyb3IgaW5zdGFuY2VvZiBtZXNzYWdlc18xLlJlc3BvbnNlRXJyb3IpIHtcbiAgICAgICAgICAgICAgICAgICAgcmVwbHkoZXJyb3IsIHJlcXVlc3RNZXNzYWdlLm1ldGhvZCwgc3RhcnRUaW1lKTtcbiAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgZWxzZSBpZiAoZXJyb3IgJiYgSXMuc3RyaW5nKGVycm9yLm1lc3NhZ2UpKSB7XG4gICAgICAgICAgICAgICAgICAgIHJlcGx5RXJyb3IobmV3IG1lc3NhZ2VzXzEuUmVzcG9uc2VFcnJvcihtZXNzYWdlc18xLkVycm9yQ29kZXMuSW50ZXJuYWxFcnJvciwgYFJlcXVlc3QgJHtyZXF1ZXN0TWVzc2FnZS5tZXRob2R9IGZhaWxlZCB3aXRoIG1lc3NhZ2U6ICR7ZXJyb3IubWVzc2FnZX1gKSwgcmVxdWVzdE1lc3NhZ2UubWV0aG9kLCBzdGFydFRpbWUpO1xuICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICBlbHNlIHtcbiAgICAgICAgICAgICAgICAgICAgcmVwbHlFcnJvcihuZXcgbWVzc2FnZXNfMS5SZXNwb25zZUVycm9yKG1lc3NhZ2VzXzEuRXJyb3JDb2Rlcy5JbnRlcm5hbEVycm9yLCBgUmVxdWVzdCAke3JlcXVlc3RNZXNzYWdlLm1ldGhvZH0gZmFpbGVkIHVuZXhwZWN0ZWRseSB3aXRob3V0IHByb3ZpZGluZyBhbnkgZGV0YWlscy5gKSwgcmVxdWVzdE1lc3NhZ2UubWV0aG9kLCBzdGFydFRpbWUpO1xuICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgIH1cbiAgICAgICAgfVxuICAgICAgICBlbHNlIHtcbiAgICAgICAgICAgIHJlcGx5RXJyb3IobmV3IG1lc3NhZ2VzXzEuUmVzcG9uc2VFcnJvcihtZXNzYWdlc18xLkVycm9yQ29kZXMuTWV0aG9kTm90Rm91bmQsIGBVbmhhbmRsZWQgbWV0aG9kICR7cmVxdWVzdE1lc3NhZ2UubWV0aG9kfWApLCByZXF1ZXN0TWVzc2FnZS5tZXRob2QsIHN0YXJ0VGltZSk7XG4gICAgICAgIH1cbiAgICB9XG4gICAgZnVuY3Rpb24gaGFuZGxlUmVzcG9uc2UocmVzcG9uc2VNZXNzYWdlKSB7XG4gICAgICAgIGlmIChpc0Rpc3Bvc2VkKCkpIHtcbiAgICAgICAgICAgIC8vIFNlZSBoYW5kbGUgcmVxdWVzdC5cbiAgICAgICAgICAgIHJldHVybjtcbiAgICAgICAgfVxuICAgICAgICBpZiAocmVzcG9uc2VNZXNzYWdlLmlkID09PSBudWxsKSB7XG4gICAgICAgICAgICBpZiAocmVzcG9uc2VNZXNzYWdlLmVycm9yKSB7XG4gICAgICAgICAgICAgICAgbG9nZ2VyLmVycm9yKGBSZWNlaXZlZCByZXNwb25zZSBtZXNzYWdlIHdpdGhvdXQgaWQ6IEVycm9yIGlzOiBcXG4ke0pTT04uc3RyaW5naWZ5KHJlc3BvbnNlTWVzc2FnZS5lcnJvciwgdW5kZWZpbmVkLCA0KX1gKTtcbiAgICAgICAgICAgIH1cbiAgICAgICAgICAgIGVsc2Uge1xuICAgICAgICAgICAgICAgIGxvZ2dlci5lcnJvcihgUmVjZWl2ZWQgcmVzcG9uc2UgbWVzc2FnZSB3aXRob3V0IGlkLiBObyBmdXJ0aGVyIGVycm9yIGluZm9ybWF0aW9uIHByb3ZpZGVkLmApO1xuICAgICAgICAgICAgfVxuICAgICAgICB9XG4gICAgICAgIGVsc2Uge1xuICAgICAgICAgICAgY29uc3Qga2V5ID0gcmVzcG9uc2VNZXNzYWdlLmlkO1xuICAgICAgICAgICAgY29uc3QgcmVzcG9uc2VQcm9taXNlID0gcmVzcG9uc2VQcm9taXNlcy5nZXQoa2V5KTtcbiAgICAgICAgICAgIHRyYWNlUmVjZWl2ZWRSZXNwb25zZShyZXNwb25zZU1lc3NhZ2UsIHJlc3BvbnNlUHJvbWlzZSk7XG4gICAgICAgICAgICBpZiAocmVzcG9uc2VQcm9taXNlICE9PSB1bmRlZmluZWQpIHtcbiAgICAgICAgICAgICAgICByZXNwb25zZVByb21pc2VzLmRlbGV0ZShrZXkpO1xuICAgICAgICAgICAgICAgIHRyeSB7XG4gICAgICAgICAgICAgICAgICAgIGlmIChyZXNwb25zZU1lc3NhZ2UuZXJyb3IpIHtcbiAgICAgICAgICAgICAgICAgICAgICAgIGNvbnN0IGVycm9yID0gcmVzcG9uc2VNZXNzYWdlLmVycm9yO1xuICAgICAgICAgICAgICAgICAgICAgICAgcmVzcG9uc2VQcm9taXNlLnJlamVjdChuZXcgbWVzc2FnZXNfMS5SZXNwb25zZUVycm9yKGVycm9yLmNvZGUsIGVycm9yLm1lc3NhZ2UsIGVycm9yLmRhdGEpKTtcbiAgICAgICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgICAgICBlbHNlIGlmIChyZXNwb25zZU1lc3NhZ2UucmVzdWx0ICE9PSB1bmRlZmluZWQpIHtcbiAgICAgICAgICAgICAgICAgICAgICAgIHJlc3BvbnNlUHJvbWlzZS5yZXNvbHZlKHJlc3BvbnNlTWVzc2FnZS5yZXN1bHQpO1xuICAgICAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgICAgIGVsc2Uge1xuICAgICAgICAgICAgICAgICAgICAgICAgdGhyb3cgbmV3IEVycm9yKCdTaG91bGQgbmV2ZXIgaGFwcGVuLicpO1xuICAgICAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgIGNhdGNoIChlcnJvcikge1xuICAgICAgICAgICAgICAgICAgICBpZiAoZXJyb3IubWVzc2FnZSkge1xuICAgICAgICAgICAgICAgICAgICAgICAgbG9nZ2VyLmVycm9yKGBSZXNwb25zZSBoYW5kbGVyICcke3Jlc3BvbnNlUHJvbWlzZS5tZXRob2R9JyBmYWlsZWQgd2l0aCBtZXNzYWdlOiAke2Vycm9yLm1lc3NhZ2V9YCk7XG4gICAgICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICAgICAgZWxzZSB7XG4gICAgICAgICAgICAgICAgICAgICAgICBsb2dnZXIuZXJyb3IoYFJlc3BvbnNlIGhhbmRsZXIgJyR7cmVzcG9uc2VQcm9taXNlLm1ldGhvZH0nIGZhaWxlZCB1bmV4cGVjdGVkbHkuYCk7XG4gICAgICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICB9XG4gICAgICAgIH1cbiAgICB9XG4gICAgZnVuY3Rpb24gaGFuZGxlTm90aWZpY2F0aW9uKG1lc3NhZ2UpIHtcbiAgICAgICAgaWYgKGlzRGlzcG9zZWQoKSkge1xuICAgICAgICAgICAgLy8gU2VlIGhhbmRsZSByZXF1ZXN0LlxuICAgICAgICAgICAgcmV0dXJuO1xuICAgICAgICB9XG4gICAgICAgIGxldCB0eXBlID0gdW5kZWZpbmVkO1xuICAgICAgICBsZXQgbm90aWZpY2F0aW9uSGFuZGxlcjtcbiAgICAgICAgaWYgKG1lc3NhZ2UubWV0aG9kID09PSBDYW5jZWxOb3RpZmljYXRpb24udHlwZS5tZXRob2QpIHtcbiAgICAgICAgICAgIGNvbnN0IGNhbmNlbElkID0gbWVzc2FnZS5wYXJhbXMuaWQ7XG4gICAgICAgICAgICBrbm93bkNhbmNlbGVkUmVxdWVzdHMuZGVsZXRlKGNhbmNlbElkKTtcbiAgICAgICAgICAgIHRyYWNlUmVjZWl2ZWROb3RpZmljYXRpb24obWVzc2FnZSk7XG4gICAgICAgICAgICByZXR1cm47XG4gICAgICAgIH1cbiAgICAgICAgZWxzZSB7XG4gICAgICAgICAgICBjb25zdCBlbGVtZW50ID0gbm90aWZpY2F0aW9uSGFuZGxlcnMuZ2V0KG1lc3NhZ2UubWV0aG9kKTtcbiAgICAgICAgICAgIGlmIChlbGVtZW50KSB7XG4gICAgICAgICAgICAgICAgbm90aWZpY2F0aW9uSGFuZGxlciA9IGVsZW1lbnQuaGFuZGxlcjtcbiAgICAgICAgICAgICAgICB0eXBlID0gZWxlbWVudC50eXBlO1xuICAgICAgICAgICAgfVxuICAgICAgICB9XG4gICAgICAgIGlmIChub3RpZmljYXRpb25IYW5kbGVyIHx8IHN0YXJOb3RpZmljYXRpb25IYW5kbGVyKSB7XG4gICAgICAgICAgICB0cnkge1xuICAgICAgICAgICAgICAgIHRyYWNlUmVjZWl2ZWROb3RpZmljYXRpb24obWVzc2FnZSk7XG4gICAgICAgICAgICAgICAgaWYgKG5vdGlmaWNhdGlvbkhhbmRsZXIpIHtcbiAgICAgICAgICAgICAgICAgICAgaWYgKG1lc3NhZ2UucGFyYW1zID09PSB1bmRlZmluZWQpIHtcbiAgICAgICAgICAgICAgICAgICAgICAgIGlmICh0eXBlICE9PSB1bmRlZmluZWQpIHtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICBpZiAodHlwZS5udW1iZXJPZlBhcmFtcyAhPT0gMCAmJiB0eXBlLnBhcmFtZXRlclN0cnVjdHVyZXMgIT09IG1lc3NhZ2VzXzEuUGFyYW1ldGVyU3RydWN0dXJlcy5ieU5hbWUpIHtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgbG9nZ2VyLmVycm9yKGBOb3RpZmljYXRpb24gJHttZXNzYWdlLm1ldGhvZH0gZGVmaW5lcyAke3R5cGUubnVtYmVyT2ZQYXJhbXN9IHBhcmFtcyBidXQgcmVjZWl2ZWQgbm9uZS5gKTtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgICAgICAgICBub3RpZmljYXRpb25IYW5kbGVyKCk7XG4gICAgICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICAgICAgZWxzZSBpZiAoQXJyYXkuaXNBcnJheShtZXNzYWdlLnBhcmFtcykpIHtcbiAgICAgICAgICAgICAgICAgICAgICAgIC8vIFRoZXJlIGFyZSBKU09OLVJQQyBsaWJyYXJpZXMgdGhhdCBzZW5kIHByb2dyZXNzIG1lc3NhZ2UgYXMgcG9zaXRpb25hbCBwYXJhbXMgYWx0aG91Z2hcbiAgICAgICAgICAgICAgICAgICAgICAgIC8vIHNwZWNpZmllZCBhcyBuYW1lZC4gU28gY29udmVydCB0aGVtIGlmIHRoaXMgaXMgdGhlIGNhc2UuXG4gICAgICAgICAgICAgICAgICAgICAgICBjb25zdCBwYXJhbXMgPSBtZXNzYWdlLnBhcmFtcztcbiAgICAgICAgICAgICAgICAgICAgICAgIGlmIChtZXNzYWdlLm1ldGhvZCA9PT0gUHJvZ3Jlc3NOb3RpZmljYXRpb24udHlwZS5tZXRob2QgJiYgcGFyYW1zLmxlbmd0aCA9PT0gMiAmJiBQcm9ncmVzc1Rva2VuLmlzKHBhcmFtc1swXSkpIHtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICBub3RpZmljYXRpb25IYW5kbGVyKHsgdG9rZW46IHBhcmFtc1swXSwgdmFsdWU6IHBhcmFtc1sxXSB9KTtcbiAgICAgICAgICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICAgICAgICAgIGVsc2Uge1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgIGlmICh0eXBlICE9PSB1bmRlZmluZWQpIHtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgaWYgKHR5cGUucGFyYW1ldGVyU3RydWN0dXJlcyA9PT0gbWVzc2FnZXNfMS5QYXJhbWV0ZXJTdHJ1Y3R1cmVzLmJ5TmFtZSkge1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgbG9nZ2VyLmVycm9yKGBOb3RpZmljYXRpb24gJHttZXNzYWdlLm1ldGhvZH0gZGVmaW5lcyBwYXJhbWV0ZXJzIGJ5IG5hbWUgYnV0IHJlY2VpdmVkIHBhcmFtZXRlcnMgYnkgcG9zaXRpb25gKTtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICBpZiAodHlwZS5udW1iZXJPZlBhcmFtcyAhPT0gbWVzc2FnZS5wYXJhbXMubGVuZ3RoKSB7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICBsb2dnZXIuZXJyb3IoYE5vdGlmaWNhdGlvbiAke21lc3NhZ2UubWV0aG9kfSBkZWZpbmVzICR7dHlwZS5udW1iZXJPZlBhcmFtc30gcGFyYW1zIGJ1dCByZWNlaXZlZCAke3BhcmFtcy5sZW5ndGh9IGFyZ3VtZW50c2ApO1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgICAgICAgICAgICAgIG5vdGlmaWNhdGlvbkhhbmRsZXIoLi4ucGFyYW1zKTtcbiAgICAgICAgICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgICAgICBlbHNlIHtcbiAgICAgICAgICAgICAgICAgICAgICAgIGlmICh0eXBlICE9PSB1bmRlZmluZWQgJiYgdHlwZS5wYXJhbWV0ZXJTdHJ1Y3R1cmVzID09PSBtZXNzYWdlc18xLlBhcmFtZXRlclN0cnVjdHVyZXMuYnlQb3NpdGlvbikge1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgIGxvZ2dlci5lcnJvcihgTm90aWZpY2F0aW9uICR7bWVzc2FnZS5tZXRob2R9IGRlZmluZXMgcGFyYW1ldGVycyBieSBwb3NpdGlvbiBidXQgcmVjZWl2ZWQgcGFyYW1ldGVycyBieSBuYW1lYCk7XG4gICAgICAgICAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgICAgICAgICBub3RpZmljYXRpb25IYW5kbGVyKG1lc3NhZ2UucGFyYW1zKTtcbiAgICAgICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICBlbHNlIGlmIChzdGFyTm90aWZpY2F0aW9uSGFuZGxlcikge1xuICAgICAgICAgICAgICAgICAgICBzdGFyTm90aWZpY2F0aW9uSGFuZGxlcihtZXNzYWdlLm1ldGhvZCwgbWVzc2FnZS5wYXJhbXMpO1xuICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgIH1cbiAgICAgICAgICAgIGNhdGNoIChlcnJvcikge1xuICAgICAgICAgICAgICAgIGlmIChlcnJvci5tZXNzYWdlKSB7XG4gICAgICAgICAgICAgICAgICAgIGxvZ2dlci5lcnJvcihgTm90aWZpY2F0aW9uIGhhbmRsZXIgJyR7bWVzc2FnZS5tZXRob2R9JyBmYWlsZWQgd2l0aCBtZXNzYWdlOiAke2Vycm9yLm1lc3NhZ2V9YCk7XG4gICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgIGVsc2Uge1xuICAgICAgICAgICAgICAgICAgICBsb2dnZXIuZXJyb3IoYE5vdGlmaWNhdGlvbiBoYW5kbGVyICcke21lc3NhZ2UubWV0aG9kfScgZmFpbGVkIHVuZXhwZWN0ZWRseS5gKTtcbiAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICB9XG4gICAgICAgIH1cbiAgICAgICAgZWxzZSB7XG4gICAgICAgICAgICB1bmhhbmRsZWROb3RpZmljYXRpb25FbWl0dGVyLmZpcmUobWVzc2FnZSk7XG4gICAgICAgIH1cbiAgICB9XG4gICAgZnVuY3Rpb24gaGFuZGxlSW52YWxpZE1lc3NhZ2UobWVzc2FnZSkge1xuICAgICAgICBpZiAoIW1lc3NhZ2UpIHtcbiAgICAgICAgICAgIGxvZ2dlci5lcnJvcignUmVjZWl2ZWQgZW1wdHkgbWVzc2FnZS4nKTtcbiAgICAgICAgICAgIHJldHVybjtcbiAgICAgICAgfVxuICAgICAgICBsb2dnZXIuZXJyb3IoYFJlY2VpdmVkIG1lc3NhZ2Ugd2hpY2ggaXMgbmVpdGhlciBhIHJlc3BvbnNlIG5vciBhIG5vdGlmaWNhdGlvbiBtZXNzYWdlOlxcbiR7SlNPTi5zdHJpbmdpZnkobWVzc2FnZSwgbnVsbCwgNCl9YCk7XG4gICAgICAgIC8vIFRlc3Qgd2hldGhlciB3ZSBmaW5kIGFuIGlkIHRvIHJlamVjdCB0aGUgcHJvbWlzZVxuICAgICAgICBjb25zdCByZXNwb25zZU1lc3NhZ2UgPSBtZXNzYWdlO1xuICAgICAgICBpZiAoSXMuc3RyaW5nKHJlc3BvbnNlTWVzc2FnZS5pZCkgfHwgSXMubnVtYmVyKHJlc3BvbnNlTWVzc2FnZS5pZCkpIHtcbiAgICAgICAgICAgIGNvbnN0IGtleSA9IHJlc3BvbnNlTWVzc2FnZS5pZDtcbiAgICAgICAgICAgIGNvbnN0IHJlc3BvbnNlSGFuZGxlciA9IHJlc3BvbnNlUHJvbWlzZXMuZ2V0KGtleSk7XG4gICAgICAgICAgICBpZiAocmVzcG9uc2VIYW5kbGVyKSB7XG4gICAgICAgICAgICAgICAgcmVzcG9uc2VIYW5kbGVyLnJlamVjdChuZXcgRXJyb3IoJ1RoZSByZWNlaXZlZCByZXNwb25zZSBoYXMgbmVpdGhlciBhIHJlc3VsdCBub3IgYW4gZXJyb3IgcHJvcGVydHkuJykpO1xuICAgICAgICAgICAgfVxuICAgICAgICB9XG4gICAgfVxuICAgIGZ1bmN0aW9uIHN0cmluZ2lmeVRyYWNlKHBhcmFtcykge1xuICAgICAgICBpZiAocGFyYW1zID09PSB1bmRlZmluZWQgfHwgcGFyYW1zID09PSBudWxsKSB7XG4gICAgICAgICAgICByZXR1cm4gdW5kZWZpbmVkO1xuICAgICAgICB9XG4gICAgICAgIHN3aXRjaCAodHJhY2UpIHtcbiAgICAgICAgICAgIGNhc2UgVHJhY2UuVmVyYm9zZTpcbiAgICAgICAgICAgICAgICByZXR1cm4gSlNPTi5zdHJpbmdpZnkocGFyYW1zLCBudWxsLCA0KTtcbiAgICAgICAgICAgIGNhc2UgVHJhY2UuQ29tcGFjdDpcbiAgICAgICAgICAgICAgICByZXR1cm4gSlNPTi5zdHJpbmdpZnkocGFyYW1zKTtcbiAgICAgICAgICAgIGRlZmF1bHQ6XG4gICAgICAgICAgICAgICAgcmV0dXJuIHVuZGVmaW5lZDtcbiAgICAgICAgfVxuICAgIH1cbiAgICBmdW5jdGlvbiB0cmFjZVNlbmRpbmdSZXF1ZXN0KG1lc3NhZ2UpIHtcbiAgICAgICAgaWYgKHRyYWNlID09PSBUcmFjZS5PZmYgfHwgIXRyYWNlcikge1xuICAgICAgICAgICAgcmV0dXJuO1xuICAgICAgICB9XG4gICAgICAgIGlmICh0cmFjZUZvcm1hdCA9PT0gVHJhY2VGb3JtYXQuVGV4dCkge1xuICAgICAgICAgICAgbGV0IGRhdGEgPSB1bmRlZmluZWQ7XG4gICAgICAgICAgICBpZiAoKHRyYWNlID09PSBUcmFjZS5WZXJib3NlIHx8IHRyYWNlID09PSBUcmFjZS5Db21wYWN0KSAmJiBtZXNzYWdlLnBhcmFtcykge1xuICAgICAgICAgICAgICAgIGRhdGEgPSBgUGFyYW1zOiAke3N0cmluZ2lmeVRyYWNlKG1lc3NhZ2UucGFyYW1zKX1cXG5cXG5gO1xuICAgICAgICAgICAgfVxuICAgICAgICAgICAgdHJhY2VyLmxvZyhgU2VuZGluZyByZXF1ZXN0ICcke21lc3NhZ2UubWV0aG9kfSAtICgke21lc3NhZ2UuaWR9KScuYCwgZGF0YSk7XG4gICAgICAgIH1cbiAgICAgICAgZWxzZSB7XG4gICAgICAgICAgICBsb2dMU1BNZXNzYWdlKCdzZW5kLXJlcXVlc3QnLCBtZXNzYWdlKTtcbiAgICAgICAgfVxuICAgIH1cbiAgICBmdW5jdGlvbiB0cmFjZVNlbmRpbmdOb3RpZmljYXRpb24obWVzc2FnZSkge1xuICAgICAgICBpZiAodHJhY2UgPT09IFRyYWNlLk9mZiB8fCAhdHJhY2VyKSB7XG4gICAgICAgICAgICByZXR1cm47XG4gICAgICAgIH1cbiAgICAgICAgaWYgKHRyYWNlRm9ybWF0ID09PSBUcmFjZUZvcm1hdC5UZXh0KSB7XG4gICAgICAgICAgICBsZXQgZGF0YSA9IHVuZGVmaW5lZDtcbiAgICAgICAgICAgIGlmICh0cmFjZSA9PT0gVHJhY2UuVmVyYm9zZSB8fCB0cmFjZSA9PT0gVHJhY2UuQ29tcGFjdCkge1xuICAgICAgICAgICAgICAgIGlmIChtZXNzYWdlLnBhcmFtcykge1xuICAgICAgICAgICAgICAgICAgICBkYXRhID0gYFBhcmFtczogJHtzdHJpbmdpZnlUcmFjZShtZXNzYWdlLnBhcmFtcyl9XFxuXFxuYDtcbiAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgZWxzZSB7XG4gICAgICAgICAgICAgICAgICAgIGRhdGEgPSAnTm8gcGFyYW1ldGVycyBwcm92aWRlZC5cXG5cXG4nO1xuICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgIH1cbiAgICAgICAgICAgIHRyYWNlci5sb2coYFNlbmRpbmcgbm90aWZpY2F0aW9uICcke21lc3NhZ2UubWV0aG9kfScuYCwgZGF0YSk7XG4gICAgICAgIH1cbiAgICAgICAgZWxzZSB7XG4gICAgICAgICAgICBsb2dMU1BNZXNzYWdlKCdzZW5kLW5vdGlmaWNhdGlvbicsIG1lc3NhZ2UpO1xuICAgICAgICB9XG4gICAgfVxuICAgIGZ1bmN0aW9uIHRyYWNlU2VuZGluZ1Jlc3BvbnNlKG1lc3NhZ2UsIG1ldGhvZCwgc3RhcnRUaW1lKSB7XG4gICAgICAgIGlmICh0cmFjZSA9PT0gVHJhY2UuT2ZmIHx8ICF0cmFjZXIpIHtcbiAgICAgICAgICAgIHJldHVybjtcbiAgICAgICAgfVxuICAgICAgICBpZiAodHJhY2VGb3JtYXQgPT09IFRyYWNlRm9ybWF0LlRleHQpIHtcbiAgICAgICAgICAgIGxldCBkYXRhID0gdW5kZWZpbmVkO1xuICAgICAgICAgICAgaWYgKHRyYWNlID09PSBUcmFjZS5WZXJib3NlIHx8IHRyYWNlID09PSBUcmFjZS5Db21wYWN0KSB7XG4gICAgICAgICAgICAgICAgaWYgKG1lc3NhZ2UuZXJyb3IgJiYgbWVzc2FnZS5lcnJvci5kYXRhKSB7XG4gICAgICAgICAgICAgICAgICAgIGRhdGEgPSBgRXJyb3IgZGF0YTogJHtzdHJpbmdpZnlUcmFjZShtZXNzYWdlLmVycm9yLmRhdGEpfVxcblxcbmA7XG4gICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgIGVsc2Uge1xuICAgICAgICAgICAgICAgICAgICBpZiAobWVzc2FnZS5yZXN1bHQpIHtcbiAgICAgICAgICAgICAgICAgICAgICAgIGRhdGEgPSBgUmVzdWx0OiAke3N0cmluZ2lmeVRyYWNlKG1lc3NhZ2UucmVzdWx0KX1cXG5cXG5gO1xuICAgICAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgICAgIGVsc2UgaWYgKG1lc3NhZ2UuZXJyb3IgPT09IHVuZGVmaW5lZCkge1xuICAgICAgICAgICAgICAgICAgICAgICAgZGF0YSA9ICdObyByZXN1bHQgcmV0dXJuZWQuXFxuXFxuJztcbiAgICAgICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgIH1cbiAgICAgICAgICAgIHRyYWNlci5sb2coYFNlbmRpbmcgcmVzcG9uc2UgJyR7bWV0aG9kfSAtICgke21lc3NhZ2UuaWR9KScuIFByb2Nlc3NpbmcgcmVxdWVzdCB0b29rICR7RGF0ZS5ub3coKSAtIHN0YXJ0VGltZX1tc2AsIGRhdGEpO1xuICAgICAgICB9XG4gICAgICAgIGVsc2Uge1xuICAgICAgICAgICAgbG9nTFNQTWVzc2FnZSgnc2VuZC1yZXNwb25zZScsIG1lc3NhZ2UpO1xuICAgICAgICB9XG4gICAgfVxuICAgIGZ1bmN0aW9uIHRyYWNlUmVjZWl2ZWRSZXF1ZXN0KG1lc3NhZ2UpIHtcbiAgICAgICAgaWYgKHRyYWNlID09PSBUcmFjZS5PZmYgfHwgIXRyYWNlcikge1xuICAgICAgICAgICAgcmV0dXJuO1xuICAgICAgICB9XG4gICAgICAgIGlmICh0cmFjZUZvcm1hdCA9PT0gVHJhY2VGb3JtYXQuVGV4dCkge1xuICAgICAgICAgICAgbGV0IGRhdGEgPSB1bmRlZmluZWQ7XG4gICAgICAgICAgICBpZiAoKHRyYWNlID09PSBUcmFjZS5WZXJib3NlIHx8IHRyYWNlID09PSBUcmFjZS5Db21wYWN0KSAmJiBtZXNzYWdlLnBhcmFtcykge1xuICAgICAgICAgICAgICAgIGRhdGEgPSBgUGFyYW1zOiAke3N0cmluZ2lmeVRyYWNlKG1lc3NhZ2UucGFyYW1zKX1cXG5cXG5gO1xuICAgICAgICAgICAgfVxuICAgICAgICAgICAgdHJhY2VyLmxvZyhgUmVjZWl2ZWQgcmVxdWVzdCAnJHttZXNzYWdlLm1ldGhvZH0gLSAoJHttZXNzYWdlLmlkfSknLmAsIGRhdGEpO1xuICAgICAgICB9XG4gICAgICAgIGVsc2Uge1xuICAgICAgICAgICAgbG9nTFNQTWVzc2FnZSgncmVjZWl2ZS1yZXF1ZXN0JywgbWVzc2FnZSk7XG4gICAgICAgIH1cbiAgICB9XG4gICAgZnVuY3Rpb24gdHJhY2VSZWNlaXZlZE5vdGlmaWNhdGlvbihtZXNzYWdlKSB7XG4gICAgICAgIGlmICh0cmFjZSA9PT0gVHJhY2UuT2ZmIHx8ICF0cmFjZXIgfHwgbWVzc2FnZS5tZXRob2QgPT09IExvZ1RyYWNlTm90aWZpY2F0aW9uLnR5cGUubWV0aG9kKSB7XG4gICAgICAgICAgICByZXR1cm47XG4gICAgICAgIH1cbiAgICAgICAgaWYgKHRyYWNlRm9ybWF0ID09PSBUcmFjZUZvcm1hdC5UZXh0KSB7XG4gICAgICAgICAgICBsZXQgZGF0YSA9IHVuZGVmaW5lZDtcbiAgICAgICAgICAgIGlmICh0cmFjZSA9PT0gVHJhY2UuVmVyYm9zZSB8fCB0cmFjZSA9PT0gVHJhY2UuQ29tcGFjdCkge1xuICAgICAgICAgICAgICAgIGlmIChtZXNzYWdlLnBhcmFtcykge1xuICAgICAgICAgICAgICAgICAgICBkYXRhID0gYFBhcmFtczogJHtzdHJpbmdpZnlUcmFjZShtZXNzYWdlLnBhcmFtcyl9XFxuXFxuYDtcbiAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgZWxzZSB7XG4gICAgICAgICAgICAgICAgICAgIGRhdGEgPSAnTm8gcGFyYW1ldGVycyBwcm92aWRlZC5cXG5cXG4nO1xuICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgIH1cbiAgICAgICAgICAgIHRyYWNlci5sb2coYFJlY2VpdmVkIG5vdGlmaWNhdGlvbiAnJHttZXNzYWdlLm1ldGhvZH0nLmAsIGRhdGEpO1xuICAgICAgICB9XG4gICAgICAgIGVsc2Uge1xuICAgICAgICAgICAgbG9nTFNQTWVzc2FnZSgncmVjZWl2ZS1ub3RpZmljYXRpb24nLCBtZXNzYWdlKTtcbiAgICAgICAgfVxuICAgIH1cbiAgICBmdW5jdGlvbiB0cmFjZVJlY2VpdmVkUmVzcG9uc2UobWVzc2FnZSwgcmVzcG9uc2VQcm9taXNlKSB7XG4gICAgICAgIGlmICh0cmFjZSA9PT0gVHJhY2UuT2ZmIHx8ICF0cmFjZXIpIHtcbiAgICAgICAgICAgIHJldHVybjtcbiAgICAgICAgfVxuICAgICAgICBpZiAodHJhY2VGb3JtYXQgPT09IFRyYWNlRm9ybWF0LlRleHQpIHtcbiAgICAgICAgICAgIGxldCBkYXRhID0gdW5kZWZpbmVkO1xuICAgICAgICAgICAgaWYgKHRyYWNlID09PSBUcmFjZS5WZXJib3NlIHx8IHRyYWNlID09PSBUcmFjZS5Db21wYWN0KSB7XG4gICAgICAgICAgICAgICAgaWYgKG1lc3NhZ2UuZXJyb3IgJiYgbWVzc2FnZS5lcnJvci5kYXRhKSB7XG4gICAgICAgICAgICAgICAgICAgIGRhdGEgPSBgRXJyb3IgZGF0YTogJHtzdHJpbmdpZnlUcmFjZShtZXNzYWdlLmVycm9yLmRhdGEpfVxcblxcbmA7XG4gICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgIGVsc2Uge1xuICAgICAgICAgICAgICAgICAgICBpZiAobWVzc2FnZS5yZXN1bHQpIHtcbiAgICAgICAgICAgICAgICAgICAgICAgIGRhdGEgPSBgUmVzdWx0OiAke3N0cmluZ2lmeVRyYWNlKG1lc3NhZ2UucmVzdWx0KX1cXG5cXG5gO1xuICAgICAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgICAgIGVsc2UgaWYgKG1lc3NhZ2UuZXJyb3IgPT09IHVuZGVmaW5lZCkge1xuICAgICAgICAgICAgICAgICAgICAgICAgZGF0YSA9ICdObyByZXN1bHQgcmV0dXJuZWQuXFxuXFxuJztcbiAgICAgICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgIH1cbiAgICAgICAgICAgIGlmIChyZXNwb25zZVByb21pc2UpIHtcbiAgICAgICAgICAgICAgICBjb25zdCBlcnJvciA9IG1lc3NhZ2UuZXJyb3IgPyBgIFJlcXVlc3QgZmFpbGVkOiAke21lc3NhZ2UuZXJyb3IubWVzc2FnZX0gKCR7bWVzc2FnZS5lcnJvci5jb2RlfSkuYCA6ICcnO1xuICAgICAgICAgICAgICAgIHRyYWNlci5sb2coYFJlY2VpdmVkIHJlc3BvbnNlICcke3Jlc3BvbnNlUHJvbWlzZS5tZXRob2R9IC0gKCR7bWVzc2FnZS5pZH0pJyBpbiAke0RhdGUubm93KCkgLSByZXNwb25zZVByb21pc2UudGltZXJTdGFydH1tcy4ke2Vycm9yfWAsIGRhdGEpO1xuICAgICAgICAgICAgfVxuICAgICAgICAgICAgZWxzZSB7XG4gICAgICAgICAgICAgICAgdHJhY2VyLmxvZyhgUmVjZWl2ZWQgcmVzcG9uc2UgJHttZXNzYWdlLmlkfSB3aXRob3V0IGFjdGl2ZSByZXNwb25zZSBwcm9taXNlLmAsIGRhdGEpO1xuICAgICAgICAgICAgfVxuICAgICAgICB9XG4gICAgICAgIGVsc2Uge1xuICAgICAgICAgICAgbG9nTFNQTWVzc2FnZSgncmVjZWl2ZS1yZXNwb25zZScsIG1lc3NhZ2UpO1xuICAgICAgICB9XG4gICAgfVxuICAgIGZ1bmN0aW9uIGxvZ0xTUE1lc3NhZ2UodHlwZSwgbWVzc2FnZSkge1xuICAgICAgICBpZiAoIXRyYWNlciB8fCB0cmFjZSA9PT0gVHJhY2UuT2ZmKSB7XG4gICAgICAgICAgICByZXR1cm47XG4gICAgICAgIH1cbiAgICAgICAgY29uc3QgbHNwTWVzc2FnZSA9IHtcbiAgICAgICAgICAgIGlzTFNQTWVzc2FnZTogdHJ1ZSxcbiAgICAgICAgICAgIHR5cGUsXG4gICAgICAgICAgICBtZXNzYWdlLFxuICAgICAgICAgICAgdGltZXN0YW1wOiBEYXRlLm5vdygpXG4gICAgICAgIH07XG4gICAgICAgIHRyYWNlci5sb2cobHNwTWVzc2FnZSk7XG4gICAgfVxuICAgIGZ1bmN0aW9uIHRocm93SWZDbG9zZWRPckRpc3Bvc2VkKCkge1xuICAgICAgICBpZiAoaXNDbG9zZWQoKSkge1xuICAgICAgICAgICAgdGhyb3cgbmV3IENvbm5lY3Rpb25FcnJvcihDb25uZWN0aW9uRXJyb3JzLkNsb3NlZCwgJ0Nvbm5lY3Rpb24gaXMgY2xvc2VkLicpO1xuICAgICAgICB9XG4gICAgICAgIGlmIChpc0Rpc3Bvc2VkKCkpIHtcbiAgICAgICAgICAgIHRocm93IG5ldyBDb25uZWN0aW9uRXJyb3IoQ29ubmVjdGlvbkVycm9ycy5EaXNwb3NlZCwgJ0Nvbm5lY3Rpb24gaXMgZGlzcG9zZWQuJyk7XG4gICAgICAgIH1cbiAgICB9XG4gICAgZnVuY3Rpb24gdGhyb3dJZkxpc3RlbmluZygpIHtcbiAgICAgICAgaWYgKGlzTGlzdGVuaW5nKCkpIHtcbiAgICAgICAgICAgIHRocm93IG5ldyBDb25uZWN0aW9uRXJyb3IoQ29ubmVjdGlvbkVycm9ycy5BbHJlYWR5TGlzdGVuaW5nLCAnQ29ubmVjdGlvbiBpcyBhbHJlYWR5IGxpc3RlbmluZycpO1xuICAgICAgICB9XG4gICAgfVxuICAgIGZ1bmN0aW9uIHRocm93SWZOb3RMaXN0ZW5pbmcoKSB7XG4gICAgICAgIGlmICghaXNMaXN0ZW5pbmcoKSkge1xuICAgICAgICAgICAgdGhyb3cgbmV3IEVycm9yKCdDYWxsIGxpc3RlbigpIGZpcnN0LicpO1xuICAgICAgICB9XG4gICAgfVxuICAgIGZ1bmN0aW9uIHVuZGVmaW5lZFRvTnVsbChwYXJhbSkge1xuICAgICAgICBpZiAocGFyYW0gPT09IHVuZGVmaW5lZCkge1xuICAgICAgICAgICAgcmV0dXJuIG51bGw7XG4gICAgICAgIH1cbiAgICAgICAgZWxzZSB7XG4gICAgICAgICAgICByZXR1cm4gcGFyYW07XG4gICAgICAgIH1cbiAgICB9XG4gICAgZnVuY3Rpb24gbnVsbFRvVW5kZWZpbmVkKHBhcmFtKSB7XG4gICAgICAgIGlmIChwYXJhbSA9PT0gbnVsbCkge1xuICAgICAgICAgICAgcmV0dXJuIHVuZGVmaW5lZDtcbiAgICAgICAgfVxuICAgICAgICBlbHNlIHtcbiAgICAgICAgICAgIHJldHVybiBwYXJhbTtcbiAgICAgICAgfVxuICAgIH1cbiAgICBmdW5jdGlvbiBpc05hbWVkUGFyYW0ocGFyYW0pIHtcbiAgICAgICAgcmV0dXJuIHBhcmFtICE9PSB1bmRlZmluZWQgJiYgcGFyYW0gIT09IG51bGwgJiYgIUFycmF5LmlzQXJyYXkocGFyYW0pICYmIHR5cGVvZiBwYXJhbSA9PT0gJ29iamVjdCc7XG4gICAgfVxuICAgIGZ1bmN0aW9uIGNvbXB1dGVTaW5nbGVQYXJhbShwYXJhbWV0ZXJTdHJ1Y3R1cmVzLCBwYXJhbSkge1xuICAgICAgICBzd2l0Y2ggKHBhcmFtZXRlclN0cnVjdHVyZXMpIHtcbiAgICAgICAgICAgIGNhc2UgbWVzc2FnZXNfMS5QYXJhbWV0ZXJTdHJ1Y3R1cmVzLmF1dG86XG4gICAgICAgICAgICAgICAgaWYgKGlzTmFtZWRQYXJhbShwYXJhbSkpIHtcbiAgICAgICAgICAgICAgICAgICAgcmV0dXJuIG51bGxUb1VuZGVmaW5lZChwYXJhbSk7XG4gICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgIGVsc2Uge1xuICAgICAgICAgICAgICAgICAgICByZXR1cm4gW3VuZGVmaW5lZFRvTnVsbChwYXJhbSldO1xuICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgIGNhc2UgbWVzc2FnZXNfMS5QYXJhbWV0ZXJTdHJ1Y3R1cmVzLmJ5TmFtZTpcbiAgICAgICAgICAgICAgICBpZiAoIWlzTmFtZWRQYXJhbShwYXJhbSkpIHtcbiAgICAgICAgICAgICAgICAgICAgdGhyb3cgbmV3IEVycm9yKGBSZWNlaXZlZCBwYXJhbWV0ZXJzIGJ5IG5hbWUgYnV0IHBhcmFtIGlzIG5vdCBhbiBvYmplY3QgbGl0ZXJhbC5gKTtcbiAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgcmV0dXJuIG51bGxUb1VuZGVmaW5lZChwYXJhbSk7XG4gICAgICAgICAgICBjYXNlIG1lc3NhZ2VzXzEuUGFyYW1ldGVyU3RydWN0dXJlcy5ieVBvc2l0aW9uOlxuICAgICAgICAgICAgICAgIHJldHVybiBbdW5kZWZpbmVkVG9OdWxsKHBhcmFtKV07XG4gICAgICAgICAgICBkZWZhdWx0OlxuICAgICAgICAgICAgICAgIHRocm93IG5ldyBFcnJvcihgVW5rbm93biBwYXJhbWV0ZXIgc3RydWN0dXJlICR7cGFyYW1ldGVyU3RydWN0dXJlcy50b1N0cmluZygpfWApO1xuICAgICAgICB9XG4gICAgfVxuICAgIGZ1bmN0aW9uIGNvbXB1dGVNZXNzYWdlUGFyYW1zKHR5cGUsIHBhcmFtcykge1xuICAgICAgICBsZXQgcmVzdWx0O1xuICAgICAgICBjb25zdCBudW1iZXJPZlBhcmFtcyA9IHR5cGUubnVtYmVyT2ZQYXJhbXM7XG4gICAgICAgIHN3aXRjaCAobnVtYmVyT2ZQYXJhbXMpIHtcbiAgICAgICAgICAgIGNhc2UgMDpcbiAgICAgICAgICAgICAgICByZXN1bHQgPSB1bmRlZmluZWQ7XG4gICAgICAgICAgICAgICAgYnJlYWs7XG4gICAgICAgICAgICBjYXNlIDE6XG4gICAgICAgICAgICAgICAgcmVzdWx0ID0gY29tcHV0ZVNpbmdsZVBhcmFtKHR5cGUucGFyYW1ldGVyU3RydWN0dXJlcywgcGFyYW1zWzBdKTtcbiAgICAgICAgICAgICAgICBicmVhaztcbiAgICAgICAgICAgIGRlZmF1bHQ6XG4gICAgICAgICAgICAgICAgcmVzdWx0ID0gW107XG4gICAgICAgICAgICAgICAgZm9yIChsZXQgaSA9IDA7IGkgPCBwYXJhbXMubGVuZ3RoICYmIGkgPCBudW1iZXJPZlBhcmFtczsgaSsrKSB7XG4gICAgICAgICAgICAgICAgICAgIHJlc3VsdC5wdXNoKHVuZGVmaW5lZFRvTnVsbChwYXJhbXNbaV0pKTtcbiAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgaWYgKHBhcmFtcy5sZW5ndGggPCBudW1iZXJPZlBhcmFtcykge1xuICAgICAgICAgICAgICAgICAgICBmb3IgKGxldCBpID0gcGFyYW1zLmxlbmd0aDsgaSA8IG51bWJlck9mUGFyYW1zOyBpKyspIHtcbiAgICAgICAgICAgICAgICAgICAgICAgIHJlc3VsdC5wdXNoKG51bGwpO1xuICAgICAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgIGJyZWFrO1xuICAgICAgICB9XG4gICAgICAgIHJldHVybiByZXN1bHQ7XG4gICAgfVxuICAgIGNvbnN0IGNvbm5lY3Rpb24gPSB7XG4gICAgICAgIHNlbmROb3RpZmljYXRpb246ICh0eXBlLCAuLi5hcmdzKSA9PiB7XG4gICAgICAgICAgICB0aHJvd0lmQ2xvc2VkT3JEaXNwb3NlZCgpO1xuICAgICAgICAgICAgbGV0IG1ldGhvZDtcbiAgICAgICAgICAgIGxldCBtZXNzYWdlUGFyYW1zO1xuICAgICAgICAgICAgaWYgKElzLnN0cmluZyh0eXBlKSkge1xuICAgICAgICAgICAgICAgIG1ldGhvZCA9IHR5cGU7XG4gICAgICAgICAgICAgICAgY29uc3QgZmlyc3QgPSBhcmdzWzBdO1xuICAgICAgICAgICAgICAgIGxldCBwYXJhbVN0YXJ0ID0gMDtcbiAgICAgICAgICAgICAgICBsZXQgcGFyYW1ldGVyU3RydWN0dXJlcyA9IG1lc3NhZ2VzXzEuUGFyYW1ldGVyU3RydWN0dXJlcy5hdXRvO1xuICAgICAgICAgICAgICAgIGlmIChtZXNzYWdlc18xLlBhcmFtZXRlclN0cnVjdHVyZXMuaXMoZmlyc3QpKSB7XG4gICAgICAgICAgICAgICAgICAgIHBhcmFtU3RhcnQgPSAxO1xuICAgICAgICAgICAgICAgICAgICBwYXJhbWV0ZXJTdHJ1Y3R1cmVzID0gZmlyc3Q7XG4gICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgIGxldCBwYXJhbUVuZCA9IGFyZ3MubGVuZ3RoO1xuICAgICAgICAgICAgICAgIGNvbnN0IG51bWJlck9mUGFyYW1zID0gcGFyYW1FbmQgLSBwYXJhbVN0YXJ0O1xuICAgICAgICAgICAgICAgIHN3aXRjaCAobnVtYmVyT2ZQYXJhbXMpIHtcbiAgICAgICAgICAgICAgICAgICAgY2FzZSAwOlxuICAgICAgICAgICAgICAgICAgICAgICAgbWVzc2FnZVBhcmFtcyA9IHVuZGVmaW5lZDtcbiAgICAgICAgICAgICAgICAgICAgICAgIGJyZWFrO1xuICAgICAgICAgICAgICAgICAgICBjYXNlIDE6XG4gICAgICAgICAgICAgICAgICAgICAgICBtZXNzYWdlUGFyYW1zID0gY29tcHV0ZVNpbmdsZVBhcmFtKHBhcmFtZXRlclN0cnVjdHVyZXMsIGFyZ3NbcGFyYW1TdGFydF0pO1xuICAgICAgICAgICAgICAgICAgICAgICAgYnJlYWs7XG4gICAgICAgICAgICAgICAgICAgIGRlZmF1bHQ6XG4gICAgICAgICAgICAgICAgICAgICAgICBpZiAocGFyYW1ldGVyU3RydWN0dXJlcyA9PT0gbWVzc2FnZXNfMS5QYXJhbWV0ZXJTdHJ1Y3R1cmVzLmJ5TmFtZSkge1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgIHRocm93IG5ldyBFcnJvcihgUmVjZWl2ZWQgJHtudW1iZXJPZlBhcmFtc30gcGFyYW1ldGVycyBmb3IgJ2J5IE5hbWUnIG5vdGlmaWNhdGlvbiBwYXJhbWV0ZXIgc3RydWN0dXJlLmApO1xuICAgICAgICAgICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgICAgICAgICAgbWVzc2FnZVBhcmFtcyA9IGFyZ3Muc2xpY2UocGFyYW1TdGFydCwgcGFyYW1FbmQpLm1hcCh2YWx1ZSA9PiB1bmRlZmluZWRUb051bGwodmFsdWUpKTtcbiAgICAgICAgICAgICAgICAgICAgICAgIGJyZWFrO1xuICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgIH1cbiAgICAgICAgICAgIGVsc2Uge1xuICAgICAgICAgICAgICAgIGNvbnN0IHBhcmFtcyA9IGFyZ3M7XG4gICAgICAgICAgICAgICAgbWV0aG9kID0gdHlwZS5tZXRob2Q7XG4gICAgICAgICAgICAgICAgbWVzc2FnZVBhcmFtcyA9IGNvbXB1dGVNZXNzYWdlUGFyYW1zKHR5cGUsIHBhcmFtcyk7XG4gICAgICAgICAgICB9XG4gICAgICAgICAgICBjb25zdCBub3RpZmljYXRpb25NZXNzYWdlID0ge1xuICAgICAgICAgICAgICAgIGpzb25ycGM6IHZlcnNpb24sXG4gICAgICAgICAgICAgICAgbWV0aG9kOiBtZXRob2QsXG4gICAgICAgICAgICAgICAgcGFyYW1zOiBtZXNzYWdlUGFyYW1zXG4gICAgICAgICAgICB9O1xuICAgICAgICAgICAgdHJhY2VTZW5kaW5nTm90aWZpY2F0aW9uKG5vdGlmaWNhdGlvbk1lc3NhZ2UpO1xuICAgICAgICAgICAgcmV0dXJuIG1lc3NhZ2VXcml0ZXIud3JpdGUobm90aWZpY2F0aW9uTWVzc2FnZSkuY2F0Y2goKGVycm9yKSA9PiB7XG4gICAgICAgICAgICAgICAgbG9nZ2VyLmVycm9yKGBTZW5kaW5nIG5vdGlmaWNhdGlvbiBmYWlsZWQuYCk7XG4gICAgICAgICAgICAgICAgdGhyb3cgZXJyb3I7XG4gICAgICAgICAgICB9KTtcbiAgICAgICAgfSxcbiAgICAgICAgb25Ob3RpZmljYXRpb246ICh0eXBlLCBoYW5kbGVyKSA9PiB7XG4gICAgICAgICAgICB0aHJvd0lmQ2xvc2VkT3JEaXNwb3NlZCgpO1xuICAgICAgICAgICAgbGV0IG1ldGhvZDtcbiAgICAgICAgICAgIGlmIChJcy5mdW5jKHR5cGUpKSB7XG4gICAgICAgICAgICAgICAgc3Rhck5vdGlmaWNhdGlvbkhhbmRsZXIgPSB0eXBlO1xuICAgICAgICAgICAgfVxuICAgICAgICAgICAgZWxzZSBpZiAoaGFuZGxlcikge1xuICAgICAgICAgICAgICAgIGlmIChJcy5zdHJpbmcodHlwZSkpIHtcbiAgICAgICAgICAgICAgICAgICAgbWV0aG9kID0gdHlwZTtcbiAgICAgICAgICAgICAgICAgICAgbm90aWZpY2F0aW9uSGFuZGxlcnMuc2V0KHR5cGUsIHsgdHlwZTogdW5kZWZpbmVkLCBoYW5kbGVyIH0pO1xuICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICBlbHNlIHtcbiAgICAgICAgICAgICAgICAgICAgbWV0aG9kID0gdHlwZS5tZXRob2Q7XG4gICAgICAgICAgICAgICAgICAgIG5vdGlmaWNhdGlvbkhhbmRsZXJzLnNldCh0eXBlLm1ldGhvZCwgeyB0eXBlLCBoYW5kbGVyIH0pO1xuICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgIH1cbiAgICAgICAgICAgIHJldHVybiB7XG4gICAgICAgICAgICAgICAgZGlzcG9zZTogKCkgPT4ge1xuICAgICAgICAgICAgICAgICAgICBpZiAobWV0aG9kICE9PSB1bmRlZmluZWQpIHtcbiAgICAgICAgICAgICAgICAgICAgICAgIG5vdGlmaWNhdGlvbkhhbmRsZXJzLmRlbGV0ZShtZXRob2QpO1xuICAgICAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgICAgIGVsc2Uge1xuICAgICAgICAgICAgICAgICAgICAgICAgc3Rhck5vdGlmaWNhdGlvbkhhbmRsZXIgPSB1bmRlZmluZWQ7XG4gICAgICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICB9O1xuICAgICAgICB9LFxuICAgICAgICBvblByb2dyZXNzOiAoX3R5cGUsIHRva2VuLCBoYW5kbGVyKSA9PiB7XG4gICAgICAgICAgICBpZiAocHJvZ3Jlc3NIYW5kbGVycy5oYXModG9rZW4pKSB7XG4gICAgICAgICAgICAgICAgdGhyb3cgbmV3IEVycm9yKGBQcm9ncmVzcyBoYW5kbGVyIGZvciB0b2tlbiAke3Rva2VufSBhbHJlYWR5IHJlZ2lzdGVyZWRgKTtcbiAgICAgICAgICAgIH1cbiAgICAgICAgICAgIHByb2dyZXNzSGFuZGxlcnMuc2V0KHRva2VuLCBoYW5kbGVyKTtcbiAgICAgICAgICAgIHJldHVybiB7XG4gICAgICAgICAgICAgICAgZGlzcG9zZTogKCkgPT4ge1xuICAgICAgICAgICAgICAgICAgICBwcm9ncmVzc0hhbmRsZXJzLmRlbGV0ZSh0b2tlbik7XG4gICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgfTtcbiAgICAgICAgfSxcbiAgICAgICAgc2VuZFByb2dyZXNzOiAoX3R5cGUsIHRva2VuLCB2YWx1ZSkgPT4ge1xuICAgICAgICAgICAgLy8gVGhpcyBzaG91bGQgbm90IGF3YWl0IGJ1dCBzaW1wbGUgcmV0dXJuIHRvIGVuc3VyZSB0aGF0IHdlIGRvbid0IGhhdmUgYW5vdGhlclxuICAgICAgICAgICAgLy8gYXN5bmMgc2NoZWR1bGluZy4gT3RoZXJ3aXNlIG9uZSBzZW5kIGNvdWxkIG92ZXJ0YWtlIGFub3RoZXIgc2VuZC5cbiAgICAgICAgICAgIHJldHVybiBjb25uZWN0aW9uLnNlbmROb3RpZmljYXRpb24oUHJvZ3Jlc3NOb3RpZmljYXRpb24udHlwZSwgeyB0b2tlbiwgdmFsdWUgfSk7XG4gICAgICAgIH0sXG4gICAgICAgIG9uVW5oYW5kbGVkUHJvZ3Jlc3M6IHVuaGFuZGxlZFByb2dyZXNzRW1pdHRlci5ldmVudCxcbiAgICAgICAgc2VuZFJlcXVlc3Q6ICh0eXBlLCAuLi5hcmdzKSA9PiB7XG4gICAgICAgICAgICB0aHJvd0lmQ2xvc2VkT3JEaXNwb3NlZCgpO1xuICAgICAgICAgICAgdGhyb3dJZk5vdExpc3RlbmluZygpO1xuICAgICAgICAgICAgbGV0IG1ldGhvZDtcbiAgICAgICAgICAgIGxldCBtZXNzYWdlUGFyYW1zO1xuICAgICAgICAgICAgbGV0IHRva2VuID0gdW5kZWZpbmVkO1xuICAgICAgICAgICAgaWYgKElzLnN0cmluZyh0eXBlKSkge1xuICAgICAgICAgICAgICAgIG1ldGhvZCA9IHR5cGU7XG4gICAgICAgICAgICAgICAgY29uc3QgZmlyc3QgPSBhcmdzWzBdO1xuICAgICAgICAgICAgICAgIGNvbnN0IGxhc3QgPSBhcmdzW2FyZ3MubGVuZ3RoIC0gMV07XG4gICAgICAgICAgICAgICAgbGV0IHBhcmFtU3RhcnQgPSAwO1xuICAgICAgICAgICAgICAgIGxldCBwYXJhbWV0ZXJTdHJ1Y3R1cmVzID0gbWVzc2FnZXNfMS5QYXJhbWV0ZXJTdHJ1Y3R1cmVzLmF1dG87XG4gICAgICAgICAgICAgICAgaWYgKG1lc3NhZ2VzXzEuUGFyYW1ldGVyU3RydWN0dXJlcy5pcyhmaXJzdCkpIHtcbiAgICAgICAgICAgICAgICAgICAgcGFyYW1TdGFydCA9IDE7XG4gICAgICAgICAgICAgICAgICAgIHBhcmFtZXRlclN0cnVjdHVyZXMgPSBmaXJzdDtcbiAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgbGV0IHBhcmFtRW5kID0gYXJncy5sZW5ndGg7XG4gICAgICAgICAgICAgICAgaWYgKGNhbmNlbGxhdGlvbl8xLkNhbmNlbGxhdGlvblRva2VuLmlzKGxhc3QpKSB7XG4gICAgICAgICAgICAgICAgICAgIHBhcmFtRW5kID0gcGFyYW1FbmQgLSAxO1xuICAgICAgICAgICAgICAgICAgICB0b2tlbiA9IGxhc3Q7XG4gICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgIGNvbnN0IG51bWJlck9mUGFyYW1zID0gcGFyYW1FbmQgLSBwYXJhbVN0YXJ0O1xuICAgICAgICAgICAgICAgIHN3aXRjaCAobnVtYmVyT2ZQYXJhbXMpIHtcbiAgICAgICAgICAgICAgICAgICAgY2FzZSAwOlxuICAgICAgICAgICAgICAgICAgICAgICAgbWVzc2FnZVBhcmFtcyA9IHVuZGVmaW5lZDtcbiAgICAgICAgICAgICAgICAgICAgICAgIGJyZWFrO1xuICAgICAgICAgICAgICAgICAgICBjYXNlIDE6XG4gICAgICAgICAgICAgICAgICAgICAgICBtZXNzYWdlUGFyYW1zID0gY29tcHV0ZVNpbmdsZVBhcmFtKHBhcmFtZXRlclN0cnVjdHVyZXMsIGFyZ3NbcGFyYW1TdGFydF0pO1xuICAgICAgICAgICAgICAgICAgICAgICAgYnJlYWs7XG4gICAgICAgICAgICAgICAgICAgIGRlZmF1bHQ6XG4gICAgICAgICAgICAgICAgICAgICAgICBpZiAocGFyYW1ldGVyU3RydWN0dXJlcyA9PT0gbWVzc2FnZXNfMS5QYXJhbWV0ZXJTdHJ1Y3R1cmVzLmJ5TmFtZSkge1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgIHRocm93IG5ldyBFcnJvcihgUmVjZWl2ZWQgJHtudW1iZXJPZlBhcmFtc30gcGFyYW1ldGVycyBmb3IgJ2J5IE5hbWUnIHJlcXVlc3QgcGFyYW1ldGVyIHN0cnVjdHVyZS5gKTtcbiAgICAgICAgICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICAgICAgICAgIG1lc3NhZ2VQYXJhbXMgPSBhcmdzLnNsaWNlKHBhcmFtU3RhcnQsIHBhcmFtRW5kKS5tYXAodmFsdWUgPT4gdW5kZWZpbmVkVG9OdWxsKHZhbHVlKSk7XG4gICAgICAgICAgICAgICAgICAgICAgICBicmVhaztcbiAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICB9XG4gICAgICAgICAgICBlbHNlIHtcbiAgICAgICAgICAgICAgICBjb25zdCBwYXJhbXMgPSBhcmdzO1xuICAgICAgICAgICAgICAgIG1ldGhvZCA9IHR5cGUubWV0aG9kO1xuICAgICAgICAgICAgICAgIG1lc3NhZ2VQYXJhbXMgPSBjb21wdXRlTWVzc2FnZVBhcmFtcyh0eXBlLCBwYXJhbXMpO1xuICAgICAgICAgICAgICAgIGNvbnN0IG51bWJlck9mUGFyYW1zID0gdHlwZS5udW1iZXJPZlBhcmFtcztcbiAgICAgICAgICAgICAgICB0b2tlbiA9IGNhbmNlbGxhdGlvbl8xLkNhbmNlbGxhdGlvblRva2VuLmlzKHBhcmFtc1tudW1iZXJPZlBhcmFtc10pID8gcGFyYW1zW251bWJlck9mUGFyYW1zXSA6IHVuZGVmaW5lZDtcbiAgICAgICAgICAgIH1cbiAgICAgICAgICAgIGNvbnN0IGlkID0gc2VxdWVuY2VOdW1iZXIrKztcbiAgICAgICAgICAgIGxldCBkaXNwb3NhYmxlO1xuICAgICAgICAgICAgaWYgKHRva2VuKSB7XG4gICAgICAgICAgICAgICAgZGlzcG9zYWJsZSA9IHRva2VuLm9uQ2FuY2VsbGF0aW9uUmVxdWVzdGVkKCgpID0+IHtcbiAgICAgICAgICAgICAgICAgICAgY29uc3QgcCA9IGNhbmNlbGxhdGlvblN0cmF0ZWd5LnNlbmRlci5zZW5kQ2FuY2VsbGF0aW9uKGNvbm5lY3Rpb24sIGlkKTtcbiAgICAgICAgICAgICAgICAgICAgaWYgKHAgPT09IHVuZGVmaW5lZCkge1xuICAgICAgICAgICAgICAgICAgICAgICAgbG9nZ2VyLmxvZyhgUmVjZWl2ZWQgbm8gcHJvbWlzZSBmcm9tIGNhbmNlbGxhdGlvbiBzdHJhdGVneSB3aGVuIGNhbmNlbGxpbmcgaWQgJHtpZH1gKTtcbiAgICAgICAgICAgICAgICAgICAgICAgIHJldHVybiBQcm9taXNlLnJlc29sdmUoKTtcbiAgICAgICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgICAgICBlbHNlIHtcbiAgICAgICAgICAgICAgICAgICAgICAgIHJldHVybiBwLmNhdGNoKCgpID0+IHtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICBsb2dnZXIubG9nKGBTZW5kaW5nIGNhbmNlbGxhdGlvbiBtZXNzYWdlcyBmb3IgaWQgJHtpZH0gZmFpbGVkYCk7XG4gICAgICAgICAgICAgICAgICAgICAgICB9KTtcbiAgICAgICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgIH0pO1xuICAgICAgICAgICAgfVxuICAgICAgICAgICAgY29uc3QgcmVxdWVzdE1lc3NhZ2UgPSB7XG4gICAgICAgICAgICAgICAganNvbnJwYzogdmVyc2lvbixcbiAgICAgICAgICAgICAgICBpZDogaWQsXG4gICAgICAgICAgICAgICAgbWV0aG9kOiBtZXRob2QsXG4gICAgICAgICAgICAgICAgcGFyYW1zOiBtZXNzYWdlUGFyYW1zXG4gICAgICAgICAgICB9O1xuICAgICAgICAgICAgdHJhY2VTZW5kaW5nUmVxdWVzdChyZXF1ZXN0TWVzc2FnZSk7XG4gICAgICAgICAgICBpZiAodHlwZW9mIGNhbmNlbGxhdGlvblN0cmF0ZWd5LnNlbmRlci5lbmFibGVDYW5jZWxsYXRpb24gPT09ICdmdW5jdGlvbicpIHtcbiAgICAgICAgICAgICAgICBjYW5jZWxsYXRpb25TdHJhdGVneS5zZW5kZXIuZW5hYmxlQ2FuY2VsbGF0aW9uKHJlcXVlc3RNZXNzYWdlKTtcbiAgICAgICAgICAgIH1cbiAgICAgICAgICAgIHJldHVybiBuZXcgUHJvbWlzZShhc3luYyAocmVzb2x2ZSwgcmVqZWN0KSA9PiB7XG4gICAgICAgICAgICAgICAgY29uc3QgcmVzb2x2ZVdpdGhDbGVhbnVwID0gKHIpID0+IHtcbiAgICAgICAgICAgICAgICAgICAgcmVzb2x2ZShyKTtcbiAgICAgICAgICAgICAgICAgICAgY2FuY2VsbGF0aW9uU3RyYXRlZ3kuc2VuZGVyLmNsZWFudXAoaWQpO1xuICAgICAgICAgICAgICAgICAgICBkaXNwb3NhYmxlPy5kaXNwb3NlKCk7XG4gICAgICAgICAgICAgICAgfTtcbiAgICAgICAgICAgICAgICBjb25zdCByZWplY3RXaXRoQ2xlYW51cCA9IChyKSA9PiB7XG4gICAgICAgICAgICAgICAgICAgIHJlamVjdChyKTtcbiAgICAgICAgICAgICAgICAgICAgY2FuY2VsbGF0aW9uU3RyYXRlZ3kuc2VuZGVyLmNsZWFudXAoaWQpO1xuICAgICAgICAgICAgICAgICAgICBkaXNwb3NhYmxlPy5kaXNwb3NlKCk7XG4gICAgICAgICAgICAgICAgfTtcbiAgICAgICAgICAgICAgICBjb25zdCByZXNwb25zZVByb21pc2UgPSB7IG1ldGhvZDogbWV0aG9kLCB0aW1lclN0YXJ0OiBEYXRlLm5vdygpLCByZXNvbHZlOiByZXNvbHZlV2l0aENsZWFudXAsIHJlamVjdDogcmVqZWN0V2l0aENsZWFudXAgfTtcbiAgICAgICAgICAgICAgICB0cnkge1xuICAgICAgICAgICAgICAgICAgICByZXNwb25zZVByb21pc2VzLnNldChpZCwgcmVzcG9uc2VQcm9taXNlKTtcbiAgICAgICAgICAgICAgICAgICAgYXdhaXQgbWVzc2FnZVdyaXRlci53cml0ZShyZXF1ZXN0TWVzc2FnZSk7XG4gICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgIGNhdGNoIChlcnJvcikge1xuICAgICAgICAgICAgICAgICAgICAvLyBXcml0aW5nIHRoZSBtZXNzYWdlIGZhaWxlZC4gU28gd2UgbmVlZCB0byBkZWxldGUgaXQgZnJvbSB0aGUgcmVzcG9uc2UgcHJvbWlzZXMgYW5kXG4gICAgICAgICAgICAgICAgICAgIC8vIHJlamVjdCBpdC5cbiAgICAgICAgICAgICAgICAgICAgcmVzcG9uc2VQcm9taXNlcy5kZWxldGUoaWQpO1xuICAgICAgICAgICAgICAgICAgICByZXNwb25zZVByb21pc2UucmVqZWN0KG5ldyBtZXNzYWdlc18xLlJlc3BvbnNlRXJyb3IobWVzc2FnZXNfMS5FcnJvckNvZGVzLk1lc3NhZ2VXcml0ZUVycm9yLCBlcnJvci5tZXNzYWdlID8gZXJyb3IubWVzc2FnZSA6ICdVbmtub3duIHJlYXNvbicpKTtcbiAgICAgICAgICAgICAgICAgICAgbG9nZ2VyLmVycm9yKGBTZW5kaW5nIHJlcXVlc3QgZmFpbGVkLmApO1xuICAgICAgICAgICAgICAgICAgICB0aHJvdyBlcnJvcjtcbiAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICB9KTtcbiAgICAgICAgfSxcbiAgICAgICAgb25SZXF1ZXN0OiAodHlwZSwgaGFuZGxlcikgPT4ge1xuICAgICAgICAgICAgdGhyb3dJZkNsb3NlZE9yRGlzcG9zZWQoKTtcbiAgICAgICAgICAgIGxldCBtZXRob2QgPSBudWxsO1xuICAgICAgICAgICAgaWYgKFN0YXJSZXF1ZXN0SGFuZGxlci5pcyh0eXBlKSkge1xuICAgICAgICAgICAgICAgIG1ldGhvZCA9IHVuZGVmaW5lZDtcbiAgICAgICAgICAgICAgICBzdGFyUmVxdWVzdEhhbmRsZXIgPSB0eXBlO1xuICAgICAgICAgICAgfVxuICAgICAgICAgICAgZWxzZSBpZiAoSXMuc3RyaW5nKHR5cGUpKSB7XG4gICAgICAgICAgICAgICAgbWV0aG9kID0gbnVsbDtcbiAgICAgICAgICAgICAgICBpZiAoaGFuZGxlciAhPT0gdW5kZWZpbmVkKSB7XG4gICAgICAgICAgICAgICAgICAgIG1ldGhvZCA9IHR5cGU7XG4gICAgICAgICAgICAgICAgICAgIHJlcXVlc3RIYW5kbGVycy5zZXQodHlwZSwgeyBoYW5kbGVyOiBoYW5kbGVyLCB0eXBlOiB1bmRlZmluZWQgfSk7XG4gICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgfVxuICAgICAgICAgICAgZWxzZSB7XG4gICAgICAgICAgICAgICAgaWYgKGhhbmRsZXIgIT09IHVuZGVmaW5lZCkge1xuICAgICAgICAgICAgICAgICAgICBtZXRob2QgPSB0eXBlLm1ldGhvZDtcbiAgICAgICAgICAgICAgICAgICAgcmVxdWVzdEhhbmRsZXJzLnNldCh0eXBlLm1ldGhvZCwgeyB0eXBlLCBoYW5kbGVyIH0pO1xuICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgIH1cbiAgICAgICAgICAgIHJldHVybiB7XG4gICAgICAgICAgICAgICAgZGlzcG9zZTogKCkgPT4ge1xuICAgICAgICAgICAgICAgICAgICBpZiAobWV0aG9kID09PSBudWxsKSB7XG4gICAgICAgICAgICAgICAgICAgICAgICByZXR1cm47XG4gICAgICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICAgICAgaWYgKG1ldGhvZCAhPT0gdW5kZWZpbmVkKSB7XG4gICAgICAgICAgICAgICAgICAgICAgICByZXF1ZXN0SGFuZGxlcnMuZGVsZXRlKG1ldGhvZCk7XG4gICAgICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICAgICAgZWxzZSB7XG4gICAgICAgICAgICAgICAgICAgICAgICBzdGFyUmVxdWVzdEhhbmRsZXIgPSB1bmRlZmluZWQ7XG4gICAgICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICB9O1xuICAgICAgICB9LFxuICAgICAgICBoYXNQZW5kaW5nUmVzcG9uc2U6ICgpID0+IHtcbiAgICAgICAgICAgIHJldHVybiByZXNwb25zZVByb21pc2VzLnNpemUgPiAwO1xuICAgICAgICB9LFxuICAgICAgICB0cmFjZTogYXN5bmMgKF92YWx1ZSwgX3RyYWNlciwgc2VuZE5vdGlmaWNhdGlvbk9yVHJhY2VPcHRpb25zKSA9PiB7XG4gICAgICAgICAgICBsZXQgX3NlbmROb3RpZmljYXRpb24gPSBmYWxzZTtcbiAgICAgICAgICAgIGxldCBfdHJhY2VGb3JtYXQgPSBUcmFjZUZvcm1hdC5UZXh0O1xuICAgICAgICAgICAgaWYgKHNlbmROb3RpZmljYXRpb25PclRyYWNlT3B0aW9ucyAhPT0gdW5kZWZpbmVkKSB7XG4gICAgICAgICAgICAgICAgaWYgKElzLmJvb2xlYW4oc2VuZE5vdGlmaWNhdGlvbk9yVHJhY2VPcHRpb25zKSkge1xuICAgICAgICAgICAgICAgICAgICBfc2VuZE5vdGlmaWNhdGlvbiA9IHNlbmROb3RpZmljYXRpb25PclRyYWNlT3B0aW9ucztcbiAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgZWxzZSB7XG4gICAgICAgICAgICAgICAgICAgIF9zZW5kTm90aWZpY2F0aW9uID0gc2VuZE5vdGlmaWNhdGlvbk9yVHJhY2VPcHRpb25zLnNlbmROb3RpZmljYXRpb24gfHwgZmFsc2U7XG4gICAgICAgICAgICAgICAgICAgIF90cmFjZUZvcm1hdCA9IHNlbmROb3RpZmljYXRpb25PclRyYWNlT3B0aW9ucy50cmFjZUZvcm1hdCB8fCBUcmFjZUZvcm1hdC5UZXh0O1xuICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgIH1cbiAgICAgICAgICAgIHRyYWNlID0gX3ZhbHVlO1xuICAgICAgICAgICAgdHJhY2VGb3JtYXQgPSBfdHJhY2VGb3JtYXQ7XG4gICAgICAgICAgICBpZiAodHJhY2UgPT09IFRyYWNlLk9mZikge1xuICAgICAgICAgICAgICAgIHRyYWNlciA9IHVuZGVmaW5lZDtcbiAgICAgICAgICAgIH1cbiAgICAgICAgICAgIGVsc2Uge1xuICAgICAgICAgICAgICAgIHRyYWNlciA9IF90cmFjZXI7XG4gICAgICAgICAgICB9XG4gICAgICAgICAgICBpZiAoX3NlbmROb3RpZmljYXRpb24gJiYgIWlzQ2xvc2VkKCkgJiYgIWlzRGlzcG9zZWQoKSkge1xuICAgICAgICAgICAgICAgIGF3YWl0IGNvbm5lY3Rpb24uc2VuZE5vdGlmaWNhdGlvbihTZXRUcmFjZU5vdGlmaWNhdGlvbi50eXBlLCB7IHZhbHVlOiBUcmFjZS50b1N0cmluZyhfdmFsdWUpIH0pO1xuICAgICAgICAgICAgfVxuICAgICAgICB9LFxuICAgICAgICBvbkVycm9yOiBlcnJvckVtaXR0ZXIuZXZlbnQsXG4gICAgICAgIG9uQ2xvc2U6IGNsb3NlRW1pdHRlci5ldmVudCxcbiAgICAgICAgb25VbmhhbmRsZWROb3RpZmljYXRpb246IHVuaGFuZGxlZE5vdGlmaWNhdGlvbkVtaXR0ZXIuZXZlbnQsXG4gICAgICAgIG9uRGlzcG9zZTogZGlzcG9zZUVtaXR0ZXIuZXZlbnQsXG4gICAgICAgIGVuZDogKCkgPT4ge1xuICAgICAgICAgICAgbWVzc2FnZVdyaXRlci5lbmQoKTtcbiAgICAgICAgfSxcbiAgICAgICAgZGlzcG9zZTogKCkgPT4ge1xuICAgICAgICAgICAgaWYgKGlzRGlzcG9zZWQoKSkge1xuICAgICAgICAgICAgICAgIHJldHVybjtcbiAgICAgICAgICAgIH1cbiAgICAgICAgICAgIHN0YXRlID0gQ29ubmVjdGlvblN0YXRlLkRpc3Bvc2VkO1xuICAgICAgICAgICAgZGlzcG9zZUVtaXR0ZXIuZmlyZSh1bmRlZmluZWQpO1xuICAgICAgICAgICAgY29uc3QgZXJyb3IgPSBuZXcgbWVzc2FnZXNfMS5SZXNwb25zZUVycm9yKG1lc3NhZ2VzXzEuRXJyb3JDb2Rlcy5QZW5kaW5nUmVzcG9uc2VSZWplY3RlZCwgJ1BlbmRpbmcgcmVzcG9uc2UgcmVqZWN0ZWQgc2luY2UgY29ubmVjdGlvbiBnb3QgZGlzcG9zZWQnKTtcbiAgICAgICAgICAgIGZvciAoY29uc3QgcHJvbWlzZSBvZiByZXNwb25zZVByb21pc2VzLnZhbHVlcygpKSB7XG4gICAgICAgICAgICAgICAgcHJvbWlzZS5yZWplY3QoZXJyb3IpO1xuICAgICAgICAgICAgfVxuICAgICAgICAgICAgcmVzcG9uc2VQcm9taXNlcyA9IG5ldyBNYXAoKTtcbiAgICAgICAgICAgIHJlcXVlc3RUb2tlbnMgPSBuZXcgTWFwKCk7XG4gICAgICAgICAgICBrbm93bkNhbmNlbGVkUmVxdWVzdHMgPSBuZXcgU2V0KCk7XG4gICAgICAgICAgICBtZXNzYWdlUXVldWUgPSBuZXcgbGlua2VkTWFwXzEuTGlua2VkTWFwKCk7XG4gICAgICAgICAgICAvLyBUZXN0IGZvciBiYWNrd2FyZHMgY29tcGF0aWJpbGl0eVxuICAgICAgICAgICAgaWYgKElzLmZ1bmMobWVzc2FnZVdyaXRlci5kaXNwb3NlKSkge1xuICAgICAgICAgICAgICAgIG1lc3NhZ2VXcml0ZXIuZGlzcG9zZSgpO1xuICAgICAgICAgICAgfVxuICAgICAgICAgICAgaWYgKElzLmZ1bmMobWVzc2FnZVJlYWRlci5kaXNwb3NlKSkge1xuICAgICAgICAgICAgICAgIG1lc3NhZ2VSZWFkZXIuZGlzcG9zZSgpO1xuICAgICAgICAgICAgfVxuICAgICAgICB9LFxuICAgICAgICBsaXN0ZW46ICgpID0+IHtcbiAgICAgICAgICAgIHRocm93SWZDbG9zZWRPckRpc3Bvc2VkKCk7XG4gICAgICAgICAgICB0aHJvd0lmTGlzdGVuaW5nKCk7XG4gICAgICAgICAgICBzdGF0ZSA9IENvbm5lY3Rpb25TdGF0ZS5MaXN0ZW5pbmc7XG4gICAgICAgICAgICBtZXNzYWdlUmVhZGVyLmxpc3RlbihjYWxsYmFjayk7XG4gICAgICAgIH0sXG4gICAgICAgIGluc3BlY3Q6ICgpID0+IHtcbiAgICAgICAgICAgIC8vIGVzbGludC1kaXNhYmxlLW5leHQtbGluZSBuby1jb25zb2xlXG4gICAgICAgICAgICAoMCwgcmFsXzEuZGVmYXVsdCkoKS5jb25zb2xlLmxvZygnaW5zcGVjdCcpO1xuICAgICAgICB9XG4gICAgfTtcbiAgICBjb25uZWN0aW9uLm9uTm90aWZpY2F0aW9uKExvZ1RyYWNlTm90aWZpY2F0aW9uLnR5cGUsIChwYXJhbXMpID0+IHtcbiAgICAgICAgaWYgKHRyYWNlID09PSBUcmFjZS5PZmYgfHwgIXRyYWNlcikge1xuICAgICAgICAgICAgcmV0dXJuO1xuICAgICAgICB9XG4gICAgICAgIGNvbnN0IHZlcmJvc2UgPSB0cmFjZSA9PT0gVHJhY2UuVmVyYm9zZSB8fCB0cmFjZSA9PT0gVHJhY2UuQ29tcGFjdDtcbiAgICAgICAgdHJhY2VyLmxvZyhwYXJhbXMubWVzc2FnZSwgdmVyYm9zZSA/IHBhcmFtcy52ZXJib3NlIDogdW5kZWZpbmVkKTtcbiAgICB9KTtcbiAgICBjb25uZWN0aW9uLm9uTm90aWZpY2F0aW9uKFByb2dyZXNzTm90aWZpY2F0aW9uLnR5cGUsIChwYXJhbXMpID0+IHtcbiAgICAgICAgY29uc3QgaGFuZGxlciA9IHByb2dyZXNzSGFuZGxlcnMuZ2V0KHBhcmFtcy50b2tlbik7XG4gICAgICAgIGlmIChoYW5kbGVyKSB7XG4gICAgICAgICAgICBoYW5kbGVyKHBhcmFtcy52YWx1ZSk7XG4gICAgICAgIH1cbiAgICAgICAgZWxzZSB7XG4gICAgICAgICAgICB1bmhhbmRsZWRQcm9ncmVzc0VtaXR0ZXIuZmlyZShwYXJhbXMpO1xuICAgICAgICB9XG4gICAgfSk7XG4gICAgcmV0dXJuIGNvbm5lY3Rpb247XG59XG5leHBvcnRzLmNyZWF0ZU1lc3NhZ2VDb25uZWN0aW9uID0gY3JlYXRlTWVzc2FnZUNvbm5lY3Rpb247XG4iLCAiXCJ1c2Ugc3RyaWN0XCI7XG4vKiAtLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLVxuICogQ29weXJpZ2h0IChjKSBNaWNyb3NvZnQgQ29ycG9yYXRpb24uIEFsbCByaWdodHMgcmVzZXJ2ZWQuXG4gKiBMaWNlbnNlZCB1bmRlciB0aGUgTUlUIExpY2Vuc2UuIFNlZSBMaWNlbnNlLnR4dCBpbiB0aGUgcHJvamVjdCByb290IGZvciBsaWNlbnNlIGluZm9ybWF0aW9uLlxuICogLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tICovXG4vLy8gPHJlZmVyZW5jZSBwYXRoPVwiLi4vLi4vdHlwaW5ncy90aGVuYWJsZS5kLnRzXCIgLz5cbk9iamVjdC5kZWZpbmVQcm9wZXJ0eShleHBvcnRzLCBcIl9fZXNNb2R1bGVcIiwgeyB2YWx1ZTogdHJ1ZSB9KTtcbmV4cG9ydHMuUHJvZ3Jlc3NUeXBlID0gZXhwb3J0cy5Qcm9ncmVzc1Rva2VuID0gZXhwb3J0cy5jcmVhdGVNZXNzYWdlQ29ubmVjdGlvbiA9IGV4cG9ydHMuTnVsbExvZ2dlciA9IGV4cG9ydHMuQ29ubmVjdGlvbk9wdGlvbnMgPSBleHBvcnRzLkNvbm5lY3Rpb25TdHJhdGVneSA9IGV4cG9ydHMuQWJzdHJhY3RNZXNzYWdlQnVmZmVyID0gZXhwb3J0cy5Xcml0ZWFibGVTdHJlYW1NZXNzYWdlV3JpdGVyID0gZXhwb3J0cy5BYnN0cmFjdE1lc3NhZ2VXcml0ZXIgPSBleHBvcnRzLk1lc3NhZ2VXcml0ZXIgPSBleHBvcnRzLlJlYWRhYmxlU3RyZWFtTWVzc2FnZVJlYWRlciA9IGV4cG9ydHMuQWJzdHJhY3RNZXNzYWdlUmVhZGVyID0gZXhwb3J0cy5NZXNzYWdlUmVhZGVyID0gZXhwb3J0cy5TaGFyZWRBcnJheVJlY2VpdmVyU3RyYXRlZ3kgPSBleHBvcnRzLlNoYXJlZEFycmF5U2VuZGVyU3RyYXRlZ3kgPSBleHBvcnRzLkNhbmNlbGxhdGlvblRva2VuID0gZXhwb3J0cy5DYW5jZWxsYXRpb25Ub2tlblNvdXJjZSA9IGV4cG9ydHMuRW1pdHRlciA9IGV4cG9ydHMuRXZlbnQgPSBleHBvcnRzLkRpc3Bvc2FibGUgPSBleHBvcnRzLkxSVUNhY2hlID0gZXhwb3J0cy5Ub3VjaCA9IGV4cG9ydHMuTGlua2VkTWFwID0gZXhwb3J0cy5QYXJhbWV0ZXJTdHJ1Y3R1cmVzID0gZXhwb3J0cy5Ob3RpZmljYXRpb25UeXBlOSA9IGV4cG9ydHMuTm90aWZpY2F0aW9uVHlwZTggPSBleHBvcnRzLk5vdGlmaWNhdGlvblR5cGU3ID0gZXhwb3J0cy5Ob3RpZmljYXRpb25UeXBlNiA9IGV4cG9ydHMuTm90aWZpY2F0aW9uVHlwZTUgPSBleHBvcnRzLk5vdGlmaWNhdGlvblR5cGU0ID0gZXhwb3J0cy5Ob3RpZmljYXRpb25UeXBlMyA9IGV4cG9ydHMuTm90aWZpY2F0aW9uVHlwZTIgPSBleHBvcnRzLk5vdGlmaWNhdGlvblR5cGUxID0gZXhwb3J0cy5Ob3RpZmljYXRpb25UeXBlMCA9IGV4cG9ydHMuTm90aWZpY2F0aW9uVHlwZSA9IGV4cG9ydHMuRXJyb3JDb2RlcyA9IGV4cG9ydHMuUmVzcG9uc2VFcnJvciA9IGV4cG9ydHMuUmVxdWVzdFR5cGU5ID0gZXhwb3J0cy5SZXF1ZXN0VHlwZTggPSBleHBvcnRzLlJlcXVlc3RUeXBlNyA9IGV4cG9ydHMuUmVxdWVzdFR5cGU2ID0gZXhwb3J0cy5SZXF1ZXN0VHlwZTUgPSBleHBvcnRzLlJlcXVlc3RUeXBlNCA9IGV4cG9ydHMuUmVxdWVzdFR5cGUzID0gZXhwb3J0cy5SZXF1ZXN0VHlwZTIgPSBleHBvcnRzLlJlcXVlc3RUeXBlMSA9IGV4cG9ydHMuUmVxdWVzdFR5cGUwID0gZXhwb3J0cy5SZXF1ZXN0VHlwZSA9IGV4cG9ydHMuTWVzc2FnZSA9IGV4cG9ydHMuUkFMID0gdm9pZCAwO1xuZXhwb3J0cy5NZXNzYWdlU3RyYXRlZ3kgPSBleHBvcnRzLkNhbmNlbGxhdGlvblN0cmF0ZWd5ID0gZXhwb3J0cy5DYW5jZWxsYXRpb25TZW5kZXJTdHJhdGVneSA9IGV4cG9ydHMuQ2FuY2VsbGF0aW9uUmVjZWl2ZXJTdHJhdGVneSA9IGV4cG9ydHMuQ29ubmVjdGlvbkVycm9yID0gZXhwb3J0cy5Db25uZWN0aW9uRXJyb3JzID0gZXhwb3J0cy5Mb2dUcmFjZU5vdGlmaWNhdGlvbiA9IGV4cG9ydHMuU2V0VHJhY2VOb3RpZmljYXRpb24gPSBleHBvcnRzLlRyYWNlRm9ybWF0ID0gZXhwb3J0cy5UcmFjZVZhbHVlcyA9IGV4cG9ydHMuVHJhY2UgPSB2b2lkIDA7XG5jb25zdCBtZXNzYWdlc18xID0gcmVxdWlyZShcIi4vbWVzc2FnZXNcIik7XG5PYmplY3QuZGVmaW5lUHJvcGVydHkoZXhwb3J0cywgXCJNZXNzYWdlXCIsIHsgZW51bWVyYWJsZTogdHJ1ZSwgZ2V0OiBmdW5jdGlvbiAoKSB7IHJldHVybiBtZXNzYWdlc18xLk1lc3NhZ2U7IH0gfSk7XG5PYmplY3QuZGVmaW5lUHJvcGVydHkoZXhwb3J0cywgXCJSZXF1ZXN0VHlwZVwiLCB7IGVudW1lcmFibGU6IHRydWUsIGdldDogZnVuY3Rpb24gKCkgeyByZXR1cm4gbWVzc2FnZXNfMS5SZXF1ZXN0VHlwZTsgfSB9KTtcbk9iamVjdC5kZWZpbmVQcm9wZXJ0eShleHBvcnRzLCBcIlJlcXVlc3RUeXBlMFwiLCB7IGVudW1lcmFibGU6IHRydWUsIGdldDogZnVuY3Rpb24gKCkgeyByZXR1cm4gbWVzc2FnZXNfMS5SZXF1ZXN0VHlwZTA7IH0gfSk7XG5PYmplY3QuZGVmaW5lUHJvcGVydHkoZXhwb3J0cywgXCJSZXF1ZXN0VHlwZTFcIiwgeyBlbnVtZXJhYmxlOiB0cnVlLCBnZXQ6IGZ1bmN0aW9uICgpIHsgcmV0dXJuIG1lc3NhZ2VzXzEuUmVxdWVzdFR5cGUxOyB9IH0pO1xuT2JqZWN0LmRlZmluZVByb3BlcnR5KGV4cG9ydHMsIFwiUmVxdWVzdFR5cGUyXCIsIHsgZW51bWVyYWJsZTogdHJ1ZSwgZ2V0OiBmdW5jdGlvbiAoKSB7IHJldHVybiBtZXNzYWdlc18xLlJlcXVlc3RUeXBlMjsgfSB9KTtcbk9iamVjdC5kZWZpbmVQcm9wZXJ0eShleHBvcnRzLCBcIlJlcXVlc3RUeXBlM1wiLCB7IGVudW1lcmFibGU6IHRydWUsIGdldDogZnVuY3Rpb24gKCkgeyByZXR1cm4gbWVzc2FnZXNfMS5SZXF1ZXN0VHlwZTM7IH0gfSk7XG5PYmplY3QuZGVmaW5lUHJvcGVydHkoZXhwb3J0cywgXCJSZXF1ZXN0VHlwZTRcIiwgeyBlbnVtZXJhYmxlOiB0cnVlLCBnZXQ6IGZ1bmN0aW9uICgpIHsgcmV0dXJuIG1lc3NhZ2VzXzEuUmVxdWVzdFR5cGU0OyB9IH0pO1xuT2JqZWN0LmRlZmluZVByb3BlcnR5KGV4cG9ydHMsIFwiUmVxdWVzdFR5cGU1XCIsIHsgZW51bWVyYWJsZTogdHJ1ZSwgZ2V0OiBmdW5jdGlvbiAoKSB7IHJldHVybiBtZXNzYWdlc18xLlJlcXVlc3RUeXBlNTsgfSB9KTtcbk9iamVjdC5kZWZpbmVQcm9wZXJ0eShleHBvcnRzLCBcIlJlcXVlc3RUeXBlNlwiLCB7IGVudW1lcmFibGU6IHRydWUsIGdldDogZnVuY3Rpb24gKCkgeyByZXR1cm4gbWVzc2FnZXNfMS5SZXF1ZXN0VHlwZTY7IH0gfSk7XG5PYmplY3QuZGVmaW5lUHJvcGVydHkoZXhwb3J0cywgXCJSZXF1ZXN0VHlwZTdcIiwgeyBlbnVtZXJhYmxlOiB0cnVlLCBnZXQ6IGZ1bmN0aW9uICgpIHsgcmV0dXJuIG1lc3NhZ2VzXzEuUmVxdWVzdFR5cGU3OyB9IH0pO1xuT2JqZWN0LmRlZmluZVByb3BlcnR5KGV4cG9ydHMsIFwiUmVxdWVzdFR5cGU4XCIsIHsgZW51bWVyYWJsZTogdHJ1ZSwgZ2V0OiBmdW5jdGlvbiAoKSB7IHJldHVybiBtZXNzYWdlc18xLlJlcXVlc3RUeXBlODsgfSB9KTtcbk9iamVjdC5kZWZpbmVQcm9wZXJ0eShleHBvcnRzLCBcIlJlcXVlc3RUeXBlOVwiLCB7IGVudW1lcmFibGU6IHRydWUsIGdldDogZnVuY3Rpb24gKCkgeyByZXR1cm4gbWVzc2FnZXNfMS5SZXF1ZXN0VHlwZTk7IH0gfSk7XG5PYmplY3QuZGVmaW5lUHJvcGVydHkoZXhwb3J0cywgXCJSZXNwb25zZUVycm9yXCIsIHsgZW51bWVyYWJsZTogdHJ1ZSwgZ2V0OiBmdW5jdGlvbiAoKSB7IHJldHVybiBtZXNzYWdlc18xLlJlc3BvbnNlRXJyb3I7IH0gfSk7XG5PYmplY3QuZGVmaW5lUHJvcGVydHkoZXhwb3J0cywgXCJFcnJvckNvZGVzXCIsIHsgZW51bWVyYWJsZTogdHJ1ZSwgZ2V0OiBmdW5jdGlvbiAoKSB7IHJldHVybiBtZXNzYWdlc18xLkVycm9yQ29kZXM7IH0gfSk7XG5PYmplY3QuZGVmaW5lUHJvcGVydHkoZXhwb3J0cywgXCJOb3RpZmljYXRpb25UeXBlXCIsIHsgZW51bWVyYWJsZTogdHJ1ZSwgZ2V0OiBmdW5jdGlvbiAoKSB7IHJldHVybiBtZXNzYWdlc18xLk5vdGlmaWNhdGlvblR5cGU7IH0gfSk7XG5PYmplY3QuZGVmaW5lUHJvcGVydHkoZXhwb3J0cywgXCJOb3RpZmljYXRpb25UeXBlMFwiLCB7IGVudW1lcmFibGU6IHRydWUsIGdldDogZnVuY3Rpb24gKCkgeyByZXR1cm4gbWVzc2FnZXNfMS5Ob3RpZmljYXRpb25UeXBlMDsgfSB9KTtcbk9iamVjdC5kZWZpbmVQcm9wZXJ0eShleHBvcnRzLCBcIk5vdGlmaWNhdGlvblR5cGUxXCIsIHsgZW51bWVyYWJsZTogdHJ1ZSwgZ2V0OiBmdW5jdGlvbiAoKSB7IHJldHVybiBtZXNzYWdlc18xLk5vdGlmaWNhdGlvblR5cGUxOyB9IH0pO1xuT2JqZWN0LmRlZmluZVByb3BlcnR5KGV4cG9ydHMsIFwiTm90aWZpY2F0aW9uVHlwZTJcIiwgeyBlbnVtZXJhYmxlOiB0cnVlLCBnZXQ6IGZ1bmN0aW9uICgpIHsgcmV0dXJuIG1lc3NhZ2VzXzEuTm90aWZpY2F0aW9uVHlwZTI7IH0gfSk7XG5PYmplY3QuZGVmaW5lUHJvcGVydHkoZXhwb3J0cywgXCJOb3RpZmljYXRpb25UeXBlM1wiLCB7IGVudW1lcmFibGU6IHRydWUsIGdldDogZnVuY3Rpb24gKCkgeyByZXR1cm4gbWVzc2FnZXNfMS5Ob3RpZmljYXRpb25UeXBlMzsgfSB9KTtcbk9iamVjdC5kZWZpbmVQcm9wZXJ0eShleHBvcnRzLCBcIk5vdGlmaWNhdGlvblR5cGU0XCIsIHsgZW51bWVyYWJsZTogdHJ1ZSwgZ2V0OiBmdW5jdGlvbiAoKSB7IHJldHVybiBtZXNzYWdlc18xLk5vdGlmaWNhdGlvblR5cGU0OyB9IH0pO1xuT2JqZWN0LmRlZmluZVByb3BlcnR5KGV4cG9ydHMsIFwiTm90aWZpY2F0aW9uVHlwZTVcIiwgeyBlbnVtZXJhYmxlOiB0cnVlLCBnZXQ6IGZ1bmN0aW9uICgpIHsgcmV0dXJuIG1lc3NhZ2VzXzEuTm90aWZpY2F0aW9uVHlwZTU7IH0gfSk7XG5PYmplY3QuZGVmaW5lUHJvcGVydHkoZXhwb3J0cywgXCJOb3RpZmljYXRpb25UeXBlNlwiLCB7IGVudW1lcmFibGU6IHRydWUsIGdldDogZnVuY3Rpb24gKCkgeyByZXR1cm4gbWVzc2FnZXNfMS5Ob3RpZmljYXRpb25UeXBlNjsgfSB9KTtcbk9iamVjdC5kZWZpbmVQcm9wZXJ0eShleHBvcnRzLCBcIk5vdGlmaWNhdGlvblR5cGU3XCIsIHsgZW51bWVyYWJsZTogdHJ1ZSwgZ2V0OiBmdW5jdGlvbiAoKSB7IHJldHVybiBtZXNzYWdlc18xLk5vdGlmaWNhdGlvblR5cGU3OyB9IH0pO1xuT2JqZWN0LmRlZmluZVByb3BlcnR5KGV4cG9ydHMsIFwiTm90aWZpY2F0aW9uVHlwZThcIiwgeyBlbnVtZXJhYmxlOiB0cnVlLCBnZXQ6IGZ1bmN0aW9uICgpIHsgcmV0dXJuIG1lc3NhZ2VzXzEuTm90aWZpY2F0aW9uVHlwZTg7IH0gfSk7XG5PYmplY3QuZGVmaW5lUHJvcGVydHkoZXhwb3J0cywgXCJOb3RpZmljYXRpb25UeXBlOVwiLCB7IGVudW1lcmFibGU6IHRydWUsIGdldDogZnVuY3Rpb24gKCkgeyByZXR1cm4gbWVzc2FnZXNfMS5Ob3RpZmljYXRpb25UeXBlOTsgfSB9KTtcbk9iamVjdC5kZWZpbmVQcm9wZXJ0eShleHBvcnRzLCBcIlBhcmFtZXRlclN0cnVjdHVyZXNcIiwgeyBlbnVtZXJhYmxlOiB0cnVlLCBnZXQ6IGZ1bmN0aW9uICgpIHsgcmV0dXJuIG1lc3NhZ2VzXzEuUGFyYW1ldGVyU3RydWN0dXJlczsgfSB9KTtcbmNvbnN0IGxpbmtlZE1hcF8xID0gcmVxdWlyZShcIi4vbGlua2VkTWFwXCIpO1xuT2JqZWN0LmRlZmluZVByb3BlcnR5KGV4cG9ydHMsIFwiTGlua2VkTWFwXCIsIHsgZW51bWVyYWJsZTogdHJ1ZSwgZ2V0OiBmdW5jdGlvbiAoKSB7IHJldHVybiBsaW5rZWRNYXBfMS5MaW5rZWRNYXA7IH0gfSk7XG5PYmplY3QuZGVmaW5lUHJvcGVydHkoZXhwb3J0cywgXCJMUlVDYWNoZVwiLCB7IGVudW1lcmFibGU6IHRydWUsIGdldDogZnVuY3Rpb24gKCkgeyByZXR1cm4gbGlua2VkTWFwXzEuTFJVQ2FjaGU7IH0gfSk7XG5PYmplY3QuZGVmaW5lUHJvcGVydHkoZXhwb3J0cywgXCJUb3VjaFwiLCB7IGVudW1lcmFibGU6IHRydWUsIGdldDogZnVuY3Rpb24gKCkgeyByZXR1cm4gbGlua2VkTWFwXzEuVG91Y2g7IH0gfSk7XG5jb25zdCBkaXNwb3NhYmxlXzEgPSByZXF1aXJlKFwiLi9kaXNwb3NhYmxlXCIpO1xuT2JqZWN0LmRlZmluZVByb3BlcnR5KGV4cG9ydHMsIFwiRGlzcG9zYWJsZVwiLCB7IGVudW1lcmFibGU6IHRydWUsIGdldDogZnVuY3Rpb24gKCkgeyByZXR1cm4gZGlzcG9zYWJsZV8xLkRpc3Bvc2FibGU7IH0gfSk7XG5jb25zdCBldmVudHNfMSA9IHJlcXVpcmUoXCIuL2V2ZW50c1wiKTtcbk9iamVjdC5kZWZpbmVQcm9wZXJ0eShleHBvcnRzLCBcIkV2ZW50XCIsIHsgZW51bWVyYWJsZTogdHJ1ZSwgZ2V0OiBmdW5jdGlvbiAoKSB7IHJldHVybiBldmVudHNfMS5FdmVudDsgfSB9KTtcbk9iamVjdC5kZWZpbmVQcm9wZXJ0eShleHBvcnRzLCBcIkVtaXR0ZXJcIiwgeyBlbnVtZXJhYmxlOiB0cnVlLCBnZXQ6IGZ1bmN0aW9uICgpIHsgcmV0dXJuIGV2ZW50c18xLkVtaXR0ZXI7IH0gfSk7XG5jb25zdCBjYW5jZWxsYXRpb25fMSA9IHJlcXVpcmUoXCIuL2NhbmNlbGxhdGlvblwiKTtcbk9iamVjdC5kZWZpbmVQcm9wZXJ0eShleHBvcnRzLCBcIkNhbmNlbGxhdGlvblRva2VuU291cmNlXCIsIHsgZW51bWVyYWJsZTogdHJ1ZSwgZ2V0OiBmdW5jdGlvbiAoKSB7IHJldHVybiBjYW5jZWxsYXRpb25fMS5DYW5jZWxsYXRpb25Ub2tlblNvdXJjZTsgfSB9KTtcbk9iamVjdC5kZWZpbmVQcm9wZXJ0eShleHBvcnRzLCBcIkNhbmNlbGxhdGlvblRva2VuXCIsIHsgZW51bWVyYWJsZTogdHJ1ZSwgZ2V0OiBmdW5jdGlvbiAoKSB7IHJldHVybiBjYW5jZWxsYXRpb25fMS5DYW5jZWxsYXRpb25Ub2tlbjsgfSB9KTtcbmNvbnN0IHNoYXJlZEFycmF5Q2FuY2VsbGF0aW9uXzEgPSByZXF1aXJlKFwiLi9zaGFyZWRBcnJheUNhbmNlbGxhdGlvblwiKTtcbk9iamVjdC5kZWZpbmVQcm9wZXJ0eShleHBvcnRzLCBcIlNoYXJlZEFycmF5U2VuZGVyU3RyYXRlZ3lcIiwgeyBlbnVtZXJhYmxlOiB0cnVlLCBnZXQ6IGZ1bmN0aW9uICgpIHsgcmV0dXJuIHNoYXJlZEFycmF5Q2FuY2VsbGF0aW9uXzEuU2hhcmVkQXJyYXlTZW5kZXJTdHJhdGVneTsgfSB9KTtcbk9iamVjdC5kZWZpbmVQcm9wZXJ0eShleHBvcnRzLCBcIlNoYXJlZEFycmF5UmVjZWl2ZXJTdHJhdGVneVwiLCB7IGVudW1lcmFibGU6IHRydWUsIGdldDogZnVuY3Rpb24gKCkgeyByZXR1cm4gc2hhcmVkQXJyYXlDYW5jZWxsYXRpb25fMS5TaGFyZWRBcnJheVJlY2VpdmVyU3RyYXRlZ3k7IH0gfSk7XG5jb25zdCBtZXNzYWdlUmVhZGVyXzEgPSByZXF1aXJlKFwiLi9tZXNzYWdlUmVhZGVyXCIpO1xuT2JqZWN0LmRlZmluZVByb3BlcnR5KGV4cG9ydHMsIFwiTWVzc2FnZVJlYWRlclwiLCB7IGVudW1lcmFibGU6IHRydWUsIGdldDogZnVuY3Rpb24gKCkgeyByZXR1cm4gbWVzc2FnZVJlYWRlcl8xLk1lc3NhZ2VSZWFkZXI7IH0gfSk7XG5PYmplY3QuZGVmaW5lUHJvcGVydHkoZXhwb3J0cywgXCJBYnN0cmFjdE1lc3NhZ2VSZWFkZXJcIiwgeyBlbnVtZXJhYmxlOiB0cnVlLCBnZXQ6IGZ1bmN0aW9uICgpIHsgcmV0dXJuIG1lc3NhZ2VSZWFkZXJfMS5BYnN0cmFjdE1lc3NhZ2VSZWFkZXI7IH0gfSk7XG5PYmplY3QuZGVmaW5lUHJvcGVydHkoZXhwb3J0cywgXCJSZWFkYWJsZVN0cmVhbU1lc3NhZ2VSZWFkZXJcIiwgeyBlbnVtZXJhYmxlOiB0cnVlLCBnZXQ6IGZ1bmN0aW9uICgpIHsgcmV0dXJuIG1lc3NhZ2VSZWFkZXJfMS5SZWFkYWJsZVN0cmVhbU1lc3NhZ2VSZWFkZXI7IH0gfSk7XG5jb25zdCBtZXNzYWdlV3JpdGVyXzEgPSByZXF1aXJlKFwiLi9tZXNzYWdlV3JpdGVyXCIpO1xuT2JqZWN0LmRlZmluZVByb3BlcnR5KGV4cG9ydHMsIFwiTWVzc2FnZVdyaXRlclwiLCB7IGVudW1lcmFibGU6IHRydWUsIGdldDogZnVuY3Rpb24gKCkgeyByZXR1cm4gbWVzc2FnZVdyaXRlcl8xLk1lc3NhZ2VXcml0ZXI7IH0gfSk7XG5PYmplY3QuZGVmaW5lUHJvcGVydHkoZXhwb3J0cywgXCJBYnN0cmFjdE1lc3NhZ2VXcml0ZXJcIiwgeyBlbnVtZXJhYmxlOiB0cnVlLCBnZXQ6IGZ1bmN0aW9uICgpIHsgcmV0dXJuIG1lc3NhZ2VXcml0ZXJfMS5BYnN0cmFjdE1lc3NhZ2VXcml0ZXI7IH0gfSk7XG5PYmplY3QuZGVmaW5lUHJvcGVydHkoZXhwb3J0cywgXCJXcml0ZWFibGVTdHJlYW1NZXNzYWdlV3JpdGVyXCIsIHsgZW51bWVyYWJsZTogdHJ1ZSwgZ2V0OiBmdW5jdGlvbiAoKSB7IHJldHVybiBtZXNzYWdlV3JpdGVyXzEuV3JpdGVhYmxlU3RyZWFtTWVzc2FnZVdyaXRlcjsgfSB9KTtcbmNvbnN0IG1lc3NhZ2VCdWZmZXJfMSA9IHJlcXVpcmUoXCIuL21lc3NhZ2VCdWZmZXJcIik7XG5PYmplY3QuZGVmaW5lUHJvcGVydHkoZXhwb3J0cywgXCJBYnN0cmFjdE1lc3NhZ2VCdWZmZXJcIiwgeyBlbnVtZXJhYmxlOiB0cnVlLCBnZXQ6IGZ1bmN0aW9uICgpIHsgcmV0dXJuIG1lc3NhZ2VCdWZmZXJfMS5BYnN0cmFjdE1lc3NhZ2VCdWZmZXI7IH0gfSk7XG5jb25zdCBjb25uZWN0aW9uXzEgPSByZXF1aXJlKFwiLi9jb25uZWN0aW9uXCIpO1xuT2JqZWN0LmRlZmluZVByb3BlcnR5KGV4cG9ydHMsIFwiQ29ubmVjdGlvblN0cmF0ZWd5XCIsIHsgZW51bWVyYWJsZTogdHJ1ZSwgZ2V0OiBmdW5jdGlvbiAoKSB7IHJldHVybiBjb25uZWN0aW9uXzEuQ29ubmVjdGlvblN0cmF0ZWd5OyB9IH0pO1xuT2JqZWN0LmRlZmluZVByb3BlcnR5KGV4cG9ydHMsIFwiQ29ubmVjdGlvbk9wdGlvbnNcIiwgeyBlbnVtZXJhYmxlOiB0cnVlLCBnZXQ6IGZ1bmN0aW9uICgpIHsgcmV0dXJuIGNvbm5lY3Rpb25fMS5Db25uZWN0aW9uT3B0aW9uczsgfSB9KTtcbk9iamVjdC5kZWZpbmVQcm9wZXJ0eShleHBvcnRzLCBcIk51bGxMb2dnZXJcIiwgeyBlbnVtZXJhYmxlOiB0cnVlLCBnZXQ6IGZ1bmN0aW9uICgpIHsgcmV0dXJuIGNvbm5lY3Rpb25fMS5OdWxsTG9nZ2VyOyB9IH0pO1xuT2JqZWN0LmRlZmluZVByb3BlcnR5KGV4cG9ydHMsIFwiY3JlYXRlTWVzc2FnZUNvbm5lY3Rpb25cIiwgeyBlbnVtZXJhYmxlOiB0cnVlLCBnZXQ6IGZ1bmN0aW9uICgpIHsgcmV0dXJuIGNvbm5lY3Rpb25fMS5jcmVhdGVNZXNzYWdlQ29ubmVjdGlvbjsgfSB9KTtcbk9iamVjdC5kZWZpbmVQcm9wZXJ0eShleHBvcnRzLCBcIlByb2dyZXNzVG9rZW5cIiwgeyBlbnVtZXJhYmxlOiB0cnVlLCBnZXQ6IGZ1bmN0aW9uICgpIHsgcmV0dXJuIGNvbm5lY3Rpb25fMS5Qcm9ncmVzc1Rva2VuOyB9IH0pO1xuT2JqZWN0LmRlZmluZVByb3BlcnR5KGV4cG9ydHMsIFwiUHJvZ3Jlc3NUeXBlXCIsIHsgZW51bWVyYWJsZTogdHJ1ZSwgZ2V0OiBmdW5jdGlvbiAoKSB7IHJldHVybiBjb25uZWN0aW9uXzEuUHJvZ3Jlc3NUeXBlOyB9IH0pO1xuT2JqZWN0LmRlZmluZVByb3BlcnR5KGV4cG9ydHMsIFwiVHJhY2VcIiwgeyBlbnVtZXJhYmxlOiB0cnVlLCBnZXQ6IGZ1bmN0aW9uICgpIHsgcmV0dXJuIGNvbm5lY3Rpb25fMS5UcmFjZTsgfSB9KTtcbk9iamVjdC5kZWZpbmVQcm9wZXJ0eShleHBvcnRzLCBcIlRyYWNlVmFsdWVzXCIsIHsgZW51bWVyYWJsZTogdHJ1ZSwgZ2V0OiBmdW5jdGlvbiAoKSB7IHJldHVybiBjb25uZWN0aW9uXzEuVHJhY2VWYWx1ZXM7IH0gfSk7XG5PYmplY3QuZGVmaW5lUHJvcGVydHkoZXhwb3J0cywgXCJUcmFjZUZvcm1hdFwiLCB7IGVudW1lcmFibGU6IHRydWUsIGdldDogZnVuY3Rpb24gKCkgeyByZXR1cm4gY29ubmVjdGlvbl8xLlRyYWNlRm9ybWF0OyB9IH0pO1xuT2JqZWN0LmRlZmluZVByb3BlcnR5KGV4cG9ydHMsIFwiU2V0VHJhY2VOb3RpZmljYXRpb25cIiwgeyBlbnVtZXJhYmxlOiB0cnVlLCBnZXQ6IGZ1bmN0aW9uICgpIHsgcmV0dXJuIGNvbm5lY3Rpb25fMS5TZXRUcmFjZU5vdGlmaWNhdGlvbjsgfSB9KTtcbk9iamVjdC5kZWZpbmVQcm9wZXJ0eShleHBvcnRzLCBcIkxvZ1RyYWNlTm90aWZpY2F0aW9uXCIsIHsgZW51bWVyYWJsZTogdHJ1ZSwgZ2V0OiBmdW5jdGlvbiAoKSB7IHJldHVybiBjb25uZWN0aW9uXzEuTG9nVHJhY2VOb3RpZmljYXRpb247IH0gfSk7XG5PYmplY3QuZGVmaW5lUHJvcGVydHkoZXhwb3J0cywgXCJDb25uZWN0aW9uRXJyb3JzXCIsIHsgZW51bWVyYWJsZTogdHJ1ZSwgZ2V0OiBmdW5jdGlvbiAoKSB7IHJldHVybiBjb25uZWN0aW9uXzEuQ29ubmVjdGlvbkVycm9yczsgfSB9KTtcbk9iamVjdC5kZWZpbmVQcm9wZXJ0eShleHBvcnRzLCBcIkNvbm5lY3Rpb25FcnJvclwiLCB7IGVudW1lcmFibGU6IHRydWUsIGdldDogZnVuY3Rpb24gKCkgeyByZXR1cm4gY29ubmVjdGlvbl8xLkNvbm5lY3Rpb25FcnJvcjsgfSB9KTtcbk9iamVjdC5kZWZpbmVQcm9wZXJ0eShleHBvcnRzLCBcIkNhbmNlbGxhdGlvblJlY2VpdmVyU3RyYXRlZ3lcIiwgeyBlbnVtZXJhYmxlOiB0cnVlLCBnZXQ6IGZ1bmN0aW9uICgpIHsgcmV0dXJuIGNvbm5lY3Rpb25fMS5DYW5jZWxsYXRpb25SZWNlaXZlclN0cmF0ZWd5OyB9IH0pO1xuT2JqZWN0LmRlZmluZVByb3BlcnR5KGV4cG9ydHMsIFwiQ2FuY2VsbGF0aW9uU2VuZGVyU3RyYXRlZ3lcIiwgeyBlbnVtZXJhYmxlOiB0cnVlLCBnZXQ6IGZ1bmN0aW9uICgpIHsgcmV0dXJuIGNvbm5lY3Rpb25fMS5DYW5jZWxsYXRpb25TZW5kZXJTdHJhdGVneTsgfSB9KTtcbk9iamVjdC5kZWZpbmVQcm9wZXJ0eShleHBvcnRzLCBcIkNhbmNlbGxhdGlvblN0cmF0ZWd5XCIsIHsgZW51bWVyYWJsZTogdHJ1ZSwgZ2V0OiBmdW5jdGlvbiAoKSB7IHJldHVybiBjb25uZWN0aW9uXzEuQ2FuY2VsbGF0aW9uU3RyYXRlZ3k7IH0gfSk7XG5PYmplY3QuZGVmaW5lUHJvcGVydHkoZXhwb3J0cywgXCJNZXNzYWdlU3RyYXRlZ3lcIiwgeyBlbnVtZXJhYmxlOiB0cnVlLCBnZXQ6IGZ1bmN0aW9uICgpIHsgcmV0dXJuIGNvbm5lY3Rpb25fMS5NZXNzYWdlU3RyYXRlZ3k7IH0gfSk7XG5jb25zdCByYWxfMSA9IHJlcXVpcmUoXCIuL3JhbFwiKTtcbmV4cG9ydHMuUkFMID0gcmFsXzEuZGVmYXVsdDtcbiIsICJcInVzZSBzdHJpY3RcIjtcbi8qIC0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tXG4gKiBDb3B5cmlnaHQgKGMpIE1pY3Jvc29mdCBDb3Jwb3JhdGlvbi4gQWxsIHJpZ2h0cyByZXNlcnZlZC5cbiAqIExpY2Vuc2VkIHVuZGVyIHRoZSBNSVQgTGljZW5zZS4gU2VlIExpY2Vuc2UudHh0IGluIHRoZSBwcm9qZWN0IHJvb3QgZm9yIGxpY2Vuc2UgaW5mb3JtYXRpb24uXG4gKiAtLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0gKi9cbk9iamVjdC5kZWZpbmVQcm9wZXJ0eShleHBvcnRzLCBcIl9fZXNNb2R1bGVcIiwgeyB2YWx1ZTogdHJ1ZSB9KTtcbmNvbnN0IGFwaV8xID0gcmVxdWlyZShcIi4uL2NvbW1vbi9hcGlcIik7XG5jbGFzcyBNZXNzYWdlQnVmZmVyIGV4dGVuZHMgYXBpXzEuQWJzdHJhY3RNZXNzYWdlQnVmZmVyIHtcbiAgICBjb25zdHJ1Y3RvcihlbmNvZGluZyA9ICd1dGYtOCcpIHtcbiAgICAgICAgc3VwZXIoZW5jb2RpbmcpO1xuICAgICAgICB0aGlzLmFzY2lpRGVjb2RlciA9IG5ldyBUZXh0RGVjb2RlcignYXNjaWknKTtcbiAgICB9XG4gICAgZW1wdHlCdWZmZXIoKSB7XG4gICAgICAgIHJldHVybiBNZXNzYWdlQnVmZmVyLmVtcHR5QnVmZmVyO1xuICAgIH1cbiAgICBmcm9tU3RyaW5nKHZhbHVlLCBfZW5jb2RpbmcpIHtcbiAgICAgICAgcmV0dXJuIChuZXcgVGV4dEVuY29kZXIoKSkuZW5jb2RlKHZhbHVlKTtcbiAgICB9XG4gICAgdG9TdHJpbmcodmFsdWUsIGVuY29kaW5nKSB7XG4gICAgICAgIGlmIChlbmNvZGluZyA9PT0gJ2FzY2lpJykge1xuICAgICAgICAgICAgcmV0dXJuIHRoaXMuYXNjaWlEZWNvZGVyLmRlY29kZSh2YWx1ZSk7XG4gICAgICAgIH1cbiAgICAgICAgZWxzZSB7XG4gICAgICAgICAgICByZXR1cm4gKG5ldyBUZXh0RGVjb2RlcihlbmNvZGluZykpLmRlY29kZSh2YWx1ZSk7XG4gICAgICAgIH1cbiAgICB9XG4gICAgYXNOYXRpdmUoYnVmZmVyLCBsZW5ndGgpIHtcbiAgICAgICAgaWYgKGxlbmd0aCA9PT0gdW5kZWZpbmVkKSB7XG4gICAgICAgICAgICByZXR1cm4gYnVmZmVyO1xuICAgICAgICB9XG4gICAgICAgIGVsc2Uge1xuICAgICAgICAgICAgcmV0dXJuIGJ1ZmZlci5zbGljZSgwLCBsZW5ndGgpO1xuICAgICAgICB9XG4gICAgfVxuICAgIGFsbG9jTmF0aXZlKGxlbmd0aCkge1xuICAgICAgICByZXR1cm4gbmV3IFVpbnQ4QXJyYXkobGVuZ3RoKTtcbiAgICB9XG59XG5NZXNzYWdlQnVmZmVyLmVtcHR5QnVmZmVyID0gbmV3IFVpbnQ4QXJyYXkoMCk7XG5jbGFzcyBSZWFkYWJsZVN0cmVhbVdyYXBwZXIge1xuICAgIGNvbnN0cnVjdG9yKHNvY2tldCkge1xuICAgICAgICB0aGlzLnNvY2tldCA9IHNvY2tldDtcbiAgICAgICAgdGhpcy5fb25EYXRhID0gbmV3IGFwaV8xLkVtaXR0ZXIoKTtcbiAgICAgICAgdGhpcy5fbWVzc2FnZUxpc3RlbmVyID0gKGV2ZW50KSA9PiB7XG4gICAgICAgICAgICBjb25zdCBibG9iID0gZXZlbnQuZGF0YTtcbiAgICAgICAgICAgIGJsb2IuYXJyYXlCdWZmZXIoKS50aGVuKChidWZmZXIpID0+IHtcbiAgICAgICAgICAgICAgICB0aGlzLl9vbkRhdGEuZmlyZShuZXcgVWludDhBcnJheShidWZmZXIpKTtcbiAgICAgICAgICAgIH0sICgpID0+IHtcbiAgICAgICAgICAgICAgICAoMCwgYXBpXzEuUkFMKSgpLmNvbnNvbGUuZXJyb3IoYENvbnZlcnRpbmcgYmxvYiB0byBhcnJheSBidWZmZXIgZmFpbGVkLmApO1xuICAgICAgICAgICAgfSk7XG4gICAgICAgIH07XG4gICAgICAgIHRoaXMuc29ja2V0LmFkZEV2ZW50TGlzdGVuZXIoJ21lc3NhZ2UnLCB0aGlzLl9tZXNzYWdlTGlzdGVuZXIpO1xuICAgIH1cbiAgICBvbkNsb3NlKGxpc3RlbmVyKSB7XG4gICAgICAgIHRoaXMuc29ja2V0LmFkZEV2ZW50TGlzdGVuZXIoJ2Nsb3NlJywgbGlzdGVuZXIpO1xuICAgICAgICByZXR1cm4gYXBpXzEuRGlzcG9zYWJsZS5jcmVhdGUoKCkgPT4gdGhpcy5zb2NrZXQucmVtb3ZlRXZlbnRMaXN0ZW5lcignY2xvc2UnLCBsaXN0ZW5lcikpO1xuICAgIH1cbiAgICBvbkVycm9yKGxpc3RlbmVyKSB7XG4gICAgICAgIHRoaXMuc29ja2V0LmFkZEV2ZW50TGlzdGVuZXIoJ2Vycm9yJywgbGlzdGVuZXIpO1xuICAgICAgICByZXR1cm4gYXBpXzEuRGlzcG9zYWJsZS5jcmVhdGUoKCkgPT4gdGhpcy5zb2NrZXQucmVtb3ZlRXZlbnRMaXN0ZW5lcignZXJyb3InLCBsaXN0ZW5lcikpO1xuICAgIH1cbiAgICBvbkVuZChsaXN0ZW5lcikge1xuICAgICAgICB0aGlzLnNvY2tldC5hZGRFdmVudExpc3RlbmVyKCdlbmQnLCBsaXN0ZW5lcik7XG4gICAgICAgIHJldHVybiBhcGlfMS5EaXNwb3NhYmxlLmNyZWF0ZSgoKSA9PiB0aGlzLnNvY2tldC5yZW1vdmVFdmVudExpc3RlbmVyKCdlbmQnLCBsaXN0ZW5lcikpO1xuICAgIH1cbiAgICBvbkRhdGEobGlzdGVuZXIpIHtcbiAgICAgICAgcmV0dXJuIHRoaXMuX29uRGF0YS5ldmVudChsaXN0ZW5lcik7XG4gICAgfVxufVxuY2xhc3MgV3JpdGFibGVTdHJlYW1XcmFwcGVyIHtcbiAgICBjb25zdHJ1Y3Rvcihzb2NrZXQpIHtcbiAgICAgICAgdGhpcy5zb2NrZXQgPSBzb2NrZXQ7XG4gICAgfVxuICAgIG9uQ2xvc2UobGlzdGVuZXIpIHtcbiAgICAgICAgdGhpcy5zb2NrZXQuYWRkRXZlbnRMaXN0ZW5lcignY2xvc2UnLCBsaXN0ZW5lcik7XG4gICAgICAgIHJldHVybiBhcGlfMS5EaXNwb3NhYmxlLmNyZWF0ZSgoKSA9PiB0aGlzLnNvY2tldC5yZW1vdmVFdmVudExpc3RlbmVyKCdjbG9zZScsIGxpc3RlbmVyKSk7XG4gICAgfVxuICAgIG9uRXJyb3IobGlzdGVuZXIpIHtcbiAgICAgICAgdGhpcy5zb2NrZXQuYWRkRXZlbnRMaXN0ZW5lcignZXJyb3InLCBsaXN0ZW5lcik7XG4gICAgICAgIHJldHVybiBhcGlfMS5EaXNwb3NhYmxlLmNyZWF0ZSgoKSA9PiB0aGlzLnNvY2tldC5yZW1vdmVFdmVudExpc3RlbmVyKCdlcnJvcicsIGxpc3RlbmVyKSk7XG4gICAgfVxuICAgIG9uRW5kKGxpc3RlbmVyKSB7XG4gICAgICAgIHRoaXMuc29ja2V0LmFkZEV2ZW50TGlzdGVuZXIoJ2VuZCcsIGxpc3RlbmVyKTtcbiAgICAgICAgcmV0dXJuIGFwaV8xLkRpc3Bvc2FibGUuY3JlYXRlKCgpID0+IHRoaXMuc29ja2V0LnJlbW92ZUV2ZW50TGlzdGVuZXIoJ2VuZCcsIGxpc3RlbmVyKSk7XG4gICAgfVxuICAgIHdyaXRlKGRhdGEsIGVuY29kaW5nKSB7XG4gICAgICAgIGlmICh0eXBlb2YgZGF0YSA9PT0gJ3N0cmluZycpIHtcbiAgICAgICAgICAgIGlmIChlbmNvZGluZyAhPT0gdW5kZWZpbmVkICYmIGVuY29kaW5nICE9PSAndXRmLTgnKSB7XG4gICAgICAgICAgICAgICAgdGhyb3cgbmV3IEVycm9yKGBJbiBhIEJyb3dzZXIgZW52aXJvbm1lbnRzIG9ubHkgdXRmLTggdGV4dCBlbmNvZGluZyBpcyBzdXBwb3J0ZWQuIEJ1dCBnb3QgZW5jb2Rpbmc6ICR7ZW5jb2Rpbmd9YCk7XG4gICAgICAgICAgICB9XG4gICAgICAgICAgICB0aGlzLnNvY2tldC5zZW5kKGRhdGEpO1xuICAgICAgICB9XG4gICAgICAgIGVsc2Uge1xuICAgICAgICAgICAgdGhpcy5zb2NrZXQuc2VuZChkYXRhKTtcbiAgICAgICAgfVxuICAgICAgICByZXR1cm4gUHJvbWlzZS5yZXNvbHZlKCk7XG4gICAgfVxuICAgIGVuZCgpIHtcbiAgICAgICAgdGhpcy5zb2NrZXQuY2xvc2UoKTtcbiAgICB9XG59XG5jb25zdCBfdGV4dEVuY29kZXIgPSBuZXcgVGV4dEVuY29kZXIoKTtcbmNvbnN0IF9yaWwgPSBPYmplY3QuZnJlZXplKHtcbiAgICBtZXNzYWdlQnVmZmVyOiBPYmplY3QuZnJlZXplKHtcbiAgICAgICAgY3JlYXRlOiAoZW5jb2RpbmcpID0+IG5ldyBNZXNzYWdlQnVmZmVyKGVuY29kaW5nKVxuICAgIH0pLFxuICAgIGFwcGxpY2F0aW9uSnNvbjogT2JqZWN0LmZyZWV6ZSh7XG4gICAgICAgIGVuY29kZXI6IE9iamVjdC5mcmVlemUoe1xuICAgICAgICAgICAgbmFtZTogJ2FwcGxpY2F0aW9uL2pzb24nLFxuICAgICAgICAgICAgZW5jb2RlOiAobXNnLCBvcHRpb25zKSA9PiB7XG4gICAgICAgICAgICAgICAgaWYgKG9wdGlvbnMuY2hhcnNldCAhPT0gJ3V0Zi04Jykge1xuICAgICAgICAgICAgICAgICAgICB0aHJvdyBuZXcgRXJyb3IoYEluIGEgQnJvd3NlciBlbnZpcm9ubWVudHMgb25seSB1dGYtOCB0ZXh0IGVuY29kaW5nIGlzIHN1cHBvcnRlZC4gQnV0IGdvdCBlbmNvZGluZzogJHtvcHRpb25zLmNoYXJzZXR9YCk7XG4gICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgIHJldHVybiBQcm9taXNlLnJlc29sdmUoX3RleHRFbmNvZGVyLmVuY29kZShKU09OLnN0cmluZ2lmeShtc2csIHVuZGVmaW5lZCwgMCkpKTtcbiAgICAgICAgICAgIH1cbiAgICAgICAgfSksXG4gICAgICAgIGRlY29kZXI6IE9iamVjdC5mcmVlemUoe1xuICAgICAgICAgICAgbmFtZTogJ2FwcGxpY2F0aW9uL2pzb24nLFxuICAgICAgICAgICAgZGVjb2RlOiAoYnVmZmVyLCBvcHRpb25zKSA9PiB7XG4gICAgICAgICAgICAgICAgaWYgKCEoYnVmZmVyIGluc3RhbmNlb2YgVWludDhBcnJheSkpIHtcbiAgICAgICAgICAgICAgICAgICAgdGhyb3cgbmV3IEVycm9yKGBJbiBhIEJyb3dzZXIgZW52aXJvbm1lbnRzIG9ubHkgVWludDhBcnJheXMgYXJlIHN1cHBvcnRlZC5gKTtcbiAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgcmV0dXJuIFByb21pc2UucmVzb2x2ZShKU09OLnBhcnNlKG5ldyBUZXh0RGVjb2RlcihvcHRpb25zLmNoYXJzZXQpLmRlY29kZShidWZmZXIpKSk7XG4gICAgICAgICAgICB9XG4gICAgICAgIH0pXG4gICAgfSksXG4gICAgc3RyZWFtOiBPYmplY3QuZnJlZXplKHtcbiAgICAgICAgYXNSZWFkYWJsZVN0cmVhbTogKHNvY2tldCkgPT4gbmV3IFJlYWRhYmxlU3RyZWFtV3JhcHBlcihzb2NrZXQpLFxuICAgICAgICBhc1dyaXRhYmxlU3RyZWFtOiAoc29ja2V0KSA9PiBuZXcgV3JpdGFibGVTdHJlYW1XcmFwcGVyKHNvY2tldClcbiAgICB9KSxcbiAgICBjb25zb2xlOiBjb25zb2xlLFxuICAgIHRpbWVyOiBPYmplY3QuZnJlZXplKHtcbiAgICAgICAgc2V0VGltZW91dChjYWxsYmFjaywgbXMsIC4uLmFyZ3MpIHtcbiAgICAgICAgICAgIGNvbnN0IGhhbmRsZSA9IHNldFRpbWVvdXQoY2FsbGJhY2ssIG1zLCAuLi5hcmdzKTtcbiAgICAgICAgICAgIHJldHVybiB7IGRpc3Bvc2U6ICgpID0+IGNsZWFyVGltZW91dChoYW5kbGUpIH07XG4gICAgICAgIH0sXG4gICAgICAgIHNldEltbWVkaWF0ZShjYWxsYmFjaywgLi4uYXJncykge1xuICAgICAgICAgICAgY29uc3QgaGFuZGxlID0gc2V0VGltZW91dChjYWxsYmFjaywgMCwgLi4uYXJncyk7XG4gICAgICAgICAgICByZXR1cm4geyBkaXNwb3NlOiAoKSA9PiBjbGVhclRpbWVvdXQoaGFuZGxlKSB9O1xuICAgICAgICB9LFxuICAgICAgICBzZXRJbnRlcnZhbChjYWxsYmFjaywgbXMsIC4uLmFyZ3MpIHtcbiAgICAgICAgICAgIGNvbnN0IGhhbmRsZSA9IHNldEludGVydmFsKGNhbGxiYWNrLCBtcywgLi4uYXJncyk7XG4gICAgICAgICAgICByZXR1cm4geyBkaXNwb3NlOiAoKSA9PiBjbGVhckludGVydmFsKGhhbmRsZSkgfTtcbiAgICAgICAgfSxcbiAgICB9KVxufSk7XG5mdW5jdGlvbiBSSUwoKSB7XG4gICAgcmV0dXJuIF9yaWw7XG59XG4oZnVuY3Rpb24gKFJJTCkge1xuICAgIGZ1bmN0aW9uIGluc3RhbGwoKSB7XG4gICAgICAgIGFwaV8xLlJBTC5pbnN0YWxsKF9yaWwpO1xuICAgIH1cbiAgICBSSUwuaW5zdGFsbCA9IGluc3RhbGw7XG59KShSSUwgfHwgKFJJTCA9IHt9KSk7XG5leHBvcnRzLmRlZmF1bHQgPSBSSUw7XG4iLCAiXCJ1c2Ugc3RyaWN0XCI7XG4vKiAtLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLVxuICogQ29weXJpZ2h0IChjKSBNaWNyb3NvZnQgQ29ycG9yYXRpb24uIEFsbCByaWdodHMgcmVzZXJ2ZWQuXG4gKiBMaWNlbnNlZCB1bmRlciB0aGUgTUlUIExpY2Vuc2UuIFNlZSBMaWNlbnNlLnR4dCBpbiB0aGUgcHJvamVjdCByb290IGZvciBsaWNlbnNlIGluZm9ybWF0aW9uLlxuICogLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tICovXG52YXIgX19jcmVhdGVCaW5kaW5nID0gKHRoaXMgJiYgdGhpcy5fX2NyZWF0ZUJpbmRpbmcpIHx8IChPYmplY3QuY3JlYXRlID8gKGZ1bmN0aW9uKG8sIG0sIGssIGsyKSB7XG4gICAgaWYgKGsyID09PSB1bmRlZmluZWQpIGsyID0gaztcbiAgICB2YXIgZGVzYyA9IE9iamVjdC5nZXRPd25Qcm9wZXJ0eURlc2NyaXB0b3IobSwgayk7XG4gICAgaWYgKCFkZXNjIHx8IChcImdldFwiIGluIGRlc2MgPyAhbS5fX2VzTW9kdWxlIDogZGVzYy53cml0YWJsZSB8fCBkZXNjLmNvbmZpZ3VyYWJsZSkpIHtcbiAgICAgIGRlc2MgPSB7IGVudW1lcmFibGU6IHRydWUsIGdldDogZnVuY3Rpb24oKSB7IHJldHVybiBtW2tdOyB9IH07XG4gICAgfVxuICAgIE9iamVjdC5kZWZpbmVQcm9wZXJ0eShvLCBrMiwgZGVzYyk7XG59KSA6IChmdW5jdGlvbihvLCBtLCBrLCBrMikge1xuICAgIGlmIChrMiA9PT0gdW5kZWZpbmVkKSBrMiA9IGs7XG4gICAgb1trMl0gPSBtW2tdO1xufSkpO1xudmFyIF9fZXhwb3J0U3RhciA9ICh0aGlzICYmIHRoaXMuX19leHBvcnRTdGFyKSB8fCBmdW5jdGlvbihtLCBleHBvcnRzKSB7XG4gICAgZm9yICh2YXIgcCBpbiBtKSBpZiAocCAhPT0gXCJkZWZhdWx0XCIgJiYgIU9iamVjdC5wcm90b3R5cGUuaGFzT3duUHJvcGVydHkuY2FsbChleHBvcnRzLCBwKSkgX19jcmVhdGVCaW5kaW5nKGV4cG9ydHMsIG0sIHApO1xufTtcbk9iamVjdC5kZWZpbmVQcm9wZXJ0eShleHBvcnRzLCBcIl9fZXNNb2R1bGVcIiwgeyB2YWx1ZTogdHJ1ZSB9KTtcbmV4cG9ydHMuY3JlYXRlTWVzc2FnZUNvbm5lY3Rpb24gPSBleHBvcnRzLkJyb3dzZXJNZXNzYWdlV3JpdGVyID0gZXhwb3J0cy5Ccm93c2VyTWVzc2FnZVJlYWRlciA9IHZvaWQgMDtcbmNvbnN0IHJpbF8xID0gcmVxdWlyZShcIi4vcmlsXCIpO1xuLy8gSW5zdGFsbCB0aGUgYnJvd3NlciBydW50aW1lIGFic3RyYWN0LlxucmlsXzEuZGVmYXVsdC5pbnN0YWxsKCk7XG5jb25zdCBhcGlfMSA9IHJlcXVpcmUoXCIuLi9jb21tb24vYXBpXCIpO1xuX19leHBvcnRTdGFyKHJlcXVpcmUoXCIuLi9jb21tb24vYXBpXCIpLCBleHBvcnRzKTtcbmNsYXNzIEJyb3dzZXJNZXNzYWdlUmVhZGVyIGV4dGVuZHMgYXBpXzEuQWJzdHJhY3RNZXNzYWdlUmVhZGVyIHtcbiAgICBjb25zdHJ1Y3Rvcihwb3J0KSB7XG4gICAgICAgIHN1cGVyKCk7XG4gICAgICAgIHRoaXMuX29uRGF0YSA9IG5ldyBhcGlfMS5FbWl0dGVyKCk7XG4gICAgICAgIHRoaXMuX21lc3NhZ2VMaXN0ZW5lciA9IChldmVudCkgPT4ge1xuICAgICAgICAgICAgdGhpcy5fb25EYXRhLmZpcmUoZXZlbnQuZGF0YSk7XG4gICAgICAgIH07XG4gICAgICAgIHBvcnQuYWRkRXZlbnRMaXN0ZW5lcignZXJyb3InLCAoZXZlbnQpID0+IHRoaXMuZmlyZUVycm9yKGV2ZW50KSk7XG4gICAgICAgIHBvcnQub25tZXNzYWdlID0gdGhpcy5fbWVzc2FnZUxpc3RlbmVyO1xuICAgIH1cbiAgICBsaXN0ZW4oY2FsbGJhY2spIHtcbiAgICAgICAgcmV0dXJuIHRoaXMuX29uRGF0YS5ldmVudChjYWxsYmFjayk7XG4gICAgfVxufVxuZXhwb3J0cy5Ccm93c2VyTWVzc2FnZVJlYWRlciA9IEJyb3dzZXJNZXNzYWdlUmVhZGVyO1xuY2xhc3MgQnJvd3Nlck1lc3NhZ2VXcml0ZXIgZXh0ZW5kcyBhcGlfMS5BYnN0cmFjdE1lc3NhZ2VXcml0ZXIge1xuICAgIGNvbnN0cnVjdG9yKHBvcnQpIHtcbiAgICAgICAgc3VwZXIoKTtcbiAgICAgICAgdGhpcy5wb3J0ID0gcG9ydDtcbiAgICAgICAgdGhpcy5lcnJvckNvdW50ID0gMDtcbiAgICAgICAgcG9ydC5hZGRFdmVudExpc3RlbmVyKCdlcnJvcicsIChldmVudCkgPT4gdGhpcy5maXJlRXJyb3IoZXZlbnQpKTtcbiAgICB9XG4gICAgd3JpdGUobXNnKSB7XG4gICAgICAgIHRyeSB7XG4gICAgICAgICAgICB0aGlzLnBvcnQucG9zdE1lc3NhZ2UobXNnKTtcbiAgICAgICAgICAgIHJldHVybiBQcm9taXNlLnJlc29sdmUoKTtcbiAgICAgICAgfVxuICAgICAgICBjYXRjaCAoZXJyb3IpIHtcbiAgICAgICAgICAgIHRoaXMuaGFuZGxlRXJyb3IoZXJyb3IsIG1zZyk7XG4gICAgICAgICAgICByZXR1cm4gUHJvbWlzZS5yZWplY3QoZXJyb3IpO1xuICAgICAgICB9XG4gICAgfVxuICAgIGhhbmRsZUVycm9yKGVycm9yLCBtc2cpIHtcbiAgICAgICAgdGhpcy5lcnJvckNvdW50Kys7XG4gICAgICAgIHRoaXMuZmlyZUVycm9yKGVycm9yLCBtc2csIHRoaXMuZXJyb3JDb3VudCk7XG4gICAgfVxuICAgIGVuZCgpIHtcbiAgICB9XG59XG5leHBvcnRzLkJyb3dzZXJNZXNzYWdlV3JpdGVyID0gQnJvd3Nlck1lc3NhZ2VXcml0ZXI7XG5mdW5jdGlvbiBjcmVhdGVNZXNzYWdlQ29ubmVjdGlvbihyZWFkZXIsIHdyaXRlciwgbG9nZ2VyLCBvcHRpb25zKSB7XG4gICAgaWYgKGxvZ2dlciA9PT0gdW5kZWZpbmVkKSB7XG4gICAgICAgIGxvZ2dlciA9IGFwaV8xLk51bGxMb2dnZXI7XG4gICAgfVxuICAgIGlmIChhcGlfMS5Db25uZWN0aW9uU3RyYXRlZ3kuaXMob3B0aW9ucykpIHtcbiAgICAgICAgb3B0aW9ucyA9IHsgY29ubmVjdGlvblN0cmF0ZWd5OiBvcHRpb25zIH07XG4gICAgfVxuICAgIHJldHVybiAoMCwgYXBpXzEuY3JlYXRlTWVzc2FnZUNvbm5lY3Rpb24pKHJlYWRlciwgd3JpdGVyLCBsb2dnZXIsIG9wdGlvbnMpO1xufVxuZXhwb3J0cy5jcmVhdGVNZXNzYWdlQ29ubmVjdGlvbiA9IGNyZWF0ZU1lc3NhZ2VDb25uZWN0aW9uO1xuIiwgIi8qIC0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tXG4gKiBDb3B5cmlnaHQgKGMpIE1pY3Jvc29mdCBDb3Jwb3JhdGlvbi4gQWxsIHJpZ2h0cyByZXNlcnZlZC5cbiAqIExpY2Vuc2VkIHVuZGVyIHRoZSBNSVQgTGljZW5zZS4gU2VlIExpY2Vuc2UudHh0IGluIHRoZSBwcm9qZWN0IHJvb3QgZm9yIGxpY2Vuc2UgaW5mb3JtYXRpb24uXG4gKiAtLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLSAqL1xuJ3VzZSBzdHJpY3QnO1xuXG5tb2R1bGUuZXhwb3J0cyA9IHJlcXVpcmUoJy4vbGliL2Jyb3dzZXIvbWFpbicpOyIsICIvKlxuICogQ29weXJpZ2h0IChjKSAyMDEwLTIwMjYgRWNsaXBzZSBEaXJpZ2libGUgY29udHJpYnV0b3JzXG4gKlxuICogQWxsIHJpZ2h0cyByZXNlcnZlZC4gVGhpcyBwcm9ncmFtIGFuZCB0aGUgYWNjb21wYW55aW5nIG1hdGVyaWFscyBhcmUgbWFkZSBhdmFpbGFibGUgdW5kZXIgdGhlXG4gKiB0ZXJtcyBvZiB0aGUgRWNsaXBzZSBQdWJsaWMgTGljZW5zZSB2Mi4wIHdoaWNoIGFjY29tcGFuaWVzIHRoaXMgZGlzdHJpYnV0aW9uLCBhbmQgaXMgYXZhaWxhYmxlIGF0XG4gKiBodHRwOi8vd3d3LmVjbGlwc2Uub3JnL2xlZ2FsL2VwbC12MjAuaHRtbFxuICpcbiAqIFNQRFgtRmlsZUNvcHlyaWdodFRleHQ6IEVjbGlwc2UgRGlyaWdpYmxlIGNvbnRyaWJ1dG9ycyBTUERYLUxpY2Vuc2UtSWRlbnRpZmllcjogRVBMLTIuMFxuICovXG5cbi8qKlxuICogSmF2YSBMU1AgY2xpZW50IGJ1bmRsZS5cbiAqXG4gKiBVc2VzIHZzY29kZS13cy1qc29ucnBjIGZvciB0eXBlZCBKU09OLVJQQyBvdmVyIFdlYlNvY2tldCBhbmQgcmVnaXN0ZXJzIE1vbmFjb1xuICogcHJvdmlkZXJzIHRoYXQgZGVsZWdhdGUgdG8gSkRULkxTLiBFeHBvc2VkIGFzIGdsb2JhbCBKYXZhTHNwQ2xpZW50TGliIHNvIHRoYXRcbiAqIGVkaXRvci5qcyBjYW4gY2FsbCBKYXZhTHNwQ2xpZW50TGliLmNvbm5lY3QocmVzb3VyY2VQYXRoKS5cbiAqXG4gKiBPbmUgSkRULkxTIHByb2Nlc3MgY292ZXJzIHRoZSBlbnRpcmUgd29ya3NwYWNlLCBzbyBhIHNpbmdsZSBXZWJTb2NrZXQgY29ubmVjdGlvblxuICogaXMgc2hhcmVkIGFjcm9zcyBhbGwgSmF2YSBmaWxlcyBvcGVuIGluIHRoZSBzYW1lIGJyb3dzZXIgcGFnZS4gVGhlIGNvbm5lY3Rpb24gaXNcbiAqIGVzdGFibGlzaGVkIG9uIHRoZSBmaXJzdCBjb25uZWN0KCkgY2FsbCBhbmQgcmV1c2VkIGZvciBhbGwgc3Vic2VxdWVudCBjYWxscy5cbiAqXG4gKiBlZGl0b3IuanMgc2V0cyB3aW5kb3cubW9uYWNvIGJlZm9yZSBjYWxsaW5nIGNvbm5lY3QoKSwgc28gdGhlIG1vbmFjby1zaGltJ3MgbGF6eVxuICogUHJveGllcyByZXNvbHZlIGNvcnJlY3RseSBhdCBjYWxsIHRpbWUuXG4gKi9cblxuaW1wb3J0IHsgY3JlYXRlTWVzc2FnZUNvbm5lY3Rpb24sIE1lc3NhZ2VDb25uZWN0aW9uIH0gZnJvbSAndnNjb2RlLWpzb25ycGMvYnJvd3Nlcic7XG5pbXBvcnQgeyB0b1NvY2tldCwgV2ViU29ja2V0TWVzc2FnZVJlYWRlciwgV2ViU29ja2V0TWVzc2FnZVdyaXRlciB9IGZyb20gJ3ZzY29kZS13cy1qc29ucnBjJztcbmltcG9ydCAqIGFzIG1vbmFjbyBmcm9tICdtb25hY28tZWRpdG9yJztcbmltcG9ydCB7XG4gICAgQ29tcGxldGlvbkl0ZW1LaW5kLFxuICAgIERpYWdub3N0aWNTZXZlcml0eSxcbiAgICBJbnNlcnRUZXh0Rm9ybWF0LFxuICAgIE1hcmt1cENvbnRlbnQsXG4gICAgTWFya3VwS2luZCxcbiAgICB0eXBlIENvZGVBY3Rpb24sXG4gICAgdHlwZSBDb21tYW5kLFxuICAgIHR5cGUgQ29tcGxldGlvbkl0ZW0sXG4gICAgdHlwZSBDb21wbGV0aW9uTGlzdCxcbiAgICB0eXBlIERpYWdub3N0aWMsXG4gICAgdHlwZSBIb3ZlcixcbiAgICB0eXBlIExvY2F0aW9uLFxuICAgIHR5cGUgUGFyYW1ldGVySW5mb3JtYXRpb24sXG4gICAgdHlwZSBTaWduYXR1cmVIZWxwLFxuICAgIHR5cGUgU2lnbmF0dXJlSW5mb3JtYXRpb24sXG4gICAgdHlwZSBUZXh0RWRpdCxcbiAgICB0eXBlIFdvcmtzcGFjZUVkaXQsXG59IGZyb20gJ3ZzY29kZS1sYW5ndWFnZXNlcnZlci10eXBlcyc7XG5cbi8vIC0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS1cbi8vIFNpbmdsZXRvbiBzdGF0ZVxuLy8gLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLVxuXG4vKiogU2hhcmVkIGNvbm5lY3Rpb24gXHUyMDE0IG9uZSBwZXIgYnJvd3NlciBwYWdlLCBjb3ZlcmluZyBhbGwgcHJvamVjdHMgaW4gdGhlIHdvcmtzcGFjZS4gKi9cbmxldCBfY29ubjogTWVzc2FnZUNvbm5lY3Rpb24gfCBudWxsID0gbnVsbDtcblxuLyoqXG4gKiBWaXJ0dWFsIHdvcmtzcGFjZSByb290IFVSSSwgZS5nLiB7QGNvZGUgZmlsZTovLy93b3Jrc3BhY2Uvd29ya3NwYWNlL30uXG4gKiBTZXQgb25jZSBvbiBmaXJzdCBjb25uZWN0KCk7IHVzZWQgdG8gc2NvcGUgTW9uYWNvIHByb3ZpZGVycyB0byB3b3Jrc3BhY2UgZmlsZXMuXG4gKi9cbmxldCBfd29ya3NwYWNlUm9vdCA9ICcnO1xuXG4vKiogVVJJcyBmb3Igd2hpY2ggdGV4dERvY3VtZW50L2RpZE9wZW4gaGFzIGFscmVhZHkgYmVlbiBzZW50LiAqL1xuY29uc3QgX29wZW5GaWxlczogU2V0PHN0cmluZz4gPSBuZXcgU2V0KCk7XG5cbi8qKiBQZW5kaW5nIGRlYm91bmNlZCBkaWRDaGFuZ2UgdGltZXJzIHBlciBmaWxlIFVSSSwgc28gYSBjaGFuZ2UgY2FuIGJlIGZsdXNoZWQgYmVmb3JlIGNvbXBsZXRpb24uICovXG5jb25zdCBfY2hhbmdlVGltZXJzOiBNYXA8c3RyaW5nLCBSZXR1cm5UeXBlPHR5cGVvZiBzZXRUaW1lb3V0Pj4gPSBuZXcgTWFwKCk7XG5cbi8qKlxuICogTGFzdCBkaWFnbm9zdGljcyBwdWJsaXNoZWQgcGVyIGZpbGUgVVJJLCBrZXB0IHZlcmJhdGltICh3aXRoIHRoZWlyIExTUCBjb2RlL2RhdGEpLiBDb2RlLWFjdGlvblxuICogcmVxdWVzdHMgbXVzdCBzZW5kIHRoZXNlIFx1MjAxNCBKRFQuTFMgbWF0Y2hlcyBxdWljay1maXhlcyBieSB0aGUgZGlhZ25vc3RpYydzIGNvZGUvZGF0YSwgd2hpY2ggTW9uYWNvJ3NcbiAqIElNYXJrZXJEYXRhIGNhbm5vdCByb3VuZC10cmlwLCBzbyByZWNvbnN0cnVjdGluZyBkaWFnbm9zdGljcyBmcm9tIG1hcmtlcnMgeWllbGRzIG5vIHF1aWNrLWZpeGVzLlxuICovXG5jb25zdCBfZGlhZ25vc3RpY3M6IE1hcDxzdHJpbmcsIERpYWdub3N0aWNbXT4gPSBuZXcgTWFwKCk7XG5cbmxldCBfcHJvdmlkZXJzUmVnaXN0ZXJlZCA9IGZhbHNlO1xuXG4vKiogU2VtYW50aWMtdG9rZW4gbGVnZW5kIHJlcG9ydGVkIGJ5IEpEVC5MUyBpbiB0aGUgaW5pdGlhbGl6ZSByZXN1bHQ7IG5lZWRlZCB0byBkZWNvZGUgdG9rZW4gZGF0YS4gKi9cbmxldCBfc2VtYW50aWNUb2tlbnNMZWdlbmQ6IHsgdG9rZW5UeXBlczogc3RyaW5nW107IHRva2VuTW9kaWZpZXJzOiBzdHJpbmdbXSB9IHwgbnVsbCA9IG51bGw7XG5cbi8vIC0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS1cbi8vIFB1YmxpYyBBUElcbi8vIC0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS1cblxuLyoqIENhbGxlZCBieSBlZGl0b3IuanMgd2hlbiBhIEphdmEgZmlsZSBpcyBvcGVuZWQuIFNhZmUgdG8gY2FsbCBtdWx0aXBsZSB0aW1lcy4gKi9cbmV4cG9ydCBhc3luYyBmdW5jdGlvbiBjb25uZWN0KHJlc291cmNlUGF0aDogc3RyaW5nKTogUHJvbWlzZTx2b2lkPiB7XG4gICAgY29uc3QgcGFydHMgPSByZXNvdXJjZVBhdGgucmVwbGFjZSgvXlxcLy8sICcnKS5zcGxpdCgnLycpO1xuICAgIGNvbnN0IHdvcmtzcGFjZSA9IHBhcnRzWzBdO1xuICAgIGNvbnN0IHByb2plY3QgICA9IHBhcnRzWzFdO1xuICAgIGNvbnN0IGZpbGVVcmkgPSBgZmlsZTovLy93b3Jrc3BhY2UvJHt3b3Jrc3BhY2V9LyR7cHJvamVjdH0vJHtwYXJ0cy5zbGljZSgyKS5qb2luKCcvJyl9YDtcblxuICAgIGlmIChfY29ubikge1xuICAgICAgICAvLyBDb25uZWN0aW9uIGFscmVhZHkgZXN0YWJsaXNoZWQgXHUyMDE0IGp1c3Qgb3BlbiB0aGUgbmV3IGZpbGUuXG4gICAgICAgIG9wZW5GaWxlKGZpbGVVcmkpO1xuICAgICAgICByZXR1cm47XG4gICAgfVxuXG4gICAgX3dvcmtzcGFjZVJvb3QgPSBgZmlsZTovLy93b3Jrc3BhY2UvJHt3b3Jrc3BhY2V9L2A7XG5cbiAgICBjb25zdCBwcm90byA9IGxvY2F0aW9uLnByb3RvY29sID09PSAnaHR0cHM6JyA/ICd3c3MnIDogJ3dzJztcbiAgICBjb25zdCB3c1VybCA9IGAke3Byb3RvfTovLyR7bG9jYXRpb24uaG9zdH0vd2Vic29ja2V0cy9pZGUvamF2YS1sc3BgXG4gICAgICAgICAgICAgICAgKyBgP3dvcmtzcGFjZT0ke2VuY29kZVVSSUNvbXBvbmVudCh3b3Jrc3BhY2UpfWA7XG5cbiAgICBjb25zdCB3cyA9IG5ldyBXZWJTb2NrZXQod3NVcmwpO1xuICAgIGF3YWl0IG5ldyBQcm9taXNlPHZvaWQ+KChyZXNvbHZlLCByZWplY3QpID0+IHtcbiAgICAgICAgd3Mub25vcGVuICA9ICgpID0+IHJlc29sdmUoKTtcbiAgICAgICAgd3Mub25lcnJvciA9ICgpID0+IHJlamVjdChuZXcgRXJyb3IoYFtqYXZhLWxzcF0gV2ViU29ja2V0IGNvbm5lY3QgZmFpbGVkOiAke3dzVXJsfWApKTtcbiAgICB9KTtcblxuICAgIGNvbnN0IHNvY2tldCA9IHRvU29ja2V0KHdzKTtcbiAgICBjb25zdCByZWFkZXIgPSBuZXcgV2ViU29ja2V0TWVzc2FnZVJlYWRlcihzb2NrZXQpO1xuICAgIGNvbnN0IHdyaXRlciA9IG5ldyBXZWJTb2NrZXRNZXNzYWdlV3JpdGVyKHNvY2tldCk7XG4gICAgX2Nvbm4gPSBjcmVhdGVNZXNzYWdlQ29ubmVjdGlvbihyZWFkZXIsIHdyaXRlcik7XG5cbiAgICAvLyBEaWFnbm9zdGljcyBub3RpZmljYXRpb24gXHUyMTkyIE1vbmFjbyBtYXJrZXJzIChhcHBsaWVzIHRvIGFueSB3b3Jrc3BhY2UgZmlsZSlcbiAgICBfY29ubi5vbk5vdGlmaWNhdGlvbigndGV4dERvY3VtZW50L3B1Ymxpc2hEaWFnbm9zdGljcycsIChwYXJhbXM6IHsgdXJpOiBzdHJpbmc7IGRpYWdub3N0aWNzOiBEaWFnbm9zdGljW10gfSkgPT4ge1xuICAgICAgICBfZGlhZ25vc3RpY3Muc2V0KHBhcmFtcy51cmksIHBhcmFtcy5kaWFnbm9zdGljcyA/PyBbXSk7XG4gICAgICAgIC8vIE51ZGdlIHRoZSBKYXZhIFByb2JsZW1zIHZpZXcgdG8gcmVmcmVzaCBub3cgKGNvdmVycyBmaWxlcyB0aGF0IGFyZW4ndCBvcGVuIGluIGFuIGVkaXRvciB0b28sXG4gICAgICAgIC8vIGhlbmNlIGJlZm9yZSB0aGUgb3Blbi1tb2RlbCBjaGVjayBiZWxvdykuIERlYm91bmNlZCBvbiB0aGUgZWRpdG9yIHNpZGUuXG4gICAgICAgICh3aW5kb3cgYXMgYW55KS5qYXZhTHNwRGlhZ25vc3RpY3NDaGFuZ2VkPy4oKTtcbiAgICAgICAgY29uc3QgbW9kZWwgPSBtb25hY28uZWRpdG9yLmdldE1vZGVscygpLmZpbmQobSA9PiBtLnVyaS50b1N0cmluZygpID09PSBwYXJhbXMudXJpKTtcbiAgICAgICAgaWYgKCFtb2RlbCkgcmV0dXJuO1xuICAgICAgICBtb25hY28uZWRpdG9yLnNldE1vZGVsTWFya2Vycyhtb2RlbCwgJ2phdmEtbHNwJywgcGFyYW1zLmRpYWdub3N0aWNzLm1hcChkID0+ICh7XG4gICAgICAgICAgICBzZXZlcml0eTogICAgICAgIGxzcFNldmVyaXR5KGQuc2V2ZXJpdHkpLFxuICAgICAgICAgICAgbWVzc2FnZTogICAgICAgICBkLm1lc3NhZ2UsXG4gICAgICAgICAgICBzb3VyY2U6ICAgICAgICAgIGQuc291cmNlID8/ICdqYXZhJyxcbiAgICAgICAgICAgIHN0YXJ0TGluZU51bWJlcjogZC5yYW5nZS5zdGFydC5saW5lICsgMSxcbiAgICAgICAgICAgIHN0YXJ0Q29sdW1uOiAgICAgZC5yYW5nZS5zdGFydC5jaGFyYWN0ZXIgKyAxLFxuICAgICAgICAgICAgZW5kTGluZU51bWJlcjogICBkLnJhbmdlLmVuZC5saW5lICsgMSxcbiAgICAgICAgICAgIGVuZENvbHVtbjogICAgICAgZC5yYW5nZS5lbmQuY2hhcmFjdGVyICsgMSxcbiAgICAgICAgfSkpKTtcbiAgICB9KTtcblxuICAgIC8vIFNlcnZlciAtPiBjbGllbnQgcmVxdWVzdHMgdGhhdCBkcml2ZSByZWZhY3RvciAvIGdlbmVyYXRlIHJlc3VsdHMgYW5kIGR5bmFtaWMgcmVnaXN0cmF0aW9uLlxuICAgIF9jb25uLm9uUmVxdWVzdCgnd29ya3NwYWNlL2FwcGx5RWRpdCcsIChwYXJhbXM6IHsgZWRpdDogV29ya3NwYWNlRWRpdCB9KSA9PiB7XG4gICAgICAgIGFwcGx5V29ya3NwYWNlRWRpdChwYXJhbXMuZWRpdCk7XG4gICAgICAgIHJldHVybiB7IGFwcGxpZWQ6IHRydWUgfTtcbiAgICB9KTtcbiAgICBfY29ubi5vblJlcXVlc3QoJ3dvcmtzcGFjZS9jb25maWd1cmF0aW9uJywgKHBhcmFtczogeyBpdGVtczogQXJyYXk8eyBzZWN0aW9uPzogc3RyaW5nIH0+IH0pID0+XG4gICAgICAgIChwYXJhbXMuaXRlbXMgPz8gW10pLm1hcCgoKSA9PiBqZHRsc1NldHRpbmdzKCkuamF2YSkpO1xuICAgIF9jb25uLm9uUmVxdWVzdCgnY2xpZW50L3JlZ2lzdGVyQ2FwYWJpbGl0eScsICgpID0+IG51bGwpO1xuICAgIF9jb25uLm9uUmVxdWVzdCgnY2xpZW50L3VucmVnaXN0ZXJDYXBhYmlsaXR5JywgKCkgPT4gbnVsbCk7XG4gICAgX2Nvbm4ub25SZXF1ZXN0KCd3aW5kb3cvc2hvd01lc3NhZ2VSZXF1ZXN0JywgKCkgPT4gbnVsbCk7XG4gICAgX2Nvbm4ub25SZXF1ZXN0KCd3aW5kb3cvd29ya0RvbmVQcm9ncmVzcy9jcmVhdGUnLCAoKSA9PiBudWxsKTtcbiAgICBfY29ubi5vbk5vdGlmaWNhdGlvbignd2luZG93L2xvZ01lc3NhZ2UnLCAocDogeyBtZXNzYWdlOiBzdHJpbmcgfSkgPT4gY29uc29sZS5kZWJ1ZygnW2phdmEtbHNwXScsIHA/Lm1lc3NhZ2UpKTtcbiAgICBfY29ubi5vbk5vdGlmaWNhdGlvbignd2luZG93L3Nob3dNZXNzYWdlJywgKHA6IHsgbWVzc2FnZTogc3RyaW5nIH0pID0+IGNvbnNvbGUuaW5mbygnW2phdmEtbHNwXScsIHA/Lm1lc3NhZ2UpKTtcbiAgICAvLyBKRFQuTFMgbGFuZ3VhZ2Utc3RhdHVzIC8gcHJvZ3Jlc3Mgbm90aWZpY2F0aW9ucyBcdTIwMTQgYWNrbm93bGVkZ2VkIHNpbGVudGx5LlxuICAgIF9jb25uLm9uTm90aWZpY2F0aW9uKCdsYW5ndWFnZS9zdGF0dXMnLCAoKSA9PiB7IC8qIGluZGV4aW5nL3JlYWR5IHN0YXR1cywgaWdub3JlZCAqLyB9KTtcbiAgICBfY29ubi5vbk5vdGlmaWNhdGlvbignbGFuZ3VhZ2UvcHJvZ3Jlc3NSZXBvcnQnLCAoKSA9PiB7IC8qIGJ1aWxkIHByb2dyZXNzLCBpZ25vcmVkICovIH0pO1xuXG4gICAgX2Nvbm4ubGlzdGVuKCk7XG5cbiAgICBjb25zdCByb290VXJpID0gX3dvcmtzcGFjZVJvb3Q7XG4gICAgY29uc3QgaW5pdFJlc3VsdDogYW55ID0gYXdhaXQgX2Nvbm4uc2VuZFJlcXVlc3QoJ2luaXRpYWxpemUnLCB7XG4gICAgICAgIHByb2Nlc3NJZDogbnVsbCxcbiAgICAgICAgcm9vdFVyaSxcbiAgICAgICAgaW5pdGlhbGl6YXRpb25PcHRpb25zOiB7XG4gICAgICAgICAgICBzZXR0aW5nczogamR0bHNTZXR0aW5ncygpLFxuICAgICAgICAgICAgZXh0ZW5kZWRDbGllbnRDYXBhYmlsaXRpZXM6IHtcbiAgICAgICAgICAgICAgICBwcm9ncmVzc1JlcG9ydFByb3ZpZGVyOiAgICAgICAgICAgIGZhbHNlLFxuICAgICAgICAgICAgICAgIGNsYXNzRmlsZUNvbnRlbnRzU3VwcG9ydDogICAgICAgICAgdHJ1ZSxcbiAgICAgICAgICAgICAgICByZXNvbHZlQWRkaXRpb25hbFRleHRFZGl0c1N1cHBvcnQ6IHRydWUsXG4gICAgICAgICAgICAgICAgLy8gRG8gTk9UIGFkdmVydGlzZSB0aGUgKlByb21wdFN1cHBvcnQgZmxhZ3M6IHRob3NlIG1ha2UgSkRULkxTIHJldHVybiBzb3VyY2UgYWN0aW9uc1xuICAgICAgICAgICAgICAgIC8vIChnZW5lcmF0ZSB0b1N0cmluZy9jb25zdHJ1Y3RvcnMvYWNjZXNzb3JzLCBvdmVycmlkZS9pbXBsZW1lbnQsIG9yZ2FuaXplIGltcG9ydHMpIGFzXG4gICAgICAgICAgICAgICAgLy8gY2xpZW50LXNpZGUgXCIqUHJvbXB0XCIgY29tbWFuZHMgdGhlIHZzY29kZS1qYXZhIGV4dGVuc2lvbiBpbXBsZW1lbnRzIGJ1dCB3ZSBkb24ndC5cbiAgICAgICAgICAgICAgICAvLyBXaXRoIHRoZW0gb2ZmLCBKRFQuTFMgcmV0dXJucyB0aGUgc2FtZSBhY3Rpb25zIGFzIHJlc29sdmFibGUgV29ya3NwYWNlRWRpdHMgb3BlcmF0aW5nXG4gICAgICAgICAgICAgICAgLy8gb24gYWxsIG1lbWJlcnMsIHdoaWNoIGFwcGx5Q29kZUFjdGlvbiByZXNvbHZlcyBhbmQgYXBwbGllcyBkaXJlY3RseS5cbiAgICAgICAgICAgICAgICBpbmZlclNlbGVjdGlvblN1cHBvcnQ6ICAgICAgICAgICAgIFsnZXh0cmFjdE1ldGhvZCcsICdleHRyYWN0VmFyaWFibGUnLCAnZXh0cmFjdEZpZWxkJ10sXG4gICAgICAgICAgICB9LFxuICAgICAgICB9LFxuICAgICAgICB3b3Jrc3BhY2VGb2xkZXJzOiBbeyB1cmk6IHJvb3RVcmksIG5hbWU6IHdvcmtzcGFjZSB9XSxcbiAgICAgICAgY2FwYWJpbGl0aWVzOiB7XG4gICAgICAgICAgICB0ZXh0RG9jdW1lbnQ6IHtcbiAgICAgICAgICAgICAgICBzeW5jaHJvbml6YXRpb246IHsgZHluYW1pY1JlZ2lzdHJhdGlvbjogdHJ1ZSwgd2lsbFNhdmU6IGZhbHNlLCBkaWRTYXZlOiB0cnVlLCB3aWxsU2F2ZVdhaXRVbnRpbDogZmFsc2UgfSxcbiAgICAgICAgICAgICAgICBjb21wbGV0aW9uOiB7XG4gICAgICAgICAgICAgICAgICAgIGR5bmFtaWNSZWdpc3RyYXRpb246IHRydWUsXG4gICAgICAgICAgICAgICAgICAgIGNvbXBsZXRpb25JdGVtOiB7XG4gICAgICAgICAgICAgICAgICAgICAgICBzbmlwcGV0U3VwcG9ydDogICAgICAgIHRydWUsXG4gICAgICAgICAgICAgICAgICAgICAgICBkb2N1bWVudGF0aW9uRm9ybWF0OiAgIFsnbWFya2Rvd24nLCAncGxhaW50ZXh0J10sXG4gICAgICAgICAgICAgICAgICAgICAgICBkZXByZWNhdGVkU3VwcG9ydDogICAgIHRydWUsXG4gICAgICAgICAgICAgICAgICAgICAgICBjb21taXRDaGFyYWN0ZXJzU3VwcG9ydDogdHJ1ZSxcbiAgICAgICAgICAgICAgICAgICAgICAgIHJlc29sdmVTdXBwb3J0OiAgICAgICAgeyBwcm9wZXJ0aWVzOiBbJ2RvY3VtZW50YXRpb24nLCAnZGV0YWlsJywgJ2FkZGl0aW9uYWxUZXh0RWRpdHMnXSB9LFxuICAgICAgICAgICAgICAgICAgICB9LFxuICAgICAgICAgICAgICAgICAgICBjb250ZXh0U3VwcG9ydDogdHJ1ZSxcbiAgICAgICAgICAgICAgICB9LFxuICAgICAgICAgICAgICAgIGhvdmVyOiAgICAgICAgICB7IGR5bmFtaWNSZWdpc3RyYXRpb246IHRydWUsIGNvbnRlbnRGb3JtYXQ6IFsnbWFya2Rvd24nLCAncGxhaW50ZXh0J10gfSxcbiAgICAgICAgICAgICAgICBzaWduYXR1cmVIZWxwOiAgeyBkeW5hbWljUmVnaXN0cmF0aW9uOiB0cnVlLCBzaWduYXR1cmVJbmZvcm1hdGlvbjogeyBkb2N1bWVudGF0aW9uRm9ybWF0OiBbJ21hcmtkb3duJywgJ3BsYWludGV4dCddLCBwYXJhbWV0ZXJJbmZvcm1hdGlvbjogeyBsYWJlbE9mZnNldFN1cHBvcnQ6IHRydWUgfSB9IH0sXG4gICAgICAgICAgICAgICAgZGVmaW5pdGlvbjogICAgIHsgZHluYW1pY1JlZ2lzdHJhdGlvbjogdHJ1ZSB9LFxuICAgICAgICAgICAgICAgIHJlZmVyZW5jZXM6ICAgICB7IGR5bmFtaWNSZWdpc3RyYXRpb246IHRydWUgfSxcbiAgICAgICAgICAgICAgICBpbXBsZW1lbnRhdGlvbjogeyBkeW5hbWljUmVnaXN0cmF0aW9uOiB0cnVlIH0sXG4gICAgICAgICAgICAgICAgdHlwZURlZmluaXRpb246IHsgZHluYW1pY1JlZ2lzdHJhdGlvbjogdHJ1ZSB9LFxuICAgICAgICAgICAgICAgIC8vIEFkdmVydGlzZWQgc28gSkRULkxTIHNlcnZlcyBjYWxsL3R5cGUgaGllcmFyY2h5LiBUaGUgZWRpdG9yIGRvZXNuJ3QgZHJpdmUgdGhlbVxuICAgICAgICAgICAgICAgIC8vIGRpcmVjdGx5OyB0aGUgc2VydmVyLXNpZGUgUkVTVCBmYWNhZGUgKEphdmFMc3BRdWVyeUVuZHBvaW50KSBkb2VzIFx1MjAxNCBidXQgSkRULkxTIG9ubHlcbiAgICAgICAgICAgICAgICAvLyBleHBvc2VzIHRoZXNlIHByb3ZpZGVycyB3aGVuIHRoZSAqbGFzdCogaW5pdGlhbGl6ZSBhZHZlcnRpc2VkIHRoZW0sIGFuZCBhIGJyb3dzZXJcbiAgICAgICAgICAgICAgICAvLyBlZGl0b3IgcmUtaW5pdGlhbGl6ZXMgdGhlIHNoYXJlZCBwcm9jZXNzLCBzbyB0aGUgZWRpdG9yIG11c3QgYWR2ZXJ0aXNlIHRoZW0gdG9vLlxuICAgICAgICAgICAgICAgIGNhbGxIaWVyYXJjaHk6ICB7IGR5bmFtaWNSZWdpc3RyYXRpb246IHRydWUgfSxcbiAgICAgICAgICAgICAgICB0eXBlSGllcmFyY2h5OiAgeyBkeW5hbWljUmVnaXN0cmF0aW9uOiB0cnVlIH0sXG4gICAgICAgICAgICAgICAgZG9jdW1lbnRIaWdobGlnaHQ6IHsgZHluYW1pY1JlZ2lzdHJhdGlvbjogdHJ1ZSB9LFxuICAgICAgICAgICAgICAgIGRvY3VtZW50U3ltYm9sOiB7IGR5bmFtaWNSZWdpc3RyYXRpb246IHRydWUsIGhpZXJhcmNoaWNhbERvY3VtZW50U3ltYm9sU3VwcG9ydDogdHJ1ZSB9LFxuICAgICAgICAgICAgICAgIGZvbGRpbmdSYW5nZTogICB7IGR5bmFtaWNSZWdpc3RyYXRpb246IHRydWUsIGxpbmVGb2xkaW5nT25seTogZmFsc2UgfSxcbiAgICAgICAgICAgICAgICBzZWxlY3Rpb25SYW5nZTogeyBkeW5hbWljUmVnaXN0cmF0aW9uOiB0cnVlIH0sXG4gICAgICAgICAgICAgICAgY29kZUxlbnM6ICAgICAgIHsgZHluYW1pY1JlZ2lzdHJhdGlvbjogdHJ1ZSB9LFxuICAgICAgICAgICAgICAgIGlubGF5SGludDogICAgICB7IGR5bmFtaWNSZWdpc3RyYXRpb246IHRydWUsIHJlc29sdmVTdXBwb3J0OiB7IHByb3BlcnRpZXM6IFsnbGFiZWwnXSB9IH0sXG4gICAgICAgICAgICAgICAgc2VtYW50aWNUb2tlbnM6IHtcbiAgICAgICAgICAgICAgICAgICAgZHluYW1pY1JlZ2lzdHJhdGlvbjogdHJ1ZSxcbiAgICAgICAgICAgICAgICAgICAgcmVxdWVzdHM6ICAgICAgICB7IHJhbmdlOiBmYWxzZSwgZnVsbDogeyBkZWx0YTogZmFsc2UgfSB9LFxuICAgICAgICAgICAgICAgICAgICB0b2tlblR5cGVzOiAgICAgIFsnbmFtZXNwYWNlJywgJ3R5cGUnLCAnY2xhc3MnLCAnZW51bScsICdpbnRlcmZhY2UnLCAnc3RydWN0JywgJ3R5cGVQYXJhbWV0ZXInLFxuICAgICAgICAgICAgICAgICAgICAgICAgJ3BhcmFtZXRlcicsICd2YXJpYWJsZScsICdwcm9wZXJ0eScsICdlbnVtTWVtYmVyJywgJ2V2ZW50JywgJ2Z1bmN0aW9uJywgJ21ldGhvZCcsICdtYWNybycsXG4gICAgICAgICAgICAgICAgICAgICAgICAna2V5d29yZCcsICdtb2RpZmllcicsICdjb21tZW50JywgJ3N0cmluZycsICdudW1iZXInLCAncmVnZXhwJywgJ29wZXJhdG9yJywgJ2RlY29yYXRvciddLFxuICAgICAgICAgICAgICAgICAgICB0b2tlbk1vZGlmaWVyczogIFsnZGVjbGFyYXRpb24nLCAnZGVmaW5pdGlvbicsICdyZWFkb25seScsICdzdGF0aWMnLCAnZGVwcmVjYXRlZCcsICdhYnN0cmFjdCcsXG4gICAgICAgICAgICAgICAgICAgICAgICAnYXN5bmMnLCAnbW9kaWZpY2F0aW9uJywgJ2RvY3VtZW50YXRpb24nLCAnZGVmYXVsdExpYnJhcnknXSxcbiAgICAgICAgICAgICAgICAgICAgZm9ybWF0czogICAgICAgICBbJ3JlbGF0aXZlJ10sXG4gICAgICAgICAgICAgICAgICAgIG92ZXJsYXBwaW5nVG9rZW5TdXBwb3J0OiBmYWxzZSxcbiAgICAgICAgICAgICAgICAgICAgbXVsdGlsaW5lVG9rZW5TdXBwb3J0OiAgIGZhbHNlLFxuICAgICAgICAgICAgICAgIH0sXG4gICAgICAgICAgICAgICAgZm9ybWF0dGluZzogICAgIHsgZHluYW1pY1JlZ2lzdHJhdGlvbjogdHJ1ZSB9LFxuICAgICAgICAgICAgICAgIHJhbmdlRm9ybWF0dGluZzogeyBkeW5hbWljUmVnaXN0cmF0aW9uOiB0cnVlIH0sXG4gICAgICAgICAgICAgICAgcmVuYW1lOiAgICAgICAgIHsgZHluYW1pY1JlZ2lzdHJhdGlvbjogdHJ1ZSwgcHJlcGFyZVN1cHBvcnQ6IHRydWUgfSxcbiAgICAgICAgICAgICAgICBjb2RlQWN0aW9uOiB7XG4gICAgICAgICAgICAgICAgICAgIGR5bmFtaWNSZWdpc3RyYXRpb246IHRydWUsXG4gICAgICAgICAgICAgICAgICAgIGNvZGVBY3Rpb25MaXRlcmFsU3VwcG9ydDoge1xuICAgICAgICAgICAgICAgICAgICAgICAgY29kZUFjdGlvbktpbmQ6IHtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICB2YWx1ZVNldDogWydxdWlja2ZpeCcsICdyZWZhY3RvcicsICdyZWZhY3Rvci5leHRyYWN0JywgJ3JlZmFjdG9yLmlubGluZScsXG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICdyZWZhY3Rvci5yZXdyaXRlJywgJ3NvdXJjZScsICdzb3VyY2Uub3JnYW5pemVJbXBvcnRzJ10sXG4gICAgICAgICAgICAgICAgICAgICAgICB9LFxuICAgICAgICAgICAgICAgICAgICB9LFxuICAgICAgICAgICAgICAgICAgICBpc1ByZWZlcnJlZFN1cHBvcnQ6IHRydWUsXG4gICAgICAgICAgICAgICAgICAgIGRhdGFTdXBwb3J0OiAgICAgICAgdHJ1ZSxcbiAgICAgICAgICAgICAgICAgICAgcmVzb2x2ZVN1cHBvcnQ6ICAgICB7IHByb3BlcnRpZXM6IFsnZWRpdCddIH0sXG4gICAgICAgICAgICAgICAgfSxcbiAgICAgICAgICAgICAgICBwdWJsaXNoRGlhZ25vc3RpY3M6IHsgcmVsYXRlZEluZm9ybWF0aW9uOiB0cnVlIH0sXG4gICAgICAgICAgICB9LFxuICAgICAgICAgICAgd29ya3NwYWNlOiB7XG4gICAgICAgICAgICAgICAgYXBwbHlFZGl0OiAgICAgICAgICAgICAgdHJ1ZSxcbiAgICAgICAgICAgICAgICBjb25maWd1cmF0aW9uOiAgICAgICAgICB0cnVlLFxuICAgICAgICAgICAgICAgIGV4ZWN1dGVDb21tYW5kOiAgICAgICAgIHsgZHluYW1pY1JlZ2lzdHJhdGlvbjogdHJ1ZSB9LFxuICAgICAgICAgICAgICAgIGRpZENoYW5nZUNvbmZpZ3VyYXRpb246IHsgZHluYW1pY1JlZ2lzdHJhdGlvbjogdHJ1ZSB9LFxuICAgICAgICAgICAgICAgIHdvcmtzcGFjZUVkaXQ6ICAgICAgICAgIHsgZG9jdW1lbnRDaGFuZ2VzOiB0cnVlLCByZXNvdXJjZU9wZXJhdGlvbnM6IFsnY3JlYXRlJywgJ3JlbmFtZScsICdkZWxldGUnXSB9LFxuICAgICAgICAgICAgfSxcbiAgICAgICAgfSxcbiAgICB9KTtcblxuICAgIF9zZW1hbnRpY1Rva2Vuc0xlZ2VuZCA9IGluaXRSZXN1bHQ/LmNhcGFiaWxpdGllcz8uc2VtYW50aWNUb2tlbnNQcm92aWRlcj8ubGVnZW5kID8/IG51bGw7XG5cbiAgICBfY29ubi5zZW5kTm90aWZpY2F0aW9uKCdpbml0aWFsaXplZCcsIHt9KTtcbiAgICBfY29ubi5zZW5kTm90aWZpY2F0aW9uKCd3b3Jrc3BhY2UvZGlkQ2hhbmdlQ29uZmlndXJhdGlvbicsIHsgc2V0dGluZ3M6IGpkdGxzU2V0dGluZ3MoKSB9KTtcblxuICAgIG9wZW5GaWxlKGZpbGVVcmkpO1xuXG4gICAgaWYgKCFfcHJvdmlkZXJzUmVnaXN0ZXJlZCkge1xuICAgICAgICBfcHJvdmlkZXJzUmVnaXN0ZXJlZCA9IHRydWU7XG4gICAgICAgIHJlZ2lzdGVyUHJvdmlkZXJzKCk7XG4gICAgfVxufVxuXG4vLyAtLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tXG4vLyBGaWxlIGxpZmVjeWNsZVxuLy8gLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLVxuXG4vKipcbiAqIFNlbmRzIHRleHREb2N1bWVudC9kaWRPcGVuIGZvciB0aGUgZ2l2ZW4gVVJJIChpZiBub3QgYWxyZWFkeSBzZW50KSBhbmQgcmVnaXN0ZXJzIGEgZGVib3VuY2VkXG4gKiB0ZXh0RG9jdW1lbnQvZGlkQ2hhbmdlIGxpc3RlbmVyIG9uIHRoZSBjb3JyZXNwb25kaW5nIE1vbmFjbyBtb2RlbC5cbiAqL1xuZnVuY3Rpb24gb3BlbkZpbGUoZmlsZVVyaTogc3RyaW5nKTogdm9pZCB7XG4gICAgaWYgKF9vcGVuRmlsZXMuaGFzKGZpbGVVcmkpIHx8ICFfY29ubikgcmV0dXJuO1xuICAgIF9vcGVuRmlsZXMuYWRkKGZpbGVVcmkpO1xuXG4gICAgY29uc3QgbW9kZWwgPSBtb25hY28uZWRpdG9yLmdldE1vZGVscygpLmZpbmQobSA9PiBtLnVyaS50b1N0cmluZygpID09PSBmaWxlVXJpKTtcbiAgICBfY29ubi5zZW5kTm90aWZpY2F0aW9uKCd0ZXh0RG9jdW1lbnQvZGlkT3BlbicsIHtcbiAgICAgICAgdGV4dERvY3VtZW50OiB7XG4gICAgICAgICAgICB1cmk6ICAgICAgICBmaWxlVXJpLFxuICAgICAgICAgICAgbGFuZ3VhZ2VJZDogJ2phdmEnLFxuICAgICAgICAgICAgdmVyc2lvbjogICAgMSxcbiAgICAgICAgICAgIHRleHQ6ICAgICAgIG1vZGVsPy5nZXRWYWx1ZSgpID8/ICcnLFxuICAgICAgICB9LFxuICAgIH0pO1xuICAgIC8vIFRlbGwgSkRULkxTIHRoZSBmaWxlIGV4aXN0cyBvbiBkaXNrIHNvIGl0cyBwcm9qZWN0IG1vZGVsIGluY2x1ZGVzIGl0IGV2ZW4gYmVmb3JlIGEgYnVpbGQgXHUyMDE0IHRoaXNcbiAgICAvLyBtYWtlcyBhIGp1c3QtY3JlYXRlZCB0eXBlIChlLmcuIGEgbmV3IGludGVyZmFjZSkgcmVzb2x2YWJsZS9vZmZlcmVkIGluIHNpYmxpbmcgZmlsZXMgaW1tZWRpYXRlbHkuXG4gICAgX2Nvbm4uc2VuZE5vdGlmaWNhdGlvbignd29ya3NwYWNlL2RpZENoYW5nZVdhdGNoZWRGaWxlcycsIHsgY2hhbmdlczogW3sgdXJpOiBmaWxlVXJpLCB0eXBlOiAxIC8qIENyZWF0ZWQgKi8gfV0gfSk7XG5cbiAgICBpZiAobW9kZWwpIHtcbiAgICAgICAgbW9kZWwub25EaWRDaGFuZ2VDb250ZW50KCgpID0+IHtcbiAgICAgICAgICAgIGNvbnN0IGV4aXN0aW5nID0gX2NoYW5nZVRpbWVycy5nZXQoZmlsZVVyaSk7XG4gICAgICAgICAgICBpZiAoZXhpc3RpbmcpIGNsZWFyVGltZW91dChleGlzdGluZyk7XG4gICAgICAgICAgICBfY2hhbmdlVGltZXJzLnNldChmaWxlVXJpLCBzZXRUaW1lb3V0KCgpID0+IHNlbmREaWRDaGFuZ2UoZmlsZVVyaSksIDQwMCkpO1xuICAgICAgICB9KTtcbiAgICB9XG59XG5cbi8qKiBTZW5kcyB0aGUgY3VycmVudCBtb2RlbCBjb250ZW50IGFzIGEgZGlkQ2hhbmdlIGFuZCBjbGVhcnMgYW55IHBlbmRpbmcgZGVib3VuY2UgZm9yIHRoZSBmaWxlLiAqL1xuZnVuY3Rpb24gc2VuZERpZENoYW5nZShmaWxlVXJpOiBzdHJpbmcpOiB2b2lkIHtcbiAgICBfY2hhbmdlVGltZXJzLmRlbGV0ZShmaWxlVXJpKTtcbiAgICBjb25zdCBtb2RlbCA9IG1vbmFjby5lZGl0b3IuZ2V0TW9kZWwobW9uYWNvLlVyaS5wYXJzZShmaWxlVXJpKSk7XG4gICAgaWYgKF9jb25uICYmIG1vZGVsKSB7XG4gICAgICAgIF9jb25uLnNlbmROb3RpZmljYXRpb24oJ3RleHREb2N1bWVudC9kaWRDaGFuZ2UnLCB7XG4gICAgICAgICAgICB0ZXh0RG9jdW1lbnQ6ICAgeyB1cmk6IGZpbGVVcmksIHZlcnNpb246IG1vZGVsLmdldFZlcnNpb25JZCgpIH0sXG4gICAgICAgICAgICBjb250ZW50Q2hhbmdlczogW3sgdGV4dDogbW9kZWwuZ2V0VmFsdWUoKSB9XSxcbiAgICAgICAgfSk7XG4gICAgfVxufVxuXG4vKipcbiAqIEZsdXNoZXMgYSBwZW5kaW5nIGRlYm91bmNlZCBjaGFuZ2UgaW1tZWRpYXRlbHkuIENhbGxlZCBiZWZvcmUgYSBjb21wbGV0aW9uIHJlcXVlc3Qgc28gSkRULkxTIHNlZXMgdGhlXG4gKiBqdXN0LXR5cGVkIHRleHQgb24gdGhlIGZpcnN0IEN0cmwrU3BhY2UsIGluc3RlYWQgb2YgY29tcGxldGluZyBhZ2FpbnN0IHN0YWxlIGNvbnRlbnQgKHRoZSBkZWJvdW5jZVxuICogb3RoZXJ3aXNlIGRlbGF5cyB0aGUgY2hhbmdlIGJ5IHVwIHRvIDQwMG1zIFx1MjAxNCB0aGUgY2F1c2Ugb2YgXCJmaXJzdCBDdHJsK1NwYWNlIHNob3dzIG5vdGhpbmdcIikuXG4gKi9cbmZ1bmN0aW9uIGZsdXNoUGVuZGluZ0NoYW5nZShmaWxlVXJpOiBzdHJpbmcpOiB2b2lkIHtcbiAgICBpZiAoX2NoYW5nZVRpbWVycy5oYXMoZmlsZVVyaSkpIHtcbiAgICAgICAgY2xlYXJUaW1lb3V0KF9jaGFuZ2VUaW1lcnMuZ2V0KGZpbGVVcmkpISk7XG4gICAgICAgIHNlbmREaWRDaGFuZ2UoZmlsZVVyaSk7XG4gICAgfVxufVxuXG4vLyAtLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tXG4vLyBXb3Jrc3BhY2UgZmlsZSBwcmVkaWNhdGVcbi8vIC0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS1cblxuLyoqXG4gKiBSZXR1cm5zIHtAY29kZSB0cnVlfSB3aGVuIHRoZSBnaXZlbiBtb2RlbCBVUkkgYmVsb25ncyB0byB0aGUgY3VycmVudGx5IGNvbm5lY3RlZCB3b3Jrc3BhY2UuIFVzZWRcbiAqIGJ5IE1vbmFjbyBwcm92aWRlcnMgdG8gc2tpcCBub24tSmF2YS13b3Jrc3BhY2UgbW9kZWxzIHdpdGhvdXQgYW4gZXhhY3QtVVJJIGNvbXBhcmlzb24uXG4gKi9cbmZ1bmN0aW9uIGlzV29ya3NwYWNlRmlsZSh1cmk6IHN0cmluZyk6IGJvb2xlYW4ge1xuICAgIHJldHVybiBfd29ya3NwYWNlUm9vdCAhPT0gJycgJiYgdXJpLnN0YXJ0c1dpdGgoX3dvcmtzcGFjZVJvb3QpO1xufVxuXG4vLyAtLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tXG4vLyBNb25hY28gcHJvdmlkZXIgcmVnaXN0cmF0aW9uXG4vLyAtLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tXG5cbmZ1bmN0aW9uIHJlZ2lzdGVyUHJvdmlkZXJzKCk6IHZvaWQge1xuXG4gICAgbW9uYWNvLmVkaXRvci5yZWdpc3RlckNvbW1hbmQoQVBQTFlfQUNUSU9OX0NPTU1BTkQsIChfYWNjZXNzb3I6IHVua25vd24sIGFjdGlvbjogQ29kZUFjdGlvbiB8IENvbW1hbmQpID0+IHtcbiAgICAgICAgYXBwbHlDb2RlQWN0aW9uKGFjdGlvbik7XG4gICAgfSk7XG4gICAgbW9uYWNvLmVkaXRvci5yZWdpc3RlckNvbW1hbmQoTk9PUF9DT01NQU5ELCAoKSA9PiB7IC8qIGRpc3BsYXktb25seSBDb2RlTGVucyAqLyB9KTtcblxuICAgIC8vIENyb3NzLWZpbGUgbmF2aWdhdGlvbjogdGhpcyBzaW5nbGUtZmlsZSBlZGl0b3IgaGFzIG5vIG1vZGVsIGZvciBvdGhlciB3b3Jrc3BhY2UgZmlsZXMsIHNvIEdvIHRvXG4gICAgLy8gRGVmaW5pdGlvbiAvIEZpbmQgUmVmZXJlbmNlcyB0byBhbm90aGVyIGZpbGUgd291bGQgc2lsZW50bHkgZG8gbm90aGluZy4gSGFuZCB0aG9zZSBvZmYgdG8gdGhlIElERVxuICAgIC8vIHRvIG9wZW4gdGhlIHRhcmdldCBmaWxlIChhbmQgcmV2ZWFsIHRoZSBsaW5lKS4gU2FtZS1maWxlIHRhcmdldHMgZmFsbCB0aHJvdWdoIHRvIE1vbmFjby5cbiAgICBtb25hY28uZWRpdG9yLnJlZ2lzdGVyRWRpdG9yT3BlbmVyKHtcbiAgICAgICAgb3BlbkNvZGVFZGl0b3I6IChzb3VyY2UsIHJlc291cmNlLCBzZWxlY3Rpb25PclBvc2l0aW9uKSA9PiB7XG4gICAgICAgICAgICBjb25zdCB1cmkgPSByZXNvdXJjZS50b1N0cmluZygpO1xuICAgICAgICAgICAgaWYgKCFpc1dvcmtzcGFjZUZpbGUodXJpKSB8fCAhdXJpLnN0YXJ0c1dpdGgoVklSVFVBTF9GSUxFX1BSRUZJWCkpIHJldHVybiBmYWxzZTtcbiAgICAgICAgICAgIGNvbnN0IGN1cnJlbnRNb2RlbCA9IHNvdXJjZS5nZXRNb2RlbCgpO1xuICAgICAgICAgICAgaWYgKGN1cnJlbnRNb2RlbCAmJiBjdXJyZW50TW9kZWwudXJpLnRvU3RyaW5nKCkgPT09IHVyaSkgcmV0dXJuIGZhbHNlOyAvLyBzYW1lIGZpbGUgXHUyMDE0IGxldCBNb25hY28ganVtcFxuICAgICAgICAgICAgY29uc3Qgb3BlbmVyID0gKGdsb2JhbFRoaXMgYXMgYW55KS5qYXZhTHNwT3BlbkZpbGU7XG4gICAgICAgICAgICBpZiAodHlwZW9mIG9wZW5lciAhPT0gJ2Z1bmN0aW9uJykgcmV0dXJuIGZhbHNlO1xuICAgICAgICAgICAgY29uc3QgcG9zID0gc2VsZWN0aW9uT3JQb3NpdGlvbiBhcyB7IHN0YXJ0TGluZU51bWJlcj86IG51bWJlcjsgbGluZU51bWJlcj86IG51bWJlcjsgc3RhcnRDb2x1bW4/OiBudW1iZXI7IGNvbHVtbj86IG51bWJlciB9IHwgdW5kZWZpbmVkO1xuICAgICAgICAgICAgY29uc3QgbGluZSA9IHBvcyA/IChwb3Muc3RhcnRMaW5lTnVtYmVyID8/IHBvcy5saW5lTnVtYmVyKSA6IHVuZGVmaW5lZDtcbiAgICAgICAgICAgIGNvbnN0IGNvbHVtbiA9IHBvcyA/IChwb3Muc3RhcnRDb2x1bW4gPz8gcG9zLmNvbHVtbikgOiB1bmRlZmluZWQ7XG4gICAgICAgICAgICBvcGVuZXIodXJpLnN1YnN0cmluZyhWSVJUVUFMX0ZJTEVfUFJFRklYLmxlbmd0aCksIGxpbmUsIGNvbHVtbik7XG4gICAgICAgICAgICByZXR1cm4gdHJ1ZTtcbiAgICAgICAgfSxcbiAgICB9KTtcblxuICAgIG1vbmFjby5sYW5ndWFnZXMucmVnaXN0ZXJDb21wbGV0aW9uSXRlbVByb3ZpZGVyKCdqYXZhJywge1xuICAgICAgICB0cmlnZ2VyQ2hhcmFjdGVyczogWycuJywgJ0AnLCAnPCddLFxuICAgICAgICBwcm92aWRlQ29tcGxldGlvbkl0ZW1zOiBhc3luYyAobW9kZWwsIHBvc2l0aW9uLCBjb250ZXh0KSA9PiB7XG4gICAgICAgICAgICBpZiAoIV9jb25uIHx8ICFpc1dvcmtzcGFjZUZpbGUobW9kZWwudXJpLnRvU3RyaW5nKCkpKSByZXR1cm4gbnVsbDtcbiAgICAgICAgICAgIGNvbnN0IGZpbGVVcmkgPSBtb2RlbC51cmkudG9TdHJpbmcoKTtcbiAgICAgICAgICAgIC8vIE1ha2Ugc3VyZSBKRFQuTFMgaGFzIHRoZSBqdXN0LXR5cGVkIHRleHQgYmVmb3JlIGNvbXBsZXRpbmcgKHNlZSBmbHVzaFBlbmRpbmdDaGFuZ2UpLlxuICAgICAgICAgICAgZmx1c2hQZW5kaW5nQ2hhbmdlKGZpbGVVcmkpO1xuICAgICAgICAgICAgY29uc3QgcmVzdWx0OiBDb21wbGV0aW9uTGlzdCB8IENvbXBsZXRpb25JdGVtW10gfCBudWxsID0gYXdhaXQgX2Nvbm4uc2VuZFJlcXVlc3QoJ3RleHREb2N1bWVudC9jb21wbGV0aW9uJywge1xuICAgICAgICAgICAgICAgIHRleHREb2N1bWVudDogeyB1cmk6IGZpbGVVcmkgfSxcbiAgICAgICAgICAgICAgICBwb3NpdGlvbjogICAgIHsgbGluZTogcG9zaXRpb24ubGluZU51bWJlciAtIDEsIGNoYXJhY3RlcjogcG9zaXRpb24uY29sdW1uIC0gMSB9LFxuICAgICAgICAgICAgICAgIC8vIE1vbmFjbyB0cmlnZ2VyIGtpbmRzIGFyZSAwLWJhc2VkIChJbnZva2UvVHJpZ2dlckNoYXJhY3Rlci9Gb3JJbmNvbXBsZXRlKTsgTFNQIGlzIDEtYmFzZWQuXG4gICAgICAgICAgICAgICAgY29udGV4dDogICAgICB7IHRyaWdnZXJLaW5kOiAoY29udGV4dC50cmlnZ2VyS2luZCA/PyAwKSArIDEsIHRyaWdnZXJDaGFyYWN0ZXI6IGNvbnRleHQudHJpZ2dlckNoYXJhY3RlciB9LFxuICAgICAgICAgICAgfSk7XG4gICAgICAgICAgICBjb25zdCBpdGVtcyA9IEFycmF5LmlzQXJyYXkocmVzdWx0KSA/IHJlc3VsdCA6IChyZXN1bHQ/Lml0ZW1zID8/IFtdKTtcbiAgICAgICAgICAgIHJldHVybiB7XG4gICAgICAgICAgICAgICAgc3VnZ2VzdGlvbnM6IGl0ZW1zLm1hcChpdGVtID0+IGxzcENvbXBsZXRpb25Ub01vbmFjbyhpdGVtLCBtb2RlbCwgcG9zaXRpb24pKSxcbiAgICAgICAgICAgICAgICAvLyBKRFQuTFMgcmV0dXJucyBhIHRydW5jYXRlZCBsaXN0IG9uIHRoZSBmaXJzdCBrZXlzdHJva2VzOyBwcm9wYWdhdGluZyBcImluY29tcGxldGVcIiBtYWtlc1xuICAgICAgICAgICAgICAgIC8vIE1vbmFjbyByZS1xdWVyeSBhcyB0aGUgdXNlciB0eXBlcyBpbnN0ZWFkIG9mIGNhY2hpbmcgdGhlIGZpcnN0IChvZnRlbiBlbXB0eSkgcmVzdWx0LlxuICAgICAgICAgICAgICAgIGluY29tcGxldGU6ICBBcnJheS5pc0FycmF5KHJlc3VsdCkgPyBmYWxzZSA6ICEhcmVzdWx0Py5pc0luY29tcGxldGUsXG4gICAgICAgICAgICB9O1xuICAgICAgICB9LFxuICAgICAgICAvLyBSZXNvbHZlIGRvY3VtZW50YXRpb24gYW5kLCBjcnVjaWFsbHksIHRoZSBhdXRvLWltcG9ydCBhZGRpdGlvbmFsVGV4dEVkaXRzIHdoaWNoIEpEVC5MUyBvbmx5XG4gICAgICAgIC8vIGF0dGFjaGVzIG9uIHJlc29sdmUgXHUyMDE0IHNlbGVjdGluZyBhIHR5cGUgdGhlbiBpbnNlcnRzIGl0cyBpbXBvcnQgc3RhdGVtZW50LlxuICAgICAgICByZXNvbHZlQ29tcGxldGlvbkl0ZW06IGFzeW5jIChpdGVtKSA9PiB7XG4gICAgICAgICAgICBjb25zdCBsc3AgPSAoaXRlbSBhcyBNb25hY29Db21wbGV0aW9uSXRlbSkuX2xzcDtcbiAgICAgICAgICAgIGlmICghX2Nvbm4gfHwgIWxzcCkgcmV0dXJuIGl0ZW07XG4gICAgICAgICAgICB0cnkge1xuICAgICAgICAgICAgICAgIGNvbnN0IHJlc29sdmVkOiBDb21wbGV0aW9uSXRlbSA9IGF3YWl0IF9jb25uLnNlbmRSZXF1ZXN0KCdjb21wbGV0aW9uSXRlbS9yZXNvbHZlJywgbHNwKTtcbiAgICAgICAgICAgICAgICBpZiAocmVzb2x2ZWQuZG9jdW1lbnRhdGlvbikge1xuICAgICAgICAgICAgICAgICAgICBpdGVtLmRvY3VtZW50YXRpb24gPSB7IHZhbHVlOiBtYXJrdXBUb1N0cmluZyhyZXNvbHZlZC5kb2N1bWVudGF0aW9uKSwgaXNUcnVzdGVkOiBmYWxzZSB9O1xuICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICBpZiAocmVzb2x2ZWQuZGV0YWlsKSBpdGVtLmRldGFpbCA9IHJlc29sdmVkLmRldGFpbDtcbiAgICAgICAgICAgICAgICBpZiAocmVzb2x2ZWQuYWRkaXRpb25hbFRleHRFZGl0cz8ubGVuZ3RoKSB7XG4gICAgICAgICAgICAgICAgICAgIGl0ZW0uYWRkaXRpb25hbFRleHRFZGl0cyA9IHJlc29sdmVkLmFkZGl0aW9uYWxUZXh0RWRpdHMubWFwKHRleHRFZGl0VG9Nb25hY28pO1xuICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgIH0gY2F0Y2ggKGUpIHtcbiAgICAgICAgICAgICAgICBjb25zb2xlLmRlYnVnKCdbamF2YS1sc3BdIGNvbXBsZXRpb24gcmVzb2x2ZSBmYWlsZWQ6JywgKGUgYXMgRXJyb3IpPy5tZXNzYWdlKTtcbiAgICAgICAgICAgIH1cbiAgICAgICAgICAgIHJldHVybiBpdGVtO1xuICAgICAgICB9LFxuICAgIH0pO1xuXG4gICAgbW9uYWNvLmxhbmd1YWdlcy5yZWdpc3RlckhvdmVyUHJvdmlkZXIoJ2phdmEnLCB7XG4gICAgICAgIHByb3ZpZGVIb3ZlcjogYXN5bmMgKG1vZGVsLCBwb3NpdGlvbikgPT4ge1xuICAgICAgICAgICAgaWYgKCFfY29ubiB8fCAhaXNXb3Jrc3BhY2VGaWxlKG1vZGVsLnVyaS50b1N0cmluZygpKSkgcmV0dXJuIG51bGw7XG4gICAgICAgICAgICBjb25zdCBmaWxlVXJpID0gbW9kZWwudXJpLnRvU3RyaW5nKCk7XG4gICAgICAgICAgICBjb25zdCByZXN1bHQ6IEhvdmVyIHwgbnVsbCA9IGF3YWl0IF9jb25uLnNlbmRSZXF1ZXN0KCd0ZXh0RG9jdW1lbnQvaG92ZXInLCB7XG4gICAgICAgICAgICAgICAgdGV4dERvY3VtZW50OiB7IHVyaTogZmlsZVVyaSB9LFxuICAgICAgICAgICAgICAgIHBvc2l0aW9uOiAgICAgeyBsaW5lOiBwb3NpdGlvbi5saW5lTnVtYmVyIC0gMSwgY2hhcmFjdGVyOiBwb3NpdGlvbi5jb2x1bW4gLSAxIH0sXG4gICAgICAgICAgICB9KTtcbiAgICAgICAgICAgIGlmICghcmVzdWx0Py5jb250ZW50cykgcmV0dXJuIG51bGw7XG4gICAgICAgICAgICBjb25zdCBjb250ZW50cyA9IEFycmF5LmlzQXJyYXkocmVzdWx0LmNvbnRlbnRzKSA/IHJlc3VsdC5jb250ZW50cyA6IFtyZXN1bHQuY29udGVudHNdO1xuICAgICAgICAgICAgcmV0dXJuIHtcbiAgICAgICAgICAgICAgICBjb250ZW50czogY29udGVudHMubWFwKGMgPT4gKHtcbiAgICAgICAgICAgICAgICAgICAgdmFsdWU6IHR5cGVvZiBjID09PSAnc3RyaW5nJyA/IGMgOiAoYyBhcyBNYXJrdXBDb250ZW50KS52YWx1ZSxcbiAgICAgICAgICAgICAgICAgICAgaXNUcnVzdGVkOiBmYWxzZSxcbiAgICAgICAgICAgICAgICB9KSksXG4gICAgICAgICAgICAgICAgcmFuZ2U6IHJlc3VsdC5yYW5nZSA/IGxzcFJhbmdlVG9Nb25hY28ocmVzdWx0LnJhbmdlKSA6IHVuZGVmaW5lZCxcbiAgICAgICAgICAgIH07XG4gICAgICAgIH0sXG4gICAgfSk7XG5cbiAgICBtb25hY28ubGFuZ3VhZ2VzLnJlZ2lzdGVyU2lnbmF0dXJlSGVscFByb3ZpZGVyKCdqYXZhJywge1xuICAgICAgICBzaWduYXR1cmVIZWxwVHJpZ2dlckNoYXJhY3RlcnM6IFsnKCcsICcsJ10sXG4gICAgICAgIHByb3ZpZGVTaWduYXR1cmVIZWxwOiBhc3luYyAobW9kZWwsIHBvc2l0aW9uKSA9PiB7XG4gICAgICAgICAgICBpZiAoIV9jb25uIHx8ICFpc1dvcmtzcGFjZUZpbGUobW9kZWwudXJpLnRvU3RyaW5nKCkpKSByZXR1cm4gbnVsbDtcbiAgICAgICAgICAgIGNvbnN0IGZpbGVVcmkgPSBtb2RlbC51cmkudG9TdHJpbmcoKTtcbiAgICAgICAgICAgIGNvbnN0IHJlc3VsdDogU2lnbmF0dXJlSGVscCB8IG51bGwgPSBhd2FpdCBfY29ubi5zZW5kUmVxdWVzdCgndGV4dERvY3VtZW50L3NpZ25hdHVyZUhlbHAnLCB7XG4gICAgICAgICAgICAgICAgdGV4dERvY3VtZW50OiB7IHVyaTogZmlsZVVyaSB9LFxuICAgICAgICAgICAgICAgIHBvc2l0aW9uOiAgICAgeyBsaW5lOiBwb3NpdGlvbi5saW5lTnVtYmVyIC0gMSwgY2hhcmFjdGVyOiBwb3NpdGlvbi5jb2x1bW4gLSAxIH0sXG4gICAgICAgICAgICB9KTtcbiAgICAgICAgICAgIGlmICghcmVzdWx0KSByZXR1cm4gbnVsbDtcbiAgICAgICAgICAgIHJldHVybiB7XG4gICAgICAgICAgICAgICAgdmFsdWU6IHtcbiAgICAgICAgICAgICAgICAgICAgc2lnbmF0dXJlczogcmVzdWx0LnNpZ25hdHVyZXMubWFwKChzaWc6IFNpZ25hdHVyZUluZm9ybWF0aW9uKSA9PiAoe1xuICAgICAgICAgICAgICAgICAgICAgICAgbGFiZWw6ICAgICAgICAgc2lnLmxhYmVsLFxuICAgICAgICAgICAgICAgICAgICAgICAgZG9jdW1lbnRhdGlvbjogc2lnLmRvY3VtZW50YXRpb24gPyBtYXJrdXBUb1N0cmluZyhzaWcuZG9jdW1lbnRhdGlvbikgOiB1bmRlZmluZWQsXG4gICAgICAgICAgICAgICAgICAgICAgICBwYXJhbWV0ZXJzOiAgICAoc2lnLnBhcmFtZXRlcnMgPz8gW10pLm1hcCgocDogUGFyYW1ldGVySW5mb3JtYXRpb24pID0+ICh7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgbGFiZWw6ICAgICAgICAgcC5sYWJlbCxcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICBkb2N1bWVudGF0aW9uOiBwLmRvY3VtZW50YXRpb24gPyBtYXJrdXBUb1N0cmluZyhwLmRvY3VtZW50YXRpb24pIDogdW5kZWZpbmVkLFxuICAgICAgICAgICAgICAgICAgICAgICAgfSkpLFxuICAgICAgICAgICAgICAgICAgICB9KSksXG4gICAgICAgICAgICAgICAgICAgIGFjdGl2ZVNpZ25hdHVyZTogcmVzdWx0LmFjdGl2ZVNpZ25hdHVyZSA/PyAwLFxuICAgICAgICAgICAgICAgICAgICBhY3RpdmVQYXJhbWV0ZXI6IHJlc3VsdC5hY3RpdmVQYXJhbWV0ZXIgPz8gMCxcbiAgICAgICAgICAgICAgICB9LFxuICAgICAgICAgICAgICAgIGRpc3Bvc2U6ICgpID0+IHt9LFxuICAgICAgICAgICAgfTtcbiAgICAgICAgfSxcbiAgICB9KTtcblxuICAgIG1vbmFjby5sYW5ndWFnZXMucmVnaXN0ZXJEZWZpbml0aW9uUHJvdmlkZXIoJ2phdmEnLCB7XG4gICAgICAgIHByb3ZpZGVEZWZpbml0aW9uOiBhc3luYyAobW9kZWwsIHBvc2l0aW9uKSA9PiB7XG4gICAgICAgICAgICBpZiAoIV9jb25uIHx8ICFpc1dvcmtzcGFjZUZpbGUobW9kZWwudXJpLnRvU3RyaW5nKCkpKSByZXR1cm4gbnVsbDtcbiAgICAgICAgICAgIGNvbnN0IGZpbGVVcmkgPSBtb2RlbC51cmkudG9TdHJpbmcoKTtcbiAgICAgICAgICAgIGNvbnN0IHJlc3VsdDogTG9jYXRpb24gfCBMb2NhdGlvbltdIHwgbnVsbCA9IGF3YWl0IF9jb25uLnNlbmRSZXF1ZXN0KCd0ZXh0RG9jdW1lbnQvZGVmaW5pdGlvbicsIHtcbiAgICAgICAgICAgICAgICB0ZXh0RG9jdW1lbnQ6IHsgdXJpOiBmaWxlVXJpIH0sXG4gICAgICAgICAgICAgICAgcG9zaXRpb246ICAgICB7IGxpbmU6IHBvc2l0aW9uLmxpbmVOdW1iZXIgLSAxLCBjaGFyYWN0ZXI6IHBvc2l0aW9uLmNvbHVtbiAtIDEgfSxcbiAgICAgICAgICAgIH0pO1xuICAgICAgICAgICAgaWYgKCFyZXN1bHQpIHJldHVybiBudWxsO1xuICAgICAgICAgICAgY29uc3QgbG9jYXRpb25zID0gKEFycmF5LmlzQXJyYXkocmVzdWx0KSA/IHJlc3VsdCA6IFtyZXN1bHRdKS5tYXAobG9jID0+ICh7XG4gICAgICAgICAgICAgICAgdXJpOiAgIG1vbmFjby5VcmkucGFyc2UobG9jLnVyaSksXG4gICAgICAgICAgICAgICAgcmFuZ2U6IGxzcFJhbmdlVG9Nb25hY28obG9jLnJhbmdlKSxcbiAgICAgICAgICAgIH0pKTtcbiAgICAgICAgICAgIGF3YWl0IGVuc3VyZU1vZGVsc0ZvckxvY2F0aW9ucyhsb2NhdGlvbnMpO1xuICAgICAgICAgICAgcmV0dXJuIGxvY2F0aW9ucztcbiAgICAgICAgfSxcbiAgICB9KTtcblxuICAgIG1vbmFjby5sYW5ndWFnZXMucmVnaXN0ZXJSZWZlcmVuY2VQcm92aWRlcignamF2YScsIHtcbiAgICAgICAgcHJvdmlkZVJlZmVyZW5jZXM6IGFzeW5jIChtb2RlbCwgcG9zaXRpb24sIGNvbnRleHQpID0+IHtcbiAgICAgICAgICAgIGlmICghX2Nvbm4gfHwgIWlzV29ya3NwYWNlRmlsZShtb2RlbC51cmkudG9TdHJpbmcoKSkpIHJldHVybiBudWxsO1xuICAgICAgICAgICAgY29uc3QgcmVzdWx0OiBMb2NhdGlvbltdIHwgbnVsbCA9IGF3YWl0IF9jb25uLnNlbmRSZXF1ZXN0KCd0ZXh0RG9jdW1lbnQvcmVmZXJlbmNlcycsIHtcbiAgICAgICAgICAgICAgICB0ZXh0RG9jdW1lbnQ6IHsgdXJpOiBtb2RlbC51cmkudG9TdHJpbmcoKSB9LFxuICAgICAgICAgICAgICAgIHBvc2l0aW9uOiAgICAgeyBsaW5lOiBwb3NpdGlvbi5saW5lTnVtYmVyIC0gMSwgY2hhcmFjdGVyOiBwb3NpdGlvbi5jb2x1bW4gLSAxIH0sXG4gICAgICAgICAgICAgICAgY29udGV4dDogICAgICB7IGluY2x1ZGVEZWNsYXJhdGlvbjogY29udGV4dC5pbmNsdWRlRGVjbGFyYXRpb24gfSxcbiAgICAgICAgICAgIH0pO1xuICAgICAgICAgICAgaWYgKCFyZXN1bHQpIHJldHVybiBudWxsO1xuICAgICAgICAgICAgY29uc3QgbG9jYXRpb25zID0gcmVzdWx0Lm1hcChsb2MgPT4gKHsgdXJpOiBtb25hY28uVXJpLnBhcnNlKGxvYy51cmkpLCByYW5nZTogbHNwUmFuZ2VUb01vbmFjbyhsb2MucmFuZ2UpIH0pKTtcbiAgICAgICAgICAgIC8vIFRoZSByZWZlcmVuY2VzIHBlZWsgY2FuIG9ubHkgc2hvdyBhIGNvZGUgcHJldmlldyBmb3IgZmlsZXMgaXQgaGFzIGEgbW9kZWwgZm9yOyBjcmVhdGVcbiAgICAgICAgICAgIC8vIGluLW1lbW9yeSBtb2RlbHMgZm9yIHRoZSByZWZlcmVuY2VkIChwb3NzaWJseSB1bm9wZW5lZCkgZmlsZXMgc28gcHJldmlld3MgcmVuZGVyLlxuICAgICAgICAgICAgYXdhaXQgZW5zdXJlTW9kZWxzRm9yTG9jYXRpb25zKGxvY2F0aW9ucyk7XG4gICAgICAgICAgICByZXR1cm4gbG9jYXRpb25zO1xuICAgICAgICB9LFxuICAgIH0pO1xuXG4gICAgbW9uYWNvLmxhbmd1YWdlcy5yZWdpc3RlclJlbmFtZVByb3ZpZGVyKCdqYXZhJywge1xuICAgICAgICBwcm92aWRlUmVuYW1lRWRpdHM6IGFzeW5jIChtb2RlbCwgcG9zaXRpb24sIG5ld05hbWUpID0+IHtcbiAgICAgICAgICAgIGlmICghX2Nvbm4gfHwgIWlzV29ya3NwYWNlRmlsZShtb2RlbC51cmkudG9TdHJpbmcoKSkpIHJldHVybiB7IGVkaXRzOiBbXSB9O1xuICAgICAgICAgICAgY29uc3QgZWRpdDogV29ya3NwYWNlRWRpdCB8IG51bGwgPSBhd2FpdCBfY29ubi5zZW5kUmVxdWVzdCgndGV4dERvY3VtZW50L3JlbmFtZScsIHtcbiAgICAgICAgICAgICAgICB0ZXh0RG9jdW1lbnQ6IHsgdXJpOiBtb2RlbC51cmkudG9TdHJpbmcoKSB9LFxuICAgICAgICAgICAgICAgIHBvc2l0aW9uOiAgICAgeyBsaW5lOiBwb3NpdGlvbi5saW5lTnVtYmVyIC0gMSwgY2hhcmFjdGVyOiBwb3NpdGlvbi5jb2x1bW4gLSAxIH0sXG4gICAgICAgICAgICAgICAgbmV3TmFtZSxcbiAgICAgICAgICAgIH0pO1xuICAgICAgICAgICAgaWYgKCFlZGl0KSByZXR1cm4geyBlZGl0czogW10gfTtcbiAgICAgICAgICAgIC8vIEEgSkRULkxTIHJlbmFtZSBjYW4gc3BhbiBtYW55IGZpbGVzIGFuZCBldmVuIHJlbmFtZSB0aGUgdHlwZSdzIG93biAuamF2YSBmaWxlLiBNb25hY28nc1xuICAgICAgICAgICAgLy8gc2luZ2xlLWZpbGUgZWRpdG9yIHdvdWxkIGRyb3AgZXZlcnl0aGluZyBidXQgdGhlIGN1cnJlbnQgbW9kZWwsIHNvIGFwcGx5IHRoZSB3aG9sZSBlZGl0XG4gICAgICAgICAgICAvLyB0aHJvdWdoIHRoZSB3b3Jrc3BhY2Ugb3Vyc2VsdmVzIHdoZW4gdGhlIElERSBwZXJzaXN0ZW5jZSBob29rIGlzIGF2YWlsYWJsZS5cbiAgICAgICAgICAgIGlmICh0eXBlb2YgKGdsb2JhbFRoaXMgYXMgYW55KS5qYXZhTHNwUGVyc2lzdFJlbmFtZSA9PT0gJ2Z1bmN0aW9uJykge1xuICAgICAgICAgICAgICAgIHRyeSB7XG4gICAgICAgICAgICAgICAgICAgIGF3YWl0IGFwcGx5UmVuYW1lQWNyb3NzV29ya3NwYWNlKG1vZGVsLCBlZGl0KTtcbiAgICAgICAgICAgICAgICAgICAgcmV0dXJuIHsgZWRpdHM6IFtdIH07XG4gICAgICAgICAgICAgICAgfSBjYXRjaCAoZSkge1xuICAgICAgICAgICAgICAgICAgICBjb25zb2xlLmVycm9yKCdbamF2YS1sc3BdIGNyb3NzLWZpbGUgcmVuYW1lIGZhaWxlZCwgYXBwbHlpbmcgdG8gdGhlIGN1cnJlbnQgZmlsZSBvbmx5OicsIGUpO1xuICAgICAgICAgICAgICAgICAgICByZXR1cm4gd29ya3NwYWNlRWRpdFRvTW9uYWNvKGVkaXQpO1xuICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgIH1cbiAgICAgICAgICAgIHJldHVybiB3b3Jrc3BhY2VFZGl0VG9Nb25hY28oZWRpdCk7XG4gICAgICAgIH0sXG4gICAgICAgIHJlc29sdmVSZW5hbWVMb2NhdGlvbjogYXN5bmMgKG1vZGVsLCBwb3NpdGlvbikgPT4ge1xuICAgICAgICAgICAgaWYgKCFfY29ubiB8fCAhaXNXb3Jrc3BhY2VGaWxlKG1vZGVsLnVyaS50b1N0cmluZygpKSkgcmV0dXJuIG51bGw7XG4gICAgICAgICAgICB0cnkge1xuICAgICAgICAgICAgICAgIGNvbnN0IHJlc3VsdDogeyByYW5nZTogTHNwUmFuZ2U7IHBsYWNlaG9sZGVyPzogc3RyaW5nIH0gfCBMc3BSYW5nZSB8IG51bGwgPVxuICAgICAgICAgICAgICAgICAgICBhd2FpdCBfY29ubi5zZW5kUmVxdWVzdCgndGV4dERvY3VtZW50L3ByZXBhcmVSZW5hbWUnLCB7XG4gICAgICAgICAgICAgICAgICAgICAgICB0ZXh0RG9jdW1lbnQ6IHsgdXJpOiBtb2RlbC51cmkudG9TdHJpbmcoKSB9LFxuICAgICAgICAgICAgICAgICAgICAgICAgcG9zaXRpb246ICAgICB7IGxpbmU6IHBvc2l0aW9uLmxpbmVOdW1iZXIgLSAxLCBjaGFyYWN0ZXI6IHBvc2l0aW9uLmNvbHVtbiAtIDEgfSxcbiAgICAgICAgICAgICAgICAgICAgfSk7XG4gICAgICAgICAgICAgICAgaWYgKCFyZXN1bHQpIHJldHVybiBudWxsO1xuICAgICAgICAgICAgICAgIGNvbnN0IHJhbmdlID0gJ3JhbmdlJyBpbiByZXN1bHQgPyByZXN1bHQucmFuZ2UgOiByZXN1bHQ7XG4gICAgICAgICAgICAgICAgY29uc3QgcGxhY2Vob2xkZXIgPSAncGxhY2Vob2xkZXInIGluIHJlc3VsdCAmJiByZXN1bHQucGxhY2Vob2xkZXJcbiAgICAgICAgICAgICAgICAgICAgPyByZXN1bHQucGxhY2Vob2xkZXJcbiAgICAgICAgICAgICAgICAgICAgOiBtb2RlbC5nZXRXb3JkQXRQb3NpdGlvbihwb3NpdGlvbik/LndvcmQgPz8gJyc7XG4gICAgICAgICAgICAgICAgcmV0dXJuIHsgcmFuZ2U6IGxzcFJhbmdlVG9Nb25hY28ocmFuZ2UpLCB0ZXh0OiBwbGFjZWhvbGRlciB9O1xuICAgICAgICAgICAgfSBjYXRjaCB7XG4gICAgICAgICAgICAgICAgY29uc3Qgd29yZCA9IG1vZGVsLmdldFdvcmRBdFBvc2l0aW9uKHBvc2l0aW9uKTtcbiAgICAgICAgICAgICAgICByZXR1cm4gd29yZCA/IHtcbiAgICAgICAgICAgICAgICAgICAgcmFuZ2U6IHsgc3RhcnRMaW5lTnVtYmVyOiBwb3NpdGlvbi5saW5lTnVtYmVyLCBzdGFydENvbHVtbjogd29yZC5zdGFydENvbHVtbiwgZW5kTGluZU51bWJlcjogcG9zaXRpb24ubGluZU51bWJlciwgZW5kQ29sdW1uOiB3b3JkLmVuZENvbHVtbiB9LFxuICAgICAgICAgICAgICAgICAgICB0ZXh0OiB3b3JkLndvcmQsXG4gICAgICAgICAgICAgICAgfSA6IG51bGw7XG4gICAgICAgICAgICB9XG4gICAgICAgIH0sXG4gICAgfSk7XG5cbiAgICAvLyBSZWdpc3RlcmluZyB0aGlzIHByb3ZpZGVyIGFsc28gZW5hYmxlcyBlZGl0b3IuanMncyBleGlzdGluZyBmb3JtYXQtb24tc2F2ZSBwYXRoIGZvciBKYXZhOiB0aGVcbiAgICAvLyBzaGFyZWQgU2F2ZSBhY3Rpb24gcnVucyBlZGl0b3IuYWN0aW9uLmZvcm1hdERvY3VtZW50IHdoZW4gYXV0by1mb3JtYXR0aW5nIGlzIG9uICh0aGUgc2FtZVxuICAgIC8vIG1lY2hhbmlzbSBhbmQgZ2xvYmFsIHRvZ2dsZSB1c2VkIGZvciBUeXBlU2NyaXB0KS5cbiAgICBtb25hY28ubGFuZ3VhZ2VzLnJlZ2lzdGVyRG9jdW1lbnRGb3JtYXR0aW5nRWRpdFByb3ZpZGVyKCdqYXZhJywge1xuICAgICAgICBwcm92aWRlRG9jdW1lbnRGb3JtYXR0aW5nRWRpdHM6IGFzeW5jIChtb2RlbCkgPT4ge1xuICAgICAgICAgICAgaWYgKCFfY29ubiB8fCAhaXNXb3Jrc3BhY2VGaWxlKG1vZGVsLnVyaS50b1N0cmluZygpKSkgcmV0dXJuIG51bGw7XG4gICAgICAgICAgICBjb25zdCBlZGl0czogVGV4dEVkaXRbXSB8IG51bGwgPSBhd2FpdCBfY29ubi5zZW5kUmVxdWVzdCgndGV4dERvY3VtZW50L2Zvcm1hdHRpbmcnLCB7XG4gICAgICAgICAgICAgICAgdGV4dERvY3VtZW50OiB7IHVyaTogbW9kZWwudXJpLnRvU3RyaW5nKCkgfSxcbiAgICAgICAgICAgICAgICBvcHRpb25zOiAgICAgIHsgdGFiU2l6ZTogbW9kZWwuZ2V0T3B0aW9ucygpLnRhYlNpemUsIGluc2VydFNwYWNlczogbW9kZWwuZ2V0T3B0aW9ucygpLmluc2VydFNwYWNlcyB9LFxuICAgICAgICAgICAgfSk7XG4gICAgICAgICAgICByZXR1cm4gZWRpdHMgPyBlZGl0cy5tYXAodGV4dEVkaXRUb01vbmFjbykgOiBudWxsO1xuICAgICAgICB9LFxuICAgIH0pO1xuXG4gICAgbW9uYWNvLmxhbmd1YWdlcy5yZWdpc3RlckNvZGVBY3Rpb25Qcm92aWRlcignamF2YScsIHtcbiAgICAgICAgcHJvdmlkZUNvZGVBY3Rpb25zOiBhc3luYyAobW9kZWwsIHJhbmdlLCBjb250ZXh0KSA9PiB7XG4gICAgICAgICAgICBjb25zdCBlbXB0eSA9IHsgYWN0aW9uczogW10sIGRpc3Bvc2UoKSB7IC8qIG5vdGhpbmcgdG8gZGlzcG9zZSAqLyB9IH07XG4gICAgICAgICAgICBpZiAoIV9jb25uIHx8ICFpc1dvcmtzcGFjZUZpbGUobW9kZWwudXJpLnRvU3RyaW5nKCkpKSByZXR1cm4gZW1wdHk7XG4gICAgICAgICAgICAvLyBTZW5kIHRoZSBvcmlnaW5hbCBMU1AgZGlhZ25vc3RpY3MgdGhhdCBvdmVybGFwIHRoZSByYW5nZSAodGhleSBjYXJyeSB0aGUgY29kZS9kYXRhIEpEVC5MU1xuICAgICAgICAgICAgLy8gbmVlZHMgdG8gY29tcHV0ZSBxdWljay1maXhlcyksIG5vdCBvbmVzIHJlY29uc3RydWN0ZWQgZnJvbSBNb25hY28gbWFya2Vycy5cbiAgICAgICAgICAgIGNvbnN0IGxzcFJhbmdlID0gbW9uYWNvUmFuZ2VUb0xzcChyYW5nZSk7XG4gICAgICAgICAgICBjb25zdCBkaWFnbm9zdGljcyA9IChfZGlhZ25vc3RpY3MuZ2V0KG1vZGVsLnVyaS50b1N0cmluZygpKSA/PyBbXSkuZmlsdGVyKGQgPT4gcmFuZ2VzT3ZlcmxhcChkLnJhbmdlLCBsc3BSYW5nZSkpO1xuICAgICAgICAgICAgY29uc3QgcmVzdWx0OiBBcnJheTxDb2RlQWN0aW9uIHwgQ29tbWFuZD4gfCBudWxsID0gYXdhaXQgX2Nvbm4uc2VuZFJlcXVlc3QoJ3RleHREb2N1bWVudC9jb2RlQWN0aW9uJywge1xuICAgICAgICAgICAgICAgIHRleHREb2N1bWVudDogeyB1cmk6IG1vZGVsLnVyaS50b1N0cmluZygpIH0sXG4gICAgICAgICAgICAgICAgcmFuZ2U6ICAgICAgICBsc3BSYW5nZSxcbiAgICAgICAgICAgICAgICAvLyBNb25hY28ncyBDb2RlQWN0aW9uVHJpZ2dlclR5cGUgKEludm9rZT0xLCBBdXRvPTIpIG1hcHMgMToxIHRvIHRoZSBMU1AgdHJpZ2dlciBraW5kLlxuICAgICAgICAgICAgICAgIC8vIEZvcndhcmRpbmcgaXQgbGV0cyBKRFQuTFMgY29tcHV0ZSBvbmx5IHF1aWNrLWZpeGVzIGZvciB0aGUgcGFzc2l2ZSBsaWdodGJ1bGIgKGNoZWFwKVxuICAgICAgICAgICAgICAgIC8vIGFuZCB0aGUgZnVsbCBhc3Npc3RzL3JlZmFjdG9yaW5ncyBvbmx5IG9uIGV4cGxpY2l0IEN0cmwrLiAvIFJlZmFjdG9yXHUyMDI2IChJbnZva2VkKS5cbiAgICAgICAgICAgICAgICBjb250ZXh0OiAgICAgIHsgZGlhZ25vc3RpY3MsIG9ubHk6IGNvbnRleHQub25seSA/IFtjb250ZXh0Lm9ubHldIDogdW5kZWZpbmVkLCB0cmlnZ2VyS2luZDogY29udGV4dC50cmlnZ2VyIH0sXG4gICAgICAgICAgICB9KTtcbiAgICAgICAgICAgIGlmICghcmVzdWx0Py5sZW5ndGgpIHJldHVybiBlbXB0eTtcbiAgICAgICAgICAgIHJldHVybiB7XG4gICAgICAgICAgICAgICAgYWN0aW9uczogcmVzdWx0Lm1hcChsc3BDb2RlQWN0aW9uVG9Nb25hY28pLFxuICAgICAgICAgICAgICAgIGRpc3Bvc2UoKSB7IC8qIG5vdGhpbmcgdG8gZGlzcG9zZSAqLyB9LFxuICAgICAgICAgICAgfTtcbiAgICAgICAgfSxcbiAgICB9LCB7XG4gICAgICAgIHByb3ZpZGVkQ29kZUFjdGlvbktpbmRzOiBbJ3F1aWNrZml4JywgJ3JlZmFjdG9yJywgJ3JlZmFjdG9yLmV4dHJhY3QnLCAncmVmYWN0b3IuaW5saW5lJyxcbiAgICAgICAgICAgICdyZWZhY3Rvci5yZXdyaXRlJywgJ3NvdXJjZScsICdzb3VyY2Uub3JnYW5pemVJbXBvcnRzJ10sXG4gICAgfSk7XG5cbiAgICAvLyAtLS0gTmF2aWdhdGlvbiAmIHN0cnVjdHVyZSAoUGFjayAxKSAtLS1cblxuICAgIG1vbmFjby5sYW5ndWFnZXMucmVnaXN0ZXJJbXBsZW1lbnRhdGlvblByb3ZpZGVyKCdqYXZhJywge1xuICAgICAgICBwcm92aWRlSW1wbGVtZW50YXRpb246IChtb2RlbCwgcG9zaXRpb24pID0+IHJlcXVlc3RMb2NhdGlvbnMoJ3RleHREb2N1bWVudC9pbXBsZW1lbnRhdGlvbicsIG1vZGVsLCBwb3NpdGlvbiksXG4gICAgfSk7XG5cbiAgICBtb25hY28ubGFuZ3VhZ2VzLnJlZ2lzdGVyVHlwZURlZmluaXRpb25Qcm92aWRlcignamF2YScsIHtcbiAgICAgICAgcHJvdmlkZVR5cGVEZWZpbml0aW9uOiAobW9kZWwsIHBvc2l0aW9uKSA9PiByZXF1ZXN0TG9jYXRpb25zKCd0ZXh0RG9jdW1lbnQvdHlwZURlZmluaXRpb24nLCBtb2RlbCwgcG9zaXRpb24pLFxuICAgIH0pO1xuXG4gICAgbW9uYWNvLmxhbmd1YWdlcy5yZWdpc3RlckRvY3VtZW50SGlnaGxpZ2h0UHJvdmlkZXIoJ2phdmEnLCB7XG4gICAgICAgIHByb3ZpZGVEb2N1bWVudEhpZ2hsaWdodHM6IGFzeW5jIChtb2RlbCwgcG9zaXRpb24pID0+IHtcbiAgICAgICAgICAgIGlmICghX2Nvbm4gfHwgIWlzV29ya3NwYWNlRmlsZShtb2RlbC51cmkudG9TdHJpbmcoKSkpIHJldHVybiBudWxsO1xuICAgICAgICAgICAgY29uc3QgcmVzdWx0OiBhbnlbXSB8IG51bGwgPSBhd2FpdCBfY29ubi5zZW5kUmVxdWVzdCgndGV4dERvY3VtZW50L2RvY3VtZW50SGlnaGxpZ2h0Jywge1xuICAgICAgICAgICAgICAgIHRleHREb2N1bWVudDogeyB1cmk6IG1vZGVsLnVyaS50b1N0cmluZygpIH0sXG4gICAgICAgICAgICAgICAgcG9zaXRpb246ICAgICB7IGxpbmU6IHBvc2l0aW9uLmxpbmVOdW1iZXIgLSAxLCBjaGFyYWN0ZXI6IHBvc2l0aW9uLmNvbHVtbiAtIDEgfSxcbiAgICAgICAgICAgIH0pO1xuICAgICAgICAgICAgaWYgKCFyZXN1bHQpIHJldHVybiBudWxsO1xuICAgICAgICAgICAgcmV0dXJuIHJlc3VsdC5tYXAoaCA9PiAoeyByYW5nZTogbHNwUmFuZ2VUb01vbmFjbyhoLnJhbmdlKSwga2luZDogaC5raW5kID8gaC5raW5kIC0gMSA6IHVuZGVmaW5lZCB9KSk7XG4gICAgICAgIH0sXG4gICAgfSk7XG5cbiAgICBtb25hY28ubGFuZ3VhZ2VzLnJlZ2lzdGVyRG9jdW1lbnRTeW1ib2xQcm92aWRlcignamF2YScsIHtcbiAgICAgICAgcHJvdmlkZURvY3VtZW50U3ltYm9sczogYXN5bmMgKG1vZGVsKSA9PiB7XG4gICAgICAgICAgICBpZiAoIV9jb25uIHx8ICFpc1dvcmtzcGFjZUZpbGUobW9kZWwudXJpLnRvU3RyaW5nKCkpKSByZXR1cm4gbnVsbDtcbiAgICAgICAgICAgIGNvbnN0IHJlc3VsdDogYW55W10gfCBudWxsID0gYXdhaXQgX2Nvbm4uc2VuZFJlcXVlc3QoJ3RleHREb2N1bWVudC9kb2N1bWVudFN5bWJvbCcsIHtcbiAgICAgICAgICAgICAgICB0ZXh0RG9jdW1lbnQ6IHsgdXJpOiBtb2RlbC51cmkudG9TdHJpbmcoKSB9LFxuICAgICAgICAgICAgfSk7XG4gICAgICAgICAgICByZXR1cm4gcmVzdWx0ID8gbWFwRG9jdW1lbnRTeW1ib2xzKHJlc3VsdCkgOiBudWxsO1xuICAgICAgICB9LFxuICAgIH0pO1xuXG4gICAgbW9uYWNvLmxhbmd1YWdlcy5yZWdpc3RlckZvbGRpbmdSYW5nZVByb3ZpZGVyKCdqYXZhJywge1xuICAgICAgICBwcm92aWRlRm9sZGluZ1JhbmdlczogYXN5bmMgKG1vZGVsKSA9PiB7XG4gICAgICAgICAgICBpZiAoIV9jb25uIHx8ICFpc1dvcmtzcGFjZUZpbGUobW9kZWwudXJpLnRvU3RyaW5nKCkpKSByZXR1cm4gbnVsbDtcbiAgICAgICAgICAgIGNvbnN0IHJlc3VsdDogYW55W10gfCBudWxsID0gYXdhaXQgX2Nvbm4uc2VuZFJlcXVlc3QoJ3RleHREb2N1bWVudC9mb2xkaW5nUmFuZ2UnLCB7XG4gICAgICAgICAgICAgICAgdGV4dERvY3VtZW50OiB7IHVyaTogbW9kZWwudXJpLnRvU3RyaW5nKCkgfSxcbiAgICAgICAgICAgIH0pO1xuICAgICAgICAgICAgaWYgKCFyZXN1bHQpIHJldHVybiBudWxsO1xuICAgICAgICAgICAgcmV0dXJuIHJlc3VsdC5tYXAociA9PiAoeyBzdGFydDogci5zdGFydExpbmUgKyAxLCBlbmQ6IHIuZW5kTGluZSArIDEsIGtpbmQ6IGZvbGRpbmdLaW5kKHIua2luZCkgfSkpO1xuICAgICAgICB9LFxuICAgIH0pO1xuXG4gICAgbW9uYWNvLmxhbmd1YWdlcy5yZWdpc3RlclNlbGVjdGlvblJhbmdlUHJvdmlkZXIoJ2phdmEnLCB7XG4gICAgICAgIHByb3ZpZGVTZWxlY3Rpb25SYW5nZXM6IGFzeW5jIChtb2RlbCwgcG9zaXRpb25zKSA9PiB7XG4gICAgICAgICAgICBpZiAoIV9jb25uIHx8ICFpc1dvcmtzcGFjZUZpbGUobW9kZWwudXJpLnRvU3RyaW5nKCkpKSByZXR1cm4gbnVsbDtcbiAgICAgICAgICAgIGNvbnN0IHJlc3VsdDogYW55W10gfCBudWxsID0gYXdhaXQgX2Nvbm4uc2VuZFJlcXVlc3QoJ3RleHREb2N1bWVudC9zZWxlY3Rpb25SYW5nZScsIHtcbiAgICAgICAgICAgICAgICB0ZXh0RG9jdW1lbnQ6IHsgdXJpOiBtb2RlbC51cmkudG9TdHJpbmcoKSB9LFxuICAgICAgICAgICAgICAgIHBvc2l0aW9uczogICAgcG9zaXRpb25zLm1hcChwID0+ICh7IGxpbmU6IHAubGluZU51bWJlciAtIDEsIGNoYXJhY3RlcjogcC5jb2x1bW4gLSAxIH0pKSxcbiAgICAgICAgICAgIH0pO1xuICAgICAgICAgICAgaWYgKCFyZXN1bHQpIHJldHVybiBudWxsO1xuICAgICAgICAgICAgcmV0dXJuIHJlc3VsdC5tYXAoZmxhdHRlblNlbGVjdGlvblJhbmdlKTtcbiAgICAgICAgfSxcbiAgICB9KTtcblxuICAgIG1vbmFjby5sYW5ndWFnZXMucmVnaXN0ZXJEb2N1bWVudFJhbmdlRm9ybWF0dGluZ0VkaXRQcm92aWRlcignamF2YScsIHtcbiAgICAgICAgcHJvdmlkZURvY3VtZW50UmFuZ2VGb3JtYXR0aW5nRWRpdHM6IGFzeW5jIChtb2RlbCwgcmFuZ2UpID0+IHtcbiAgICAgICAgICAgIGlmICghX2Nvbm4gfHwgIWlzV29ya3NwYWNlRmlsZShtb2RlbC51cmkudG9TdHJpbmcoKSkpIHJldHVybiBudWxsO1xuICAgICAgICAgICAgY29uc3QgZWRpdHM6IFRleHRFZGl0W10gfCBudWxsID0gYXdhaXQgX2Nvbm4uc2VuZFJlcXVlc3QoJ3RleHREb2N1bWVudC9yYW5nZUZvcm1hdHRpbmcnLCB7XG4gICAgICAgICAgICAgICAgdGV4dERvY3VtZW50OiB7IHVyaTogbW9kZWwudXJpLnRvU3RyaW5nKCkgfSxcbiAgICAgICAgICAgICAgICByYW5nZTogICAgICAgIG1vbmFjb1JhbmdlVG9Mc3AocmFuZ2UpLFxuICAgICAgICAgICAgICAgIG9wdGlvbnM6ICAgICAgeyB0YWJTaXplOiBtb2RlbC5nZXRPcHRpb25zKCkudGFiU2l6ZSwgaW5zZXJ0U3BhY2VzOiBtb2RlbC5nZXRPcHRpb25zKCkuaW5zZXJ0U3BhY2VzIH0sXG4gICAgICAgICAgICB9KTtcbiAgICAgICAgICAgIHJldHVybiBlZGl0cyA/IGVkaXRzLm1hcCh0ZXh0RWRpdFRvTW9uYWNvKSA6IG51bGw7XG4gICAgICAgIH0sXG4gICAgfSk7XG5cbiAgICAvLyAtLS0gSW5sYXkgaGludHMgKyBzZW1hbnRpYyBoaWdobGlnaHRpbmcgKFBhY2sgMikgLS0tXG5cbiAgICBtb25hY28ubGFuZ3VhZ2VzLnJlZ2lzdGVySW5sYXlIaW50c1Byb3ZpZGVyKCdqYXZhJywge1xuICAgICAgICBwcm92aWRlSW5sYXlIaW50czogYXN5bmMgKG1vZGVsLCByYW5nZSkgPT4ge1xuICAgICAgICAgICAgaWYgKCFfY29ubiB8fCAhaXNXb3Jrc3BhY2VGaWxlKG1vZGVsLnVyaS50b1N0cmluZygpKSkgcmV0dXJuIG51bGw7XG4gICAgICAgICAgICBjb25zdCByZXN1bHQ6IGFueVtdIHwgbnVsbCA9IGF3YWl0IF9jb25uLnNlbmRSZXF1ZXN0KCd0ZXh0RG9jdW1lbnQvaW5sYXlIaW50Jywge1xuICAgICAgICAgICAgICAgIHRleHREb2N1bWVudDogeyB1cmk6IG1vZGVsLnVyaS50b1N0cmluZygpIH0sXG4gICAgICAgICAgICAgICAgcmFuZ2U6ICAgICAgICBtb25hY29SYW5nZVRvTHNwKHJhbmdlKSxcbiAgICAgICAgICAgIH0pO1xuICAgICAgICAgICAgaWYgKCFyZXN1bHQpIHJldHVybiBudWxsO1xuICAgICAgICAgICAgcmV0dXJuIHtcbiAgICAgICAgICAgICAgICBoaW50czogcmVzdWx0Lm1hcChoID0+ICh7XG4gICAgICAgICAgICAgICAgICAgIHBvc2l0aW9uOiAgICAgeyBsaW5lTnVtYmVyOiBoLnBvc2l0aW9uLmxpbmUgKyAxLCBjb2x1bW46IGgucG9zaXRpb24uY2hhcmFjdGVyICsgMSB9LFxuICAgICAgICAgICAgICAgICAgICBsYWJlbDogICAgICAgIHR5cGVvZiBoLmxhYmVsID09PSAnc3RyaW5nJyA/IGgubGFiZWwgOiAoaC5sYWJlbCA/PyBbXSkubWFwKChwOiBhbnkpID0+ICh7IGxhYmVsOiBwLnZhbHVlIH0pKSxcbiAgICAgICAgICAgICAgICAgICAga2luZDogICAgICAgICBoLmtpbmQsXG4gICAgICAgICAgICAgICAgICAgIHBhZGRpbmdMZWZ0OiAgaC5wYWRkaW5nTGVmdCxcbiAgICAgICAgICAgICAgICAgICAgcGFkZGluZ1JpZ2h0OiBoLnBhZGRpbmdSaWdodCxcbiAgICAgICAgICAgICAgICAgICAgdG9vbHRpcDogICAgICBoLnRvb2x0aXAgPyBtYXJrdXBUb1N0cmluZyhoLnRvb2x0aXApIDogdW5kZWZpbmVkLFxuICAgICAgICAgICAgICAgIH0pKSxcbiAgICAgICAgICAgICAgICBkaXNwb3NlKCkgeyAvKiBub3RoaW5nIHRvIGRpc3Bvc2UgKi8gfSxcbiAgICAgICAgICAgIH07XG4gICAgICAgIH0sXG4gICAgfSk7XG5cbiAgICBtb25hY28ubGFuZ3VhZ2VzLnJlZ2lzdGVyRG9jdW1lbnRTZW1hbnRpY1Rva2Vuc1Byb3ZpZGVyKCdqYXZhJywge1xuICAgICAgICBnZXRMZWdlbmQ6ICgpID0+IF9zZW1hbnRpY1Rva2Vuc0xlZ2VuZCA/PyB7IHRva2VuVHlwZXM6IFtdLCB0b2tlbk1vZGlmaWVyczogW10gfSxcbiAgICAgICAgcHJvdmlkZURvY3VtZW50U2VtYW50aWNUb2tlbnM6IGFzeW5jIChtb2RlbCkgPT4ge1xuICAgICAgICAgICAgaWYgKCFfY29ubiB8fCAhaXNXb3Jrc3BhY2VGaWxlKG1vZGVsLnVyaS50b1N0cmluZygpKSB8fCAhX3NlbWFudGljVG9rZW5zTGVnZW5kKSByZXR1cm4gbnVsbDtcbiAgICAgICAgICAgIGNvbnN0IHJlc3VsdDogeyBkYXRhOiBudW1iZXJbXTsgcmVzdWx0SWQ/OiBzdHJpbmcgfSB8IG51bGwgPSBhd2FpdCBfY29ubi5zZW5kUmVxdWVzdCgndGV4dERvY3VtZW50L3NlbWFudGljVG9rZW5zL2Z1bGwnLCB7XG4gICAgICAgICAgICAgICAgdGV4dERvY3VtZW50OiB7IHVyaTogbW9kZWwudXJpLnRvU3RyaW5nKCkgfSxcbiAgICAgICAgICAgIH0pO1xuICAgICAgICAgICAgaWYgKCFyZXN1bHQ/LmRhdGEpIHJldHVybiBudWxsO1xuICAgICAgICAgICAgcmV0dXJuIHsgZGF0YTogbmV3IFVpbnQzMkFycmF5KHJlc3VsdC5kYXRhKSwgcmVzdWx0SWQ6IHJlc3VsdC5yZXN1bHRJZCB9O1xuICAgICAgICB9LFxuICAgICAgICByZWxlYXNlRG9jdW1lbnRTZW1hbnRpY1Rva2VuczogKCkgPT4geyAvKiBub3RoaW5nIHRvIHJlbGVhc2UgKi8gfSxcbiAgICB9KTtcblxuICAgIC8vIC0tLSBDb2RlTGVucyAoUGFjayAzKSAtLS1cblxuICAgIG1vbmFjby5sYW5ndWFnZXMucmVnaXN0ZXJDb2RlTGVuc1Byb3ZpZGVyKCdqYXZhJywge1xuICAgICAgICBwcm92aWRlQ29kZUxlbnNlczogYXN5bmMgKG1vZGVsKSA9PiB7XG4gICAgICAgICAgICBpZiAoIV9jb25uIHx8ICFpc1dvcmtzcGFjZUZpbGUobW9kZWwudXJpLnRvU3RyaW5nKCkpKSByZXR1cm4geyBsZW5zZXM6IFtdLCBkaXNwb3NlKCkgeyAvKiAqLyB9IH07XG4gICAgICAgICAgICBjb25zdCByZXN1bHQ6IGFueVtdIHwgbnVsbCA9IGF3YWl0IF9jb25uLnNlbmRSZXF1ZXN0KCd0ZXh0RG9jdW1lbnQvY29kZUxlbnMnLCB7XG4gICAgICAgICAgICAgICAgdGV4dERvY3VtZW50OiB7IHVyaTogbW9kZWwudXJpLnRvU3RyaW5nKCkgfSxcbiAgICAgICAgICAgIH0pO1xuICAgICAgICAgICAgY29uc3QgbGVuc2VzID0gKHJlc3VsdCA/PyBbXSkubWFwKChsZW5zLCBpKSA9PiAoe1xuICAgICAgICAgICAgICAgIHJhbmdlOiAgICBsc3BSYW5nZVRvTW9uYWNvKGxlbnMucmFuZ2UpLFxuICAgICAgICAgICAgICAgIGlkOiAgICAgICBTdHJpbmcoaSksXG4gICAgICAgICAgICAgICAgY29tbWFuZDogIGxlbnMuY29tbWFuZCA/IG1hcExlbnNDb21tYW5kKGxlbnMuY29tbWFuZCkgOiB1bmRlZmluZWQsXG4gICAgICAgICAgICAgICAgX2xzcDogICAgIGxlbnMsXG4gICAgICAgICAgICB9KSk7XG4gICAgICAgICAgICByZXR1cm4geyBsZW5zZXMsIGRpc3Bvc2UoKSB7IC8qICovIH0gfTtcbiAgICAgICAgfSxcbiAgICAgICAgcmVzb2x2ZUNvZGVMZW5zOiBhc3luYyAoX21vZGVsLCBjb2RlTGVucykgPT4ge1xuICAgICAgICAgICAgY29uc3QgbHNwID0gKGNvZGVMZW5zIGFzIGFueSkuX2xzcDtcbiAgICAgICAgICAgIGlmIChfY29ubiAmJiBsc3AgJiYgIWxzcC5jb21tYW5kKSB7XG4gICAgICAgICAgICAgICAgdHJ5IHtcbiAgICAgICAgICAgICAgICAgICAgY29uc3QgcmVzb2x2ZWQ6IGFueSA9IGF3YWl0IF9jb25uLnNlbmRSZXF1ZXN0KCdjb2RlTGVucy9yZXNvbHZlJywgbHNwKTtcbiAgICAgICAgICAgICAgICAgICAgY29kZUxlbnMuY29tbWFuZCA9IHJlc29sdmVkPy5jb21tYW5kID8gbWFwTGVuc0NvbW1hbmQocmVzb2x2ZWQuY29tbWFuZCkgOiB7IGlkOiBOT09QX0NPTU1BTkQsIHRpdGxlOiAnJyB9O1xuICAgICAgICAgICAgICAgIH0gY2F0Y2gge1xuICAgICAgICAgICAgICAgICAgICBjb2RlTGVucy5jb21tYW5kID0geyBpZDogTk9PUF9DT01NQU5ELCB0aXRsZTogJycgfTtcbiAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICB9XG4gICAgICAgICAgICByZXR1cm4gY29kZUxlbnM7XG4gICAgICAgIH0sXG4gICAgfSk7XG5cbiAgICAvLyAtLS0gSmF2YSBrZXl3b3JkIGNvbXBsZXRpb24gKFBhY2sgMWIpOiBhbHdheXMtYXZhaWxhYmxlLCByYW5rZWQgYmVsb3cgU0RLL0xTUCByZXN1bHRzIC0tLVxuXG4gICAgbW9uYWNvLmxhbmd1YWdlcy5yZWdpc3RlckNvbXBsZXRpb25JdGVtUHJvdmlkZXIoJ2phdmEnLCB7XG4gICAgICAgIHByb3ZpZGVDb21wbGV0aW9uSXRlbXM6IChtb2RlbCwgcG9zaXRpb24pID0+IHtcbiAgICAgICAgICAgIGlmICghaXNXb3Jrc3BhY2VGaWxlKG1vZGVsLnVyaS50b1N0cmluZygpKSkgcmV0dXJuIG51bGw7XG4gICAgICAgICAgICBjb25zdCB3b3JkID0gbW9kZWwuZ2V0V29yZFVudGlsUG9zaXRpb24ocG9zaXRpb24pO1xuICAgICAgICAgICAgY29uc3QgcmFuZ2U6IG1vbmFjby5JUmFuZ2UgPSB7XG4gICAgICAgICAgICAgICAgc3RhcnRMaW5lTnVtYmVyOiBwb3NpdGlvbi5saW5lTnVtYmVyLCBzdGFydENvbHVtbjogd29yZC5zdGFydENvbHVtbixcbiAgICAgICAgICAgICAgICBlbmRMaW5lTnVtYmVyOiAgIHBvc2l0aW9uLmxpbmVOdW1iZXIsIGVuZENvbHVtbjogICB3b3JkLmVuZENvbHVtbixcbiAgICAgICAgICAgIH07XG4gICAgICAgICAgICByZXR1cm4ge1xuICAgICAgICAgICAgICAgIHN1Z2dlc3Rpb25zOiBKQVZBX0tFWVdPUkRTLm1hcChrZXl3b3JkID0+ICh7XG4gICAgICAgICAgICAgICAgICAgIGxhYmVsOiAgICAgIGtleXdvcmQsXG4gICAgICAgICAgICAgICAgICAgIGtpbmQ6ICAgICAgIG1vbmFjby5sYW5ndWFnZXMuQ29tcGxldGlvbkl0ZW1LaW5kLktleXdvcmQsXG4gICAgICAgICAgICAgICAgICAgIGluc2VydFRleHQ6IGtleXdvcmQsXG4gICAgICAgICAgICAgICAgICAgIHJhbmdlLFxuICAgICAgICAgICAgICAgICAgICBzb3J0VGV4dDogICBgOV8ke2tleXdvcmR9YCxcbiAgICAgICAgICAgICAgICB9KSksXG4gICAgICAgICAgICB9O1xuICAgICAgICB9LFxuICAgIH0pO1xufVxuXG4vLyAtLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tXG4vLyBIZWxwZXJzXG4vLyAtLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tXG5cbmZ1bmN0aW9uIGxzcFNldmVyaXR5KHNldmVyaXR5OiBEaWFnbm9zdGljU2V2ZXJpdHkgfCB1bmRlZmluZWQpOiBtb25hY28uTWFya2VyU2V2ZXJpdHkge1xuICAgIHN3aXRjaCAoc2V2ZXJpdHkpIHtcbiAgICAgICAgY2FzZSBEaWFnbm9zdGljU2V2ZXJpdHkuRXJyb3I6ICAgICAgIHJldHVybiBtb25hY28uTWFya2VyU2V2ZXJpdHkuRXJyb3I7XG4gICAgICAgIGNhc2UgRGlhZ25vc3RpY1NldmVyaXR5Lldhcm5pbmc6ICAgICByZXR1cm4gbW9uYWNvLk1hcmtlclNldmVyaXR5Lldhcm5pbmc7XG4gICAgICAgIGNhc2UgRGlhZ25vc3RpY1NldmVyaXR5LkluZm9ybWF0aW9uOiByZXR1cm4gbW9uYWNvLk1hcmtlclNldmVyaXR5LkluZm87XG4gICAgICAgIGNhc2UgRGlhZ25vc3RpY1NldmVyaXR5LkhpbnQ6ICAgICAgICByZXR1cm4gbW9uYWNvLk1hcmtlclNldmVyaXR5LkhpbnQ7XG4gICAgICAgIGRlZmF1bHQ6ICAgICAgICAgICAgICAgICAgICAgICAgICAgICByZXR1cm4gbW9uYWNvLk1hcmtlclNldmVyaXR5LkVycm9yO1xuICAgIH1cbn1cblxuZnVuY3Rpb24gbHNwUmFuZ2VUb01vbmFjbyhyOiB7IHN0YXJ0OiB7IGxpbmU6IG51bWJlcjsgY2hhcmFjdGVyOiBudW1iZXIgfTsgZW5kOiB7IGxpbmU6IG51bWJlcjsgY2hhcmFjdGVyOiBudW1iZXIgfSB9KTogbW9uYWNvLklSYW5nZSB7XG4gICAgcmV0dXJuIHtcbiAgICAgICAgc3RhcnRMaW5lTnVtYmVyOiByLnN0YXJ0LmxpbmUgKyAxLFxuICAgICAgICBzdGFydENvbHVtbjogICAgIHIuc3RhcnQuY2hhcmFjdGVyICsgMSxcbiAgICAgICAgZW5kTGluZU51bWJlcjogICByLmVuZC5saW5lICsgMSxcbiAgICAgICAgZW5kQ29sdW1uOiAgICAgICByLmVuZC5jaGFyYWN0ZXIgKyAxLFxuICAgIH07XG59XG5cbmZ1bmN0aW9uIG1hcmt1cFRvU3RyaW5nKGM6IHN0cmluZyB8IE1hcmt1cENvbnRlbnQpOiBzdHJpbmcge1xuICAgIHJldHVybiB0eXBlb2YgYyA9PT0gJ3N0cmluZycgPyBjIDogYy52YWx1ZTtcbn1cblxuZnVuY3Rpb24gbHNwQ29tcGxldGlvbktpbmQoa2luZDogQ29tcGxldGlvbkl0ZW1LaW5kIHwgdW5kZWZpbmVkKTogbW9uYWNvLmxhbmd1YWdlcy5Db21wbGV0aW9uSXRlbUtpbmQge1xuICAgIGNvbnN0IEsgPSBtb25hY28ubGFuZ3VhZ2VzLkNvbXBsZXRpb25JdGVtS2luZDtcbiAgICBzd2l0Y2ggKGtpbmQpIHtcbiAgICAgICAgY2FzZSBDb21wbGV0aW9uSXRlbUtpbmQuVGV4dDogICAgICAgICAgcmV0dXJuIEsuVGV4dDtcbiAgICAgICAgY2FzZSBDb21wbGV0aW9uSXRlbUtpbmQuTWV0aG9kOiAgICAgICAgcmV0dXJuIEsuTWV0aG9kO1xuICAgICAgICBjYXNlIENvbXBsZXRpb25JdGVtS2luZC5GdW5jdGlvbjogICAgICByZXR1cm4gSy5GdW5jdGlvbjtcbiAgICAgICAgY2FzZSBDb21wbGV0aW9uSXRlbUtpbmQuQ29uc3RydWN0b3I6ICAgcmV0dXJuIEsuQ29uc3RydWN0b3I7XG4gICAgICAgIGNhc2UgQ29tcGxldGlvbkl0ZW1LaW5kLkZpZWxkOiAgICAgICAgIHJldHVybiBLLkZpZWxkO1xuICAgICAgICBjYXNlIENvbXBsZXRpb25JdGVtS2luZC5WYXJpYWJsZTogICAgICByZXR1cm4gSy5WYXJpYWJsZTtcbiAgICAgICAgY2FzZSBDb21wbGV0aW9uSXRlbUtpbmQuQ2xhc3M6ICAgICAgICAgcmV0dXJuIEsuQ2xhc3M7XG4gICAgICAgIGNhc2UgQ29tcGxldGlvbkl0ZW1LaW5kLkludGVyZmFjZTogICAgIHJldHVybiBLLkludGVyZmFjZTtcbiAgICAgICAgY2FzZSBDb21wbGV0aW9uSXRlbUtpbmQuTW9kdWxlOiAgICAgICAgcmV0dXJuIEsuTW9kdWxlO1xuICAgICAgICBjYXNlIENvbXBsZXRpb25JdGVtS2luZC5Qcm9wZXJ0eTogICAgICByZXR1cm4gSy5Qcm9wZXJ0eTtcbiAgICAgICAgY2FzZSBDb21wbGV0aW9uSXRlbUtpbmQuS2V5d29yZDogICAgICAgcmV0dXJuIEsuS2V5d29yZDtcbiAgICAgICAgY2FzZSBDb21wbGV0aW9uSXRlbUtpbmQuU25pcHBldDogICAgICAgcmV0dXJuIEsuU25pcHBldDtcbiAgICAgICAgY2FzZSBDb21wbGV0aW9uSXRlbUtpbmQuQ29uc3RhbnQ6ICAgICAgcmV0dXJuIEsuQ29uc3RhbnQ7XG4gICAgICAgIGNhc2UgQ29tcGxldGlvbkl0ZW1LaW5kLlN0cnVjdDogICAgICAgIHJldHVybiBLLlN0cnVjdDtcbiAgICAgICAgY2FzZSBDb21wbGV0aW9uSXRlbUtpbmQuVHlwZVBhcmFtZXRlcjogcmV0dXJuIEsuVHlwZVBhcmFtZXRlcjtcbiAgICAgICAgZGVmYXVsdDogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgcmV0dXJuIEsuVGV4dDtcbiAgICB9XG59XG5cbmZ1bmN0aW9uIGxzcENvbXBsZXRpb25Ub01vbmFjbyhcbiAgICBpdGVtOiBDb21wbGV0aW9uSXRlbSxcbiAgICBtb2RlbDogbW9uYWNvLmVkaXRvci5JVGV4dE1vZGVsLFxuICAgIHBvc2l0aW9uOiBtb25hY28uUG9zaXRpb24sXG4pOiBNb25hY29Db21wbGV0aW9uSXRlbSB7XG4gICAgY29uc3Qgd29yZCA9IG1vZGVsLmdldFdvcmRVbnRpbFBvc2l0aW9uKHBvc2l0aW9uKTtcbiAgICBsZXQgcmFuZ2U6IG1vbmFjby5JUmFuZ2UgPSB7XG4gICAgICAgIHN0YXJ0TGluZU51bWJlcjogcG9zaXRpb24ubGluZU51bWJlcixcbiAgICAgICAgc3RhcnRDb2x1bW46ICAgICB3b3JkLnN0YXJ0Q29sdW1uLFxuICAgICAgICBlbmRMaW5lTnVtYmVyOiAgIHBvc2l0aW9uLmxpbmVOdW1iZXIsXG4gICAgICAgIGVuZENvbHVtbjogICAgICAgd29yZC5lbmRDb2x1bW4sXG4gICAgfTtcbiAgICBsZXQgaW5zZXJ0VGV4dCA9IGl0ZW0uaW5zZXJ0VGV4dCA/PyBpdGVtLmxhYmVsO1xuICAgIGNvbnN0IHRleHRFZGl0ID0gaXRlbS50ZXh0RWRpdCBhcyB7IHJhbmdlPzogTHNwUmFuZ2U7IHJlcGxhY2U/OiBMc3BSYW5nZTsgaW5zZXJ0PzogTHNwUmFuZ2U7IG5ld1RleHQ/OiBzdHJpbmcgfSB8IHVuZGVmaW5lZDtcbiAgICBpZiAodGV4dEVkaXQpIHtcbiAgICAgICAgY29uc3QgciA9IHRleHRFZGl0LnJhbmdlID8/IHRleHRFZGl0LnJlcGxhY2UgPz8gdGV4dEVkaXQuaW5zZXJ0O1xuICAgICAgICBpZiAocikgcmFuZ2UgPSBsc3BSYW5nZVRvTW9uYWNvKHIpO1xuICAgICAgICBpZiAodHlwZW9mIHRleHRFZGl0Lm5ld1RleHQgPT09ICdzdHJpbmcnKSBpbnNlcnRUZXh0ID0gdGV4dEVkaXQubmV3VGV4dDtcbiAgICB9XG4gICAgY29uc3QgcmVzdWx0OiBNb25hY29Db21wbGV0aW9uSXRlbSA9IHtcbiAgICAgICAgbGFiZWw6ICAgICAgICAgICBpdGVtLmxhYmVsLFxuICAgICAgICBraW5kOiAgICAgICAgICAgIGxzcENvbXBsZXRpb25LaW5kKGl0ZW0ua2luZCksXG4gICAgICAgIGRldGFpbDogICAgICAgICAgaXRlbS5kZXRhaWwsXG4gICAgICAgIGRvY3VtZW50YXRpb246ICAgaXRlbS5kb2N1bWVudGF0aW9uID8geyB2YWx1ZTogbWFya3VwVG9TdHJpbmcoaXRlbS5kb2N1bWVudGF0aW9uKSwgaXNUcnVzdGVkOiBmYWxzZSB9IDogdW5kZWZpbmVkLFxuICAgICAgICBpbnNlcnRUZXh0LFxuICAgICAgICBpbnNlcnRUZXh0UnVsZXM6IGl0ZW0uaW5zZXJ0VGV4dEZvcm1hdCA9PT0gSW5zZXJ0VGV4dEZvcm1hdC5TbmlwcGV0XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgID8gbW9uYWNvLmxhbmd1YWdlcy5Db21wbGV0aW9uSXRlbUluc2VydFRleHRSdWxlLkluc2VydEFzU25pcHBldFxuICAgICAgICAgICAgICAgICAgICAgICAgICAgICA6IHVuZGVmaW5lZCxcbiAgICAgICAgcmFuZ2UsXG4gICAgICAgIHNvcnRUZXh0OiAgICAgICAgICAgIHNka1ByaW9yaXRpc2VkU29ydFRleHQoaXRlbSksXG4gICAgICAgIGZpbHRlclRleHQ6ICAgICAgICAgIGl0ZW0uZmlsdGVyVGV4dCxcbiAgICAgICAgcHJlc2VsZWN0OiAgICAgICAgICAgaXRlbS5wcmVzZWxlY3QsXG4gICAgICAgIGNvbW1pdENoYXJhY3RlcnM6ICAgIGl0ZW0uY29tbWl0Q2hhcmFjdGVycyxcbiAgICAgICAgYWRkaXRpb25hbFRleHRFZGl0czogaXRlbS5hZGRpdGlvbmFsVGV4dEVkaXRzPy5tYXAodGV4dEVkaXRUb01vbmFjbyksXG4gICAgfTtcbiAgICByZXN1bHQuX2xzcCA9IGl0ZW07XG4gICAgcmV0dXJuIHJlc3VsdDtcbn1cblxuLyoqXG4gKiBSYW5rcyBEaXJpZ2libGUgU0RLIHN1Z2dlc3Rpb25zICh7QGNvZGUgb3JnLmVjbGlwc2UuZGlyaWdpYmxlLnNkay4qfSkgYWJvdmUgZXZlcnl0aGluZyBlbHNlIGJ5XG4gKiBwcmVmaXhpbmcgdGhlIHNlcnZlciBzb3J0VGV4dCB3aXRoIGEgcHJpb3JpdHkgYnVja2V0LCBwcmVzZXJ2aW5nIHRoZSBzZXJ2ZXIgb3JkZXIgd2l0aGluIGVhY2ggYnVja2V0LlxuICovXG5mdW5jdGlvbiBzZGtQcmlvcml0aXNlZFNvcnRUZXh0KGl0ZW06IENvbXBsZXRpb25JdGVtKTogc3RyaW5nIHtcbiAgICBjb25zdCBiYXNlID0gaXRlbS5zb3J0VGV4dCA/PyAodHlwZW9mIGl0ZW0ubGFiZWwgPT09ICdzdHJpbmcnID8gaXRlbS5sYWJlbCA6ICcnKTtcbiAgICBjb25zdCBkZXNjcmlwdGlvbiA9IChpdGVtLmxhYmVsRGV0YWlscyAmJiB0eXBlb2YgaXRlbS5sYWJlbERldGFpbHMuZGVzY3JpcHRpb24gPT09ICdzdHJpbmcnKVxuICAgICAgICA/IGl0ZW0ubGFiZWxEZXRhaWxzLmRlc2NyaXB0aW9uIDogJyc7XG4gICAgY29uc3QgaGF5c3RhY2sgPSBgJHtpdGVtLmRldGFpbCA/PyAnJ30gJHtkZXNjcmlwdGlvbn1gO1xuICAgIHJldHVybiBoYXlzdGFjay5pbmNsdWRlcygnb3JnLmVjbGlwc2UuZGlyaWdpYmxlLnNkaycpID8gYDBfJHtiYXNlfWAgOiBgMV8ke2Jhc2V9YDtcbn1cblxuLy8gLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLVxuLy8gQ29kZSBhY3Rpb25zLCBjb21tYW5kcywgcmVmYWN0b3IvZ2VuZXJhdGUsIHdvcmtzcGFjZSBlZGl0c1xuLy8gLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLVxuXG4vKiogTW9uYWNvIGNvbW1hbmQgaWQgdXNlZCB0byBhcHBseSBhIGRlZmVycmVkIExTUCBjb2RlIGFjdGlvbiB3aGVuIHRoZSB1c2VyIHNlbGVjdHMgaXQuICovXG5jb25zdCBBUFBMWV9BQ1RJT05fQ09NTUFORCA9ICdkaXJpZ2libGUuamF2YS5hcHBseUNvZGVBY3Rpb24nO1xuXG4vKiogVmlydHVhbCBVUkkgcHJlZml4IG9mIGVkaXRvciBtb2RlbHM7IHN0cmlwcGluZyBpdCB5aWVsZHMgdGhlIElERSB3b3Jrc3BhY2UgcGF0aCAoL3dzL3Byb2ovLi4uKS4gKi9cbmNvbnN0IFZJUlRVQUxfRklMRV9QUkVGSVggPSAnZmlsZTovLy93b3Jrc3BhY2UnO1xuXG4vKiogTW9uYWNvIGNvbW1hbmQgaWQgdXNlZCBmb3IgZGlzcGxheS1vbmx5IENvZGVMZW5zZXMgKGUuZy4gYSByZWZlcmVuY2UgY291bnQgd2l0aCBubyByZXNvbHZlZCBhY3Rpb24pLiAqL1xuY29uc3QgTk9PUF9DT01NQU5EID0gJ2RpcmlnaWJsZS5qYXZhLm5vb3BMZW5zJztcblxuLyoqIEphdmEgU0Uga2V5d29yZHMvbGl0ZXJhbHMgb2ZmZXJlZCBhcyBsb3ctcHJpb3JpdHkgY29tcGxldGlvbiBzbyB0aGV5IGFsd2F5cyBhcHBlYXIgd2hpbGUgdHlwaW5nLiAqL1xuY29uc3QgSkFWQV9LRVlXT1JEUyA9IFsnYWJzdHJhY3QnLCAnYXNzZXJ0JywgJ2Jvb2xlYW4nLCAnYnJlYWsnLCAnYnl0ZScsICdjYXNlJywgJ2NhdGNoJywgJ2NoYXInLCAnY2xhc3MnLFxuICAgICdjb25zdCcsICdjb250aW51ZScsICdkZWZhdWx0JywgJ2RvJywgJ2RvdWJsZScsICdlbHNlJywgJ2VudW0nLCAnZXh0ZW5kcycsICdmaW5hbCcsICdmaW5hbGx5JywgJ2Zsb2F0JyxcbiAgICAnZm9yJywgJ2dvdG8nLCAnaWYnLCAnaW1wbGVtZW50cycsICdpbXBvcnQnLCAnaW5zdGFuY2VvZicsICdpbnQnLCAnaW50ZXJmYWNlJywgJ2xvbmcnLCAnbmF0aXZlJywgJ25ldycsXG4gICAgJ3BhY2thZ2UnLCAncHJpdmF0ZScsICdwcm90ZWN0ZWQnLCAncHVibGljJywgJ3JldHVybicsICdzaG9ydCcsICdzdGF0aWMnLCAnc3RyaWN0ZnAnLCAnc3VwZXInLCAnc3dpdGNoJyxcbiAgICAnc3luY2hyb25pemVkJywgJ3RoaXMnLCAndGhyb3cnLCAndGhyb3dzJywgJ3RyYW5zaWVudCcsICd0cnknLCAndm9pZCcsICd2b2xhdGlsZScsICd3aGlsZScsICd2YXInLFxuICAgICd5aWVsZCcsICdyZWNvcmQnLCAnc2VhbGVkJywgJ3Blcm1pdHMnLCAndHJ1ZScsICdmYWxzZScsICdudWxsJ107XG5cbi8qKiBTaGFyZWQgZGVmaW5pdGlvbi1zdHlsZSBsb2NhdGlvbiByZXF1ZXN0IHVzZWQgYnkgZ28tdG8tZGVmaW5pdGlvbi9pbXBsZW1lbnRhdGlvbi90eXBlLWRlZmluaXRpb24uICovXG5hc3luYyBmdW5jdGlvbiByZXF1ZXN0TG9jYXRpb25zKG1ldGhvZDogc3RyaW5nLCBtb2RlbDogbW9uYWNvLmVkaXRvci5JVGV4dE1vZGVsLCBwb3NpdGlvbjogbW9uYWNvLlBvc2l0aW9uKSB7XG4gICAgaWYgKCFfY29ubiB8fCAhaXNXb3Jrc3BhY2VGaWxlKG1vZGVsLnVyaS50b1N0cmluZygpKSkgcmV0dXJuIG51bGw7XG4gICAgY29uc3QgcmVzdWx0OiBMb2NhdGlvbiB8IExvY2F0aW9uW10gfCBudWxsID0gYXdhaXQgX2Nvbm4uc2VuZFJlcXVlc3QobWV0aG9kLCB7XG4gICAgICAgIHRleHREb2N1bWVudDogeyB1cmk6IG1vZGVsLnVyaS50b1N0cmluZygpIH0sXG4gICAgICAgIHBvc2l0aW9uOiAgICAgeyBsaW5lOiBwb3NpdGlvbi5saW5lTnVtYmVyIC0gMSwgY2hhcmFjdGVyOiBwb3NpdGlvbi5jb2x1bW4gLSAxIH0sXG4gICAgfSk7XG4gICAgaWYgKCFyZXN1bHQpIHJldHVybiBudWxsO1xuICAgIGNvbnN0IGxvY2F0aW9ucyA9IChBcnJheS5pc0FycmF5KHJlc3VsdCkgPyByZXN1bHQgOiBbcmVzdWx0XSkubWFwKGxvYyA9PiAoeyB1cmk6IG1vbmFjby5VcmkucGFyc2UobG9jLnVyaSksIHJhbmdlOiBsc3BSYW5nZVRvTW9uYWNvKGxvYy5yYW5nZSkgfSkpO1xuICAgIGF3YWl0IGVuc3VyZU1vZGVsc0ZvckxvY2F0aW9ucyhsb2NhdGlvbnMpO1xuICAgIHJldHVybiBsb2NhdGlvbnM7XG59XG5cbi8qKlxuICogQ3JlYXRlcyBpbi1tZW1vcnkgTW9uYWNvIG1vZGVscyBmb3IgdGhlIHdvcmtzcGFjZSBmaWxlcyByZWZlcmVuY2VkIGJ5IHRoZSBnaXZlbiBsb2NhdGlvbnMgKGZldGNoZWRcbiAqIG92ZXIgUkVTVCkgc28gdGhlIHJlZmVyZW5jZXMgLyBwZWVrIHdpZGdldHMgY2FuIHJlbmRlciBhIGNvZGUgcHJldmlldyBcdTIwMTQgTW9uYWNvIGNhbiBvbmx5IHByZXZpZXcgZmlsZXNcbiAqIGl0IGhhcyBhIG1vZGVsIGZvciwgYW5kIHRoaXMgc2luZ2xlLWZpbGUgZWRpdG9yIG90aGVyd2lzZSBoYXMgbm9uZSBmb3Igb3RoZXIgZmlsZXMuXG4gKi9cbmFzeW5jIGZ1bmN0aW9uIGVuc3VyZU1vZGVsc0ZvckxvY2F0aW9ucyhsb2NhdGlvbnM6IEFycmF5PHsgdXJpOiBtb25hY28uVXJpIH0+KTogUHJvbWlzZTx2b2lkPiB7XG4gICAgY29uc3Qgc2VlbiA9IG5ldyBTZXQ8c3RyaW5nPigpO1xuICAgIGF3YWl0IFByb21pc2UuYWxsKGxvY2F0aW9ucy5tYXAoYXN5bmMgKHsgdXJpIH0pID0+IHtcbiAgICAgICAgY29uc3QgdXJpU3RyID0gdXJpLnRvU3RyaW5nKCk7XG4gICAgICAgIGlmIChzZWVuLmhhcyh1cmlTdHIpIHx8ICF1cmlTdHIuc3RhcnRzV2l0aChWSVJUVUFMX0ZJTEVfUFJFRklYKSB8fCBtb25hY28uZWRpdG9yLmdldE1vZGVsKHVyaSkpIHJldHVybjtcbiAgICAgICAgc2Vlbi5hZGQodXJpU3RyKTtcbiAgICAgICAgdHJ5IHtcbiAgICAgICAgICAgIGNvbnN0IHRleHQgPSBhd2FpdCBmZXRjaFdvcmtzcGFjZUZpbGVUZXh0KHVyaVRvV29ya3NwYWNlUGF0aCh1cmlTdHIpID8/IHVyaVN0cik7XG4gICAgICAgICAgICBpZiAoIW1vbmFjby5lZGl0b3IuZ2V0TW9kZWwodXJpKSkgbW9uYWNvLmVkaXRvci5jcmVhdGVNb2RlbCh0ZXh0LCAnamF2YScsIHVyaSk7XG4gICAgICAgIH0gY2F0Y2gge1xuICAgICAgICAgICAgLy8gcHJldmlldyBqdXN0IHdvbid0IHJlbmRlciBmb3IgdGhpcyBvbmVcbiAgICAgICAgfVxuICAgIH0pKTtcbn1cblxuLyoqIFJlY3Vyc2l2ZWx5IG1hcHMgTFNQIGhpZXJhcmNoaWNhbCBEb2N1bWVudFN5bWJvbHMgdG8gTW9uYWNvJ3Mgc2hhcGUgKGtpbmRzIGFyZSAxLWJhc2VkIHZzIDAtYmFzZWQpLiAqL1xuZnVuY3Rpb24gbWFwRG9jdW1lbnRTeW1ib2xzKHN5bWJvbHM6IGFueVtdKTogbW9uYWNvLmxhbmd1YWdlcy5Eb2N1bWVudFN5bWJvbFtdIHtcbiAgICByZXR1cm4gKHN5bWJvbHMgPz8gW10pLm1hcChzID0+ICh7XG4gICAgICAgIG5hbWU6ICAgICAgICAgICBzLm5hbWUsXG4gICAgICAgIGRldGFpbDogICAgICAgICBzLmRldGFpbCA/PyAnJyxcbiAgICAgICAga2luZDogICAgICAgICAgIChzLmtpbmQgPz8gMSkgLSAxLFxuICAgICAgICB0YWdzOiAgICAgICAgICAgcy50YWdzID8/IFtdLFxuICAgICAgICByYW5nZTogICAgICAgICAgbHNwUmFuZ2VUb01vbmFjbyhzLnJhbmdlKSxcbiAgICAgICAgc2VsZWN0aW9uUmFuZ2U6IGxzcFJhbmdlVG9Nb25hY28ocy5zZWxlY3Rpb25SYW5nZSA/PyBzLnJhbmdlKSxcbiAgICAgICAgY2hpbGRyZW46ICAgICAgIHMuY2hpbGRyZW4gPyBtYXBEb2N1bWVudFN5bWJvbHMocy5jaGlsZHJlbikgOiBbXSxcbiAgICB9KSk7XG59XG5cbmZ1bmN0aW9uIGZvbGRpbmdLaW5kKGtpbmQ6IHN0cmluZyB8IHVuZGVmaW5lZCk6IG1vbmFjby5sYW5ndWFnZXMuRm9sZGluZ1JhbmdlS2luZCB8IHVuZGVmaW5lZCB7XG4gICAgY29uc3QgRksgPSBtb25hY28ubGFuZ3VhZ2VzLkZvbGRpbmdSYW5nZUtpbmQ7XG4gICAgc3dpdGNoIChraW5kKSB7XG4gICAgICAgIGNhc2UgJ2NvbW1lbnQnOiByZXR1cm4gRksuQ29tbWVudDtcbiAgICAgICAgY2FzZSAnaW1wb3J0cyc6IHJldHVybiBGSy5JbXBvcnRzO1xuICAgICAgICBjYXNlICdyZWdpb24nOiAgcmV0dXJuIEZLLlJlZ2lvbjtcbiAgICAgICAgZGVmYXVsdDogICAgICAgIHJldHVybiB1bmRlZmluZWQ7XG4gICAgfVxufVxuXG4vKiogRmxhdHRlbnMgYW4gTFNQIFNlbGVjdGlvblJhbmdlIHBhcmVudC1jaGFpbiBpbnRvIE1vbmFjbydzIGlubmVybW9zdC10by1vdXRlcm1vc3QgYXJyYXkuICovXG5mdW5jdGlvbiBmbGF0dGVuU2VsZWN0aW9uUmFuZ2Uoc2VsZWN0aW9uUmFuZ2U6IGFueSk6IG1vbmFjby5sYW5ndWFnZXMuU2VsZWN0aW9uUmFuZ2VbXSB7XG4gICAgY29uc3QgcmFuZ2VzOiBtb25hY28ubGFuZ3VhZ2VzLlNlbGVjdGlvblJhbmdlW10gPSBbXTtcbiAgICBsZXQgY3VycmVudCA9IHNlbGVjdGlvblJhbmdlO1xuICAgIHdoaWxlIChjdXJyZW50KSB7XG4gICAgICAgIHJhbmdlcy5wdXNoKHsgcmFuZ2U6IGxzcFJhbmdlVG9Nb25hY28oY3VycmVudC5yYW5nZSkgfSk7XG4gICAgICAgIGN1cnJlbnQgPSBjdXJyZW50LnBhcmVudDtcbiAgICB9XG4gICAgcmV0dXJuIHJhbmdlcztcbn1cblxuLyoqIE1hcHMgYSBKRFQuTFMgQ29kZUxlbnMgY29tbWFuZCB0byBhIE1vbmFjbyBjb21tYW5kLCB3aXJpbmcgdGhlIHJlZmVyZW5jZXMvaW1wbGVtZW50YXRpb25zIHBlZWsuICovXG5mdW5jdGlvbiBtYXBMZW5zQ29tbWFuZChjbWQ6IGFueSk6IG1vbmFjby5sYW5ndWFnZXMuQ29tbWFuZCB7XG4gICAgY29uc3QgYXJncyA9IGNtZC5hcmd1bWVudHMgPz8gW107XG4gICAgaWYgKChjbWQuY29tbWFuZCA9PT0gJ2phdmEuc2hvdy5yZWZlcmVuY2VzJyB8fCBjbWQuY29tbWFuZCA9PT0gJ2phdmEuc2hvdy5pbXBsZW1lbnRhdGlvbnMnKSAmJiBhcmdzLmxlbmd0aCA+PSAzKSB7XG4gICAgICAgIGNvbnN0IGxvY2F0aW9ucyA9IChhcmdzWzJdID8/IFtdKS5tYXAoKGw6IGFueSkgPT4gKHsgdXJpOiBtb25hY28uVXJpLnBhcnNlKGwudXJpKSwgcmFuZ2U6IGxzcFJhbmdlVG9Nb25hY28obC5yYW5nZSkgfSkpO1xuICAgICAgICByZXR1cm4ge1xuICAgICAgICAgICAgaWQ6ICAgICAgICAnZWRpdG9yLmFjdGlvbi5zaG93UmVmZXJlbmNlcycsXG4gICAgICAgICAgICB0aXRsZTogICAgIGNtZC50aXRsZSxcbiAgICAgICAgICAgIGFyZ3VtZW50czogW21vbmFjby5VcmkucGFyc2UoYXJnc1swXSksIHsgbGluZU51bWJlcjogYXJnc1sxXS5saW5lICsgMSwgY29sdW1uOiBhcmdzWzFdLmNoYXJhY3RlciArIDEgfSwgbG9jYXRpb25zXSxcbiAgICAgICAgfTtcbiAgICB9XG4gICAgcmV0dXJuIHsgaWQ6IE5PT1BfQ09NTUFORCwgdGl0bGU6IGNtZC50aXRsZSB9O1xufVxuXG50eXBlIExzcFJhbmdlID0geyBzdGFydDogeyBsaW5lOiBudW1iZXI7IGNoYXJhY3RlcjogbnVtYmVyIH07IGVuZDogeyBsaW5lOiBudW1iZXI7IGNoYXJhY3RlcjogbnVtYmVyIH0gfTtcblxuLyoqIE1vbmFjbyBjb21wbGV0aW9uIGl0ZW0gY2FycnlpbmcgdGhlIG9yaWdpbmF0aW5nIExTUCBpdGVtIHNvIHJlc29sdmUgY2FuIGZldGNoIGl0cyBpbXBvcnQgZWRpdHMuICovXG50eXBlIE1vbmFjb0NvbXBsZXRpb25JdGVtID0gbW9uYWNvLmxhbmd1YWdlcy5Db21wbGV0aW9uSXRlbSAmIHsgX2xzcD86IENvbXBsZXRpb25JdGVtIH07XG5cbmZ1bmN0aW9uIHRleHRFZGl0VG9Nb25hY28oZWRpdDogVGV4dEVkaXQpOiBtb25hY28ubGFuZ3VhZ2VzLlRleHRFZGl0IHtcbiAgICByZXR1cm4geyByYW5nZTogbHNwUmFuZ2VUb01vbmFjbyhlZGl0LnJhbmdlKSwgdGV4dDogZWRpdC5uZXdUZXh0IH07XG59XG5cbmZ1bmN0aW9uIG1vbmFjb1JhbmdlVG9Mc3AocjogbW9uYWNvLklSYW5nZSk6IExzcFJhbmdlIHtcbiAgICByZXR1cm4ge1xuICAgICAgICBzdGFydDogeyBsaW5lOiByLnN0YXJ0TGluZU51bWJlciAtIDEsIGNoYXJhY3Rlcjogci5zdGFydENvbHVtbiAtIDEgfSxcbiAgICAgICAgZW5kOiAgIHsgbGluZTogci5lbmRMaW5lTnVtYmVyIC0gMSwgY2hhcmFjdGVyOiByLmVuZENvbHVtbiAtIDEgfSxcbiAgICB9O1xufVxuXG4vKiogVHJ1ZSB3aGVuIHR3byBMU1AgcmFuZ2VzIGludGVyc2VjdCAodXNlZCB0byBwaWNrIHRoZSBkaWFnbm9zdGljcyByZWxldmFudCB0byBhIGNvZGUtYWN0aW9uIHJlcXVlc3QpLiAqL1xuZnVuY3Rpb24gcmFuZ2VzT3ZlcmxhcChhOiBMc3BSYW5nZSwgYjogTHNwUmFuZ2UpOiBib29sZWFuIHtcbiAgICBjb25zdCBub3RBZnRlciA9IChwOiB7IGxpbmU6IG51bWJlcjsgY2hhcmFjdGVyOiBudW1iZXIgfSwgcTogeyBsaW5lOiBudW1iZXI7IGNoYXJhY3RlcjogbnVtYmVyIH0pID0+XG4gICAgICAgIHAubGluZSA8IHEubGluZSB8fCAocC5saW5lID09PSBxLmxpbmUgJiYgcC5jaGFyYWN0ZXIgPD0gcS5jaGFyYWN0ZXIpO1xuICAgIHJldHVybiBub3RBZnRlcihhLnN0YXJ0LCBiLmVuZCkgJiYgbm90QWZ0ZXIoYi5zdGFydCwgYS5lbmQpO1xufVxuXG5mdW5jdGlvbiBsc3BDb2RlQWN0aW9uVG9Nb25hY28oYWN0aW9uOiBDb2RlQWN0aW9uIHwgQ29tbWFuZCk6IG1vbmFjby5sYW5ndWFnZXMuQ29kZUFjdGlvbiB7XG4gICAgY29uc3QgaXNDb21tYW5kID0gdHlwZW9mIChhY3Rpb24gYXMgQ29tbWFuZCkuY29tbWFuZCA9PT0gJ3N0cmluZyc7XG4gICAgY29uc3QgdGl0bGUgPSBhY3Rpb24udGl0bGVcbiAgICAgICAgPz8gKGlzQ29tbWFuZCA/IChhY3Rpb24gYXMgQ29tbWFuZCkuY29tbWFuZCA6IChhY3Rpb24gYXMgQ29kZUFjdGlvbikuY29tbWFuZD8udGl0bGUpXG4gICAgICAgID8/ICdBY3Rpb24nO1xuICAgIGNvbnN0IGtpbmQgPSBpc0NvbW1hbmQgPyAncXVpY2tmaXgnIDogKChhY3Rpb24gYXMgQ29kZUFjdGlvbikua2luZCA/PyAncXVpY2tmaXgnKTtcbiAgICByZXR1cm4ge1xuICAgICAgICB0aXRsZSxcbiAgICAgICAga2luZCxcbiAgICAgICAgZGlhZ25vc3RpY3M6IFtdLFxuICAgICAgICBpc1ByZWZlcnJlZDogKGFjdGlvbiBhcyBDb2RlQWN0aW9uKS5pc1ByZWZlcnJlZCxcbiAgICAgICAgLy8gQXBwbHkgbGF6aWx5IHRocm91Z2ggb3VyIGNvbW1hbmQgc28gd2UgY2FuIHJlc29sdmUsIHJ1biBzZXJ2ZXIgY29tbWFuZHMgYW5kIGFwcGx5IGVkaXRzIHVuaWZvcm1seS5cbiAgICAgICAgY29tbWFuZDogeyBpZDogQVBQTFlfQUNUSU9OX0NPTU1BTkQsIHRpdGxlLCBhcmd1bWVudHM6IFthY3Rpb25dIH0sXG4gICAgfTtcbn1cblxuYXN5bmMgZnVuY3Rpb24gYXBwbHlDb2RlQWN0aW9uKGFjdGlvbjogQ29kZUFjdGlvbiB8IENvbW1hbmQpOiBQcm9taXNlPHZvaWQ+IHtcbiAgICBpZiAoIV9jb25uKSByZXR1cm47XG4gICAgdHJ5IHtcbiAgICAgICAgaWYgKHR5cGVvZiAoYWN0aW9uIGFzIENvbW1hbmQpLmNvbW1hbmQgPT09ICdzdHJpbmcnKSB7XG4gICAgICAgICAgICBhd2FpdCBydW5TZXJ2ZXJDb21tYW5kKGFjdGlvbiBhcyBDb21tYW5kKTtcbiAgICAgICAgICAgIHJldHVybjtcbiAgICAgICAgfVxuICAgICAgICBsZXQgcmVzb2x2ZWQgPSBhY3Rpb24gYXMgQ29kZUFjdGlvbjtcbiAgICAgICAgaWYgKCFyZXNvbHZlZC5lZGl0ICYmIChyZXNvbHZlZCBhcyB7IGRhdGE/OiB1bmtub3duIH0pLmRhdGEgIT09IHVuZGVmaW5lZCkge1xuICAgICAgICAgICAgcmVzb2x2ZWQgPSBhd2FpdCBfY29ubi5zZW5kUmVxdWVzdCgnY29kZUFjdGlvbi9yZXNvbHZlJywgcmVzb2x2ZWQpO1xuICAgICAgICB9XG4gICAgICAgIGlmIChyZXNvbHZlZC5lZGl0KSBhcHBseVdvcmtzcGFjZUVkaXQocmVzb2x2ZWQuZWRpdCk7XG4gICAgICAgIGlmIChyZXNvbHZlZC5jb21tYW5kKSBhd2FpdCBydW5TZXJ2ZXJDb21tYW5kKHJlc29sdmVkLmNvbW1hbmQpO1xuICAgIH0gY2F0Y2ggKGUpIHtcbiAgICAgICAgY29uc29sZS53YXJuKCdbamF2YS1sc3BdIGNvZGUgYWN0aW9uIGZhaWxlZDonLCAoZSBhcyBFcnJvcik/Lm1lc3NhZ2UgPz8gZSk7XG4gICAgfVxufVxuXG5mdW5jdGlvbiBpc1dvcmtzcGFjZUVkaXQodmFsdWU6IHVua25vd24pOiB2YWx1ZSBpcyBXb3Jrc3BhY2VFZGl0IHtcbiAgICByZXR1cm4gISF2YWx1ZSAmJiAoISEodmFsdWUgYXMgV29ya3NwYWNlRWRpdCkuY2hhbmdlcyB8fCAhISh2YWx1ZSBhcyBXb3Jrc3BhY2VFZGl0KS5kb2N1bWVudENoYW5nZXMpO1xufVxuXG5hc3luYyBmdW5jdGlvbiBydW5TZXJ2ZXJDb21tYW5kKGNtZDogQ29tbWFuZCk6IFByb21pc2U8dm9pZD4ge1xuICAgIGlmICghX2Nvbm4gfHwgIWNtZD8uY29tbWFuZCkgcmV0dXJuO1xuICAgIGlmIChHRU5FUkFURVtjbWQuY29tbWFuZF0pIHtcbiAgICAgICAgYXdhaXQgcnVuR2VuZXJhdGUoY21kLmNvbW1hbmQsIGNtZC5hcmd1bWVudHMgPz8gW10pO1xuICAgICAgICByZXR1cm47XG4gICAgfVxuICAgIGNvbnN0IHJlc3VsdCA9IGF3YWl0IF9jb25uLnNlbmRSZXF1ZXN0KCd3b3Jrc3BhY2UvZXhlY3V0ZUNvbW1hbmQnLCB7IGNvbW1hbmQ6IGNtZC5jb21tYW5kLCBhcmd1bWVudHM6IGNtZC5hcmd1bWVudHMgPz8gW10gfSk7XG4gICAgaWYgKGlzV29ya3NwYWNlRWRpdChyZXN1bHQpKSBhcHBseVdvcmtzcGFjZUVkaXQocmVzdWx0KTtcbn1cblxuaW50ZXJmYWNlIEdlbmVyYXRlU3BlYyB7XG4gICAgbGFiZWw6IHN0cmluZztcbiAgICBzdGF0dXM6IHN0cmluZztcbiAgICBnZW5lcmF0ZTogc3RyaW5nO1xuICAgIG1lbWJlcnM6IChzdGF0dXM6IGFueSkgPT4gQXJyYXk8eyBsYWJlbDogc3RyaW5nOyByZWY6IGFueSB9PjtcbiAgICBidWlsZEFyZ3M6IChwcm9tcHRBcmdzOiBhbnlbXSwgc3RhdHVzOiBhbnksIHNlbGVjdGVkOiBhbnlbXSkgPT4gYW55W107XG59XG5cbi8qKiBNZW1iZXItbmFtZWQgZmllbGRzIFx1MjE5MiBwaWNrZXIgbGFiZWxzLiAqL1xuZnVuY3Rpb24gZmllbGRMYWJlbChmOiBhbnkpOiBzdHJpbmcge1xuICAgIGNvbnN0IG5hbWUgPSBmPy5uYW1lID8/IGY/LmZpZWxkTmFtZSA/PyAnJztcbiAgICBjb25zdCB0eXBlID0gZj8udHlwZSA/PyBmPy50eXBlTmFtZTtcbiAgICByZXR1cm4gdHlwZSA/IGAke25hbWV9OiAke3R5cGV9YCA6IGAke25hbWV9YDtcbn1cblxuLyoqXG4gKiBUaGUgSkRULkxTIHNvdXJjZS1nZW5lcmF0aW9uIGNvbW1hbmRzIChjb25zdHJ1Y3RvcnMsIGdldHRlcnMvc2V0dGVycywgdG9TdHJpbmcsIGhhc2hDb2RlL2VxdWFscykuXG4gKiBFYWNoIG1hcHMgdGhlIGNsaWVudCBcIipQcm9tcHRcIiBjb21tYW5kIHRvIHRoZSBzZXJ2ZXIgc3RhdHVzICsgZ2VuZXJhdGUgZGVsZWdhdGUgY29tbWFuZHM7IHRoZSBzdGF0dXNcbiAqIGNhbGwgeWllbGRzIHRoZSBjYW5kaWRhdGUgZmllbGRzIHNob3duIGluIHRoZSBtZW1iZXIgcGlja2VyLCB0aGUgZ2VuZXJhdGUgY2FsbCByZXR1cm5zIHRoZSBlZGl0LlxuICovXG5jb25zdCBHRU5FUkFURTogUmVjb3JkPHN0cmluZywgR2VuZXJhdGVTcGVjPiA9IHtcbiAgICAnamF2YS5hY3Rpb24uZ2VuZXJhdGVDb25zdHJ1Y3RvcnNQcm9tcHQnOiB7XG4gICAgICAgIGxhYmVsOiAnU2VsZWN0IGZpZWxkcyBhbmQgY29uc3RydWN0b3JzJyxcbiAgICAgICAgc3RhdHVzOiAnamF2YS5hY3Rpb24uY2hlY2tDb25zdHJ1Y3RvcnNTdGF0dXMnLFxuICAgICAgICBnZW5lcmF0ZTogJ2phdmEuYWN0aW9uLmdlbmVyYXRlQ29uc3RydWN0b3JzJyxcbiAgICAgICAgbWVtYmVyczogKHMpID0+IChzPy5maWVsZHMgPz8gW10pLm1hcCgoZjogYW55KSA9PiAoeyBsYWJlbDogZmllbGRMYWJlbChmKSwgcmVmOiBmIH0pKSxcbiAgICAgICAgYnVpbGRBcmdzOiAoYXJncywgcywgc2VsKSA9PiBbYXJnc1swXSwgeyBjb25zdHJ1Y3RvcnM6IHM/LmNvbnN0cnVjdG9ycyA/PyBbXSwgZmllbGRzOiBzZWwubWFwKG0gPT4gbS5yZWYpIH1dLFxuICAgIH0sXG4gICAgJ2phdmEuYWN0aW9uLmdlbmVyYXRlVG9TdHJpbmdQcm9tcHQnOiB7XG4gICAgICAgIGxhYmVsOiAnU2VsZWN0IGZpZWxkcyB0byBpbmNsdWRlIGluIHRvU3RyaW5nKCknLFxuICAgICAgICBzdGF0dXM6ICdqYXZhLmFjdGlvbi5jaGVja1RvU3RyaW5nU3RhdHVzJyxcbiAgICAgICAgZ2VuZXJhdGU6ICdqYXZhLmFjdGlvbi5nZW5lcmF0ZVRvU3RyaW5nJyxcbiAgICAgICAgbWVtYmVyczogKHMpID0+IChzPy5maWVsZHMgPz8gW10pLm1hcCgoZjogYW55KSA9PiAoeyBsYWJlbDogZmllbGRMYWJlbChmKSwgcmVmOiBmIH0pKSxcbiAgICAgICAgYnVpbGRBcmdzOiAoYXJncywgX3MsIHNlbCkgPT4gW2FyZ3NbMF0sIHNlbC5tYXAobSA9PiBtLnJlZildLFxuICAgIH0sXG4gICAgJ2phdmEuYWN0aW9uLmhhc2hDb2RlRXF1YWxzUHJvbXB0Jzoge1xuICAgICAgICBsYWJlbDogJ1NlbGVjdCBmaWVsZHMgZm9yIGhhc2hDb2RlKCkgYW5kIGVxdWFscygpJyxcbiAgICAgICAgc3RhdHVzOiAnamF2YS5hY3Rpb24uY2hlY2tIYXNoQ29kZUVxdWFsc1N0YXR1cycsXG4gICAgICAgIGdlbmVyYXRlOiAnamF2YS5hY3Rpb24uZ2VuZXJhdGVIYXNoQ29kZUVxdWFscycsXG4gICAgICAgIG1lbWJlcnM6IChzKSA9PiAocz8uZmllbGRzID8/IFtdKS5tYXAoKGY6IGFueSkgPT4gKHsgbGFiZWw6IGZpZWxkTGFiZWwoZiksIHJlZjogZiB9KSksXG4gICAgICAgIGJ1aWxkQXJnczogKGFyZ3MsIF9zLCBzZWwpID0+IFthcmdzWzBdLCBzZWwubWFwKG0gPT4gbS5yZWYpLCBmYWxzZV0sXG4gICAgfSxcbiAgICAnamF2YS5hY3Rpb24uZ2VuZXJhdGVBY2Nlc3NvcnNQcm9tcHQnOiB7XG4gICAgICAgIGxhYmVsOiAnU2VsZWN0IGZpZWxkcyB0byBnZW5lcmF0ZSBnZXR0ZXJzIGFuZCBzZXR0ZXJzJyxcbiAgICAgICAgc3RhdHVzOiAnamF2YS5hY3Rpb24uY2hlY2tBY2Nlc3NvcnNTdGF0dXMnLFxuICAgICAgICBnZW5lcmF0ZTogJ2phdmEuYWN0aW9uLmdlbmVyYXRlQWNjZXNzb3JzJyxcbiAgICAgICAgbWVtYmVyczogKHMpID0+IChzPy5hY2Nlc3NvcnMgPz8gcyA/PyBbXSkubWFwKChhOiBhbnkpID0+ICh7IGxhYmVsOiBmaWVsZExhYmVsKGEpLCByZWY6IGEgfSkpLFxuICAgICAgICBidWlsZEFyZ3M6IChhcmdzLCBfcywgc2VsKSA9PiBbYXJnc1swXSwgc2VsLm1hcChtID0+IG0ucmVmKV0sXG4gICAgfSxcbiAgICAnamF2YS5hY3Rpb24ub3ZlcnJpZGVNZXRob2RzUHJvbXB0Jzoge1xuICAgICAgICBsYWJlbDogJ1NlbGVjdCBtZXRob2RzIHRvIG92ZXJyaWRlIG9yIGltcGxlbWVudCcsXG4gICAgICAgIHN0YXR1czogJ2phdmEuYWN0aW9uLmxpc3RPdmVycmlkYWJsZU1ldGhvZHMnLFxuICAgICAgICBnZW5lcmF0ZTogJ2phdmEuYWN0aW9uLmFkZE92ZXJyaWRhYmxlTWV0aG9kcycsXG4gICAgICAgIG1lbWJlcnM6IChzKSA9PiAocz8ubWV0aG9kcyA/PyBbXSkubWFwKChtOiBhbnkpID0+ICh7XG4gICAgICAgICAgICBsYWJlbDogYCR7bS5uYW1lfSgkeyhtLnBhcmFtZXRlcnMgPz8gW10pLmpvaW4oJywgJyl9KSR7bS5kZWNsYXJpbmdDbGFzcyA/ICcgOiAnICsgbS5kZWNsYXJpbmdDbGFzcyA6ICcnfWAsXG4gICAgICAgICAgICByZWY6IG0sXG4gICAgICAgIH0pKSxcbiAgICAgICAgYnVpbGRBcmdzOiAoYXJncywgc3RhdHVzLCBzZWwpID0+IFthcmdzWzBdLCB7IG92ZXJyaWRhYmxlTWV0aG9kczogc2VsLm1hcChtID0+IG0ucmVmKSwgdHlwZTogc3RhdHVzPy50eXBlIH1dLFxuICAgIH0sXG59O1xuXG5hc3luYyBmdW5jdGlvbiBydW5HZW5lcmF0ZShwcm9tcHRJZDogc3RyaW5nLCBhcmdzOiBhbnlbXSk6IFByb21pc2U8dm9pZD4ge1xuICAgIGlmICghX2Nvbm4pIHJldHVybjtcbiAgICBjb25zdCBzcGVjID0gR0VORVJBVEVbcHJvbXB0SWRdO1xuICAgIGNvbnN0IHN0YXR1cyA9IGF3YWl0IF9jb25uLnNlbmRSZXF1ZXN0KCd3b3Jrc3BhY2UvZXhlY3V0ZUNvbW1hbmQnLCB7IGNvbW1hbmQ6IHNwZWMuc3RhdHVzLCBhcmd1bWVudHM6IGFyZ3MgfSk7XG4gICAgaWYgKCFzdGF0dXMpIHJldHVybjtcbiAgICBjb25zdCBtZW1iZXJzID0gc3BlYy5tZW1iZXJzKHN0YXR1cyk7XG4gICAgbGV0IHNlbGVjdGVkID0gbWVtYmVycztcbiAgICBjb25zdCBwaWNrZXIgPSAoZ2xvYmFsVGhpcyBhcyBhbnkpLmphdmFMc3BNZW1iZXJQaWNrZXI7XG4gICAgaWYgKG1lbWJlcnMubGVuZ3RoICYmIHR5cGVvZiBwaWNrZXIgPT09ICdmdW5jdGlvbicpIHtcbiAgICAgICAgY29uc3QgY2hvc2VuOiBzdHJpbmdbXSB8IG51bGwgPSBhd2FpdCBwaWNrZXIoc3BlYy5sYWJlbCwgbWVtYmVycy5tYXAobSA9PiBtLmxhYmVsKSk7XG4gICAgICAgIGlmIChjaG9zZW4gPT09IG51bGwpIHJldHVybjsgLy8gdXNlciBjYW5jZWxsZWQgdGhlIGRpYWxvZ1xuICAgICAgICBzZWxlY3RlZCA9IG1lbWJlcnMuZmlsdGVyKG0gPT4gY2hvc2VuLmluY2x1ZGVzKG0ubGFiZWwpKTtcbiAgICB9XG4gICAgY29uc3QgZWRpdCA9IGF3YWl0IF9jb25uLnNlbmRSZXF1ZXN0KCd3b3Jrc3BhY2UvZXhlY3V0ZUNvbW1hbmQnLCB7XG4gICAgICAgIGNvbW1hbmQ6IHNwZWMuZ2VuZXJhdGUsXG4gICAgICAgIGFyZ3VtZW50czogc3BlYy5idWlsZEFyZ3MoYXJncywgc3RhdHVzLCBzZWxlY3RlZCksXG4gICAgfSk7XG4gICAgaWYgKGlzV29ya3NwYWNlRWRpdChlZGl0KSkgYXBwbHlXb3Jrc3BhY2VFZGl0KGVkaXQpO1xufVxuXG4vKiogR3JvdXBzIGEgd29ya3NwYWNlIGVkaXQncyB0ZXh0IGVkaXRzIGJ5IGRvY3VtZW50IGFuZCBhcHBsaWVzIHRoZW0gaW4gcGxhY2UgdG8gb3BlbiBNb25hY28gbW9kZWxzLiAqL1xuZnVuY3Rpb24gYXBwbHlXb3Jrc3BhY2VFZGl0KGVkaXQ6IFdvcmtzcGFjZUVkaXQgfCBudWxsIHwgdW5kZWZpbmVkKTogdm9pZCB7XG4gICAgaWYgKCFlZGl0KSByZXR1cm47XG4gICAgY29uc3QgYnlVcmk6IFJlY29yZDxzdHJpbmcsIFRleHRFZGl0W10+ID0ge307XG4gICAgaWYgKGVkaXQuY2hhbmdlcykge1xuICAgICAgICBmb3IgKGNvbnN0IHVyaSBpbiBlZGl0LmNoYW5nZXMpIGJ5VXJpW3VyaV0gPSAoYnlVcmlbdXJpXSA/PyBbXSkuY29uY2F0KGVkaXQuY2hhbmdlc1t1cmldKTtcbiAgICB9XG4gICAgaWYgKGVkaXQuZG9jdW1lbnRDaGFuZ2VzKSB7XG4gICAgICAgIGZvciAoY29uc3QgZGMgb2YgZWRpdC5kb2N1bWVudENoYW5nZXMgYXMgYW55W10pIHtcbiAgICAgICAgICAgIGlmIChkYz8udGV4dERvY3VtZW50Py51cmkgJiYgQXJyYXkuaXNBcnJheShkYy5lZGl0cykpIHtcbiAgICAgICAgICAgICAgICBieVVyaVtkYy50ZXh0RG9jdW1lbnQudXJpXSA9IChieVVyaVtkYy50ZXh0RG9jdW1lbnQudXJpXSA/PyBbXSkuY29uY2F0KGRjLmVkaXRzKTtcbiAgICAgICAgICAgIH1cbiAgICAgICAgfVxuICAgIH1cbiAgICBmb3IgKGNvbnN0IHVyaSBpbiBieVVyaSkge1xuICAgICAgICBjb25zdCBtb2RlbCA9IG1vbmFjby5lZGl0b3IuZ2V0TW9kZWxzKCkuZmluZChtID0+IG0udXJpLnRvU3RyaW5nKCkgPT09IHVyaSk7XG4gICAgICAgIGlmICghbW9kZWwpIGNvbnRpbnVlO1xuICAgICAgICBjb25zdCBvcHMgPSBieVVyaVt1cmldLm1hcChlID0+ICh7IHJhbmdlOiBsc3BSYW5nZVRvTW9uYWNvKGUucmFuZ2UpLCB0ZXh0OiBlLm5ld1RleHQsIGZvcmNlTW92ZU1hcmtlcnM6IHRydWUgfSkpO1xuICAgICAgICBtb2RlbC5wdXNoRWRpdE9wZXJhdGlvbnMoW10sIG9wcywgKCkgPT4gbnVsbCk7XG4gICAgfVxufVxuXG4vKiogQ29udmVydHMgYW4gTFNQIHdvcmtzcGFjZSBlZGl0IGludG8gdGhlIE1vbmFjbyBzaGFwZSByZXR1cm5lZCBieSB0aGUgcmVuYW1lIHByb3ZpZGVyLiAqL1xuZnVuY3Rpb24gd29ya3NwYWNlRWRpdFRvTW9uYWNvKGVkaXQ6IFdvcmtzcGFjZUVkaXQgfCBudWxsKTogbW9uYWNvLmxhbmd1YWdlcy5Xb3Jrc3BhY2VFZGl0IHtcbiAgICBjb25zdCBlZGl0czogYW55W10gPSBbXTtcbiAgICBjb25zdCBwdXNoID0gKHVyaTogc3RyaW5nLCBsaXN0OiBUZXh0RWRpdFtdKSA9PiB7XG4gICAgICAgIGZvciAoY29uc3QgZSBvZiBsaXN0KSB7XG4gICAgICAgICAgICBlZGl0cy5wdXNoKHsgcmVzb3VyY2U6IG1vbmFjby5VcmkucGFyc2UodXJpKSwgdGV4dEVkaXQ6IHsgcmFuZ2U6IGxzcFJhbmdlVG9Nb25hY28oZS5yYW5nZSksIHRleHQ6IGUubmV3VGV4dCB9LCB2ZXJzaW9uSWQ6IHVuZGVmaW5lZCB9KTtcbiAgICAgICAgfVxuICAgIH07XG4gICAgaWYgKGVkaXQ/LmNoYW5nZXMpIHtcbiAgICAgICAgZm9yIChjb25zdCB1cmkgaW4gZWRpdC5jaGFuZ2VzKSBwdXNoKHVyaSwgZWRpdC5jaGFuZ2VzW3VyaV0pO1xuICAgIH1cbiAgICBpZiAoZWRpdD8uZG9jdW1lbnRDaGFuZ2VzKSB7XG4gICAgICAgIGZvciAoY29uc3QgZGMgb2YgZWRpdC5kb2N1bWVudENoYW5nZXMgYXMgYW55W10pIHtcbiAgICAgICAgICAgIGlmIChkYz8udGV4dERvY3VtZW50Py51cmkgJiYgQXJyYXkuaXNBcnJheShkYy5lZGl0cykpIHB1c2goZGMudGV4dERvY3VtZW50LnVyaSwgZGMuZWRpdHMpO1xuICAgICAgICB9XG4gICAgfVxuICAgIHJldHVybiB7IGVkaXRzIH07XG59XG5cbi8qKiBNYXBzIGEgdmlydHVhbCBlZGl0b3IgVVJJIGJhY2sgdG8gdGhlIElERSB3b3Jrc3BhY2UgcGF0aCAoe0Bjb2RlIC93cy9wcm9qLy4uLn0pLiAqL1xuZnVuY3Rpb24gdXJpVG9Xb3Jrc3BhY2VQYXRoKHVyaTogc3RyaW5nKTogc3RyaW5nIHwgbnVsbCB7XG4gICAgaWYgKCF1cmkuc3RhcnRzV2l0aChWSVJUVUFMX0ZJTEVfUFJFRklYKSkgcmV0dXJuIG51bGw7XG4gICAgcmV0dXJuIGRlY29kZVVSSUNvbXBvbmVudCh1cmkuc3Vic3RyaW5nKFZJUlRVQUxfRklMRV9QUkVGSVgubGVuZ3RoKSk7XG59XG5cbi8qKiBSZWFkcyBhIHdvcmtzcGFjZSBmaWxlJ3MgY3VycmVudCB0ZXh0IG92ZXIgdGhlIElERSBSRVNUIEFQSS4gKi9cbmFzeW5jIGZ1bmN0aW9uIGZldGNoV29ya3NwYWNlRmlsZVRleHQoaWRlUGF0aDogc3RyaW5nKTogUHJvbWlzZTxzdHJpbmc+IHtcbiAgICBjb25zdCByZXNwb25zZSA9IGF3YWl0IGZldGNoKCcvc2VydmljZXMvaWRlL3dvcmtzcGFjZXMnICsgaWRlUGF0aCwgeyBoZWFkZXJzOiB7ICdYLVJlcXVlc3RlZC1XaXRoJzogJ0ZldGNoJyB9IH0pO1xuICAgIGlmICghcmVzcG9uc2Uub2spIHtcbiAgICAgICAgdGhyb3cgbmV3IEVycm9yKGBDb3VsZCBub3QgcmVhZCAke2lkZVBhdGh9IChIVFRQICR7cmVzcG9uc2Uuc3RhdHVzfSlgKTtcbiAgICB9XG4gICAgcmV0dXJuIHJlc3BvbnNlLnRleHQoKTtcbn1cblxuLyoqIEFwcGxpZXMgTFNQIHRleHQgZWRpdHMgdG8gYSBzdHJpbmcuIE9mZnNldHMgYXJlIHJlc29sdmVkIGFnYWluc3QgdGhlIG9yaWdpbmFsIHRleHQgYW5kIGVkaXRzIGFyZVxuICogIGFwcGxpZWQgZnJvbSB0aGUgZW5kIGJhY2t3YXJkcywgc28gZWFybGllciBvZmZzZXRzIHN0YXkgdmFsaWQuICovXG5mdW5jdGlvbiBhcHBseUVkaXRzVG9UZXh0KHRleHQ6IHN0cmluZywgZWRpdHM6IFRleHRFZGl0W10pOiBzdHJpbmcge1xuICAgIGNvbnN0IGxpbmVTdGFydHMgPSBbMF07XG4gICAgZm9yIChsZXQgaSA9IDA7IGkgPCB0ZXh0Lmxlbmd0aDsgaSsrKSB7XG4gICAgICAgIGlmICh0ZXh0LmNoYXJDb2RlQXQoaSkgPT09IDEwIC8qIFxcbiAqLykgbGluZVN0YXJ0cy5wdXNoKGkgKyAxKTtcbiAgICB9XG4gICAgY29uc3Qgb2Zmc2V0ID0gKHA6IHsgbGluZTogbnVtYmVyOyBjaGFyYWN0ZXI6IG51bWJlciB9KSA9PiAobGluZVN0YXJ0c1twLmxpbmVdID8/IHRleHQubGVuZ3RoKSArIHAuY2hhcmFjdGVyO1xuICAgIGNvbnN0IG9yZGVyZWQgPSBlZGl0cy5zbGljZSgpXG4gICAgICAgICAgICAgICAgICAgICAgICAgLnNvcnQoKGEsIGIpID0+IG9mZnNldChiLnJhbmdlLnN0YXJ0KSAtIG9mZnNldChhLnJhbmdlLnN0YXJ0KSk7XG4gICAgbGV0IHJlc3VsdCA9IHRleHQ7XG4gICAgZm9yIChjb25zdCBlIG9mIG9yZGVyZWQpIHtcbiAgICAgICAgcmVzdWx0ID0gcmVzdWx0LnNsaWNlKDAsIG9mZnNldChlLnJhbmdlLnN0YXJ0KSkgKyBlLm5ld1RleHQgKyByZXN1bHQuc2xpY2Uob2Zmc2V0KGUucmFuZ2UuZW5kKSk7XG4gICAgfVxuICAgIHJldHVybiByZXN1bHQ7XG59XG5cbi8qKlxuICogQXBwbGllcyBhIEpEVC5MUyByZW5hbWUge0BsaW5rIFdvcmtzcGFjZUVkaXR9IGFjcm9zcyB0aGUgd2hvbGUgd29ya3NwYWNlOiB0ZXh0IGVkaXRzIGluIGV2ZXJ5XG4gKiBhZmZlY3RlZCBmaWxlIHBsdXMgYW55IHtAY29kZSBSZW5hbWVGaWxlfSBvcGVyYXRpb24gKGEgcHVibGljLXR5cGUgcmVuYW1lIHJlbmFtZXMgaXRzIG93blxuICoge0Bjb2RlIC5qYXZhfSBmaWxlKS4gVGhlIGN1cnJlbnQgZmlsZSdzIGVkaXRzIGdvIHRocm91Z2ggdGhlIGxpdmUgTW9uYWNvIG1vZGVsOyB0aGUgcmVzdCBhcmUgcmVhZCxcbiAqIGVkaXRlZCBhbmQgd3JpdHRlbiBiYWNrIG92ZXIgUkVTVC4gUGVyc2lzdGVuY2UgKENTUkYtZ3VhcmRlZCB3cml0ZXMsIHRoZSB0YWIgc3dpdGNoIHdoZW4gdGhlIGN1cnJlbnRcbiAqIGZpbGUgaXMgcmVuYW1lZCwgYW5kIHJlbG9hZGluZyBvdGhlciBvcGVuIGVkaXRvcnMpIGlzIGRlbGVnYXRlZCB0byB0aGUgSURFIHZpYSBqYXZhTHNwUGVyc2lzdFJlbmFtZS5cbiAqL1xuYXN5bmMgZnVuY3Rpb24gYXBwbHlSZW5hbWVBY3Jvc3NXb3Jrc3BhY2UobW9kZWw6IG1vbmFjby5lZGl0b3IuSVRleHRNb2RlbCwgZWRpdDogV29ya3NwYWNlRWRpdCk6IFByb21pc2U8dm9pZD4ge1xuICAgIGNvbnN0IGN1cnJlbnRVcmkgPSBtb2RlbC51cmkudG9TdHJpbmcoKTtcbiAgICBjb25zdCB0ZXh0QnlVcmk6IFJlY29yZDxzdHJpbmcsIFRleHRFZGl0W10+ID0ge307XG4gICAgY29uc3QgcmVuYW1lQnlVcmk6IFJlY29yZDxzdHJpbmcsIHN0cmluZz4gPSB7fTtcblxuICAgIGlmIChlZGl0LmNoYW5nZXMpIHtcbiAgICAgICAgZm9yIChjb25zdCB1cmkgaW4gZWRpdC5jaGFuZ2VzKSB0ZXh0QnlVcmlbdXJpXSA9ICh0ZXh0QnlVcmlbdXJpXSA/PyBbXSkuY29uY2F0KGVkaXQuY2hhbmdlc1t1cmldKTtcbiAgICB9XG4gICAgaWYgKGVkaXQuZG9jdW1lbnRDaGFuZ2VzKSB7XG4gICAgICAgIGZvciAoY29uc3QgZGMgb2YgZWRpdC5kb2N1bWVudENoYW5nZXMgYXMgYW55W10pIHtcbiAgICAgICAgICAgIGlmIChkYz8ua2luZCA9PT0gJ3JlbmFtZScgJiYgZGMub2xkVXJpICYmIGRjLm5ld1VyaSkge1xuICAgICAgICAgICAgICAgIHJlbmFtZUJ5VXJpW2RjLm9sZFVyaV0gPSBkYy5uZXdVcmk7XG4gICAgICAgICAgICB9IGVsc2UgaWYgKGRjPy50ZXh0RG9jdW1lbnQ/LnVyaSAmJiBBcnJheS5pc0FycmF5KGRjLmVkaXRzKSkge1xuICAgICAgICAgICAgICAgIHRleHRCeVVyaVtkYy50ZXh0RG9jdW1lbnQudXJpXSA9ICh0ZXh0QnlVcmlbZGMudGV4dERvY3VtZW50LnVyaV0gPz8gW10pLmNvbmNhdChkYy5lZGl0cyk7XG4gICAgICAgICAgICB9XG4gICAgICAgIH1cbiAgICB9XG5cbiAgICAvLyBKRFQuTFMgbWF5IGtleSBhIGZpbGUncyB0ZXh0IGVkaXRzIGJ5IGl0cyBQT1NULXJlbmFtZSBVUkkgKGRvY3VtZW50Q2hhbmdlcyBhcmUgb3JkZXJlZCwgYW5kIHRoZVxuICAgIC8vIHJlbmFtZSBjYW4gcHJlY2VkZSB0aGUgZWRpdCkuIFJlLWF0dHJpYnV0ZSBldmVyeSBlZGl0IHRvIHRoZSBvbi1kaXNrIChvbGQpIFVSSSBzbyB0aGUgY29udGVudCBpc1xuICAgIC8vIGVkaXRlZCBjb3JyZWN0bHkgYmVmb3JlIHRoZSBmaWxlIGlzIHdyaXR0ZW4vcmVuYW1lZCBcdTIwMTQgb3RoZXJ3aXNlIHRoZSBuZXcgZmlsZSBrZWVwcyB0aGUgb2xkIHR5cGVcbiAgICAvLyBuYW1lIGFuZCB0cmlnZ2VycyBcIlRoZSBwdWJsaWMgdHlwZSBYIG11c3QgYmUgZGVmaW5lZCBpbiBpdHMgb3duIGZpbGVcIi5cbiAgICBjb25zdCBuZXdUb09sZDogUmVjb3JkPHN0cmluZywgc3RyaW5nPiA9IHt9O1xuICAgIGZvciAoY29uc3Qgb2xkVXJpIGluIHJlbmFtZUJ5VXJpKSBuZXdUb09sZFtyZW5hbWVCeVVyaVtvbGRVcmldXSA9IG9sZFVyaTtcbiAgICBjb25zdCBlZGl0c0J5T2xkOiBSZWNvcmQ8c3RyaW5nLCBUZXh0RWRpdFtdPiA9IHt9O1xuICAgIGZvciAoY29uc3QgdXJpIGluIHRleHRCeVVyaSkge1xuICAgICAgICBjb25zdCBvbkRpc2tVcmkgPSBuZXdUb09sZFt1cmldID8/IHVyaTtcbiAgICAgICAgZWRpdHNCeU9sZFtvbkRpc2tVcmldID0gKGVkaXRzQnlPbGRbb25EaXNrVXJpXSA/PyBbXSkuY29uY2F0KHRleHRCeVVyaVt1cmldKTtcbiAgICB9XG5cbiAgICBjb25zdCBwYXlsb2FkOiB7XG4gICAgICAgIGN1cnJlbnRQYXRoOiBzdHJpbmcgfCBudWxsO1xuICAgICAgICBjdXJyZW50Q29udGVudDogc3RyaW5nIHwgbnVsbDtcbiAgICAgICAgY3VycmVudE5ld1BhdGg6IHN0cmluZyB8IG51bGw7XG4gICAgICAgIHdyaXRlczogQXJyYXk8eyBwYXRoOiBzdHJpbmcgfCBudWxsOyBjb250ZW50OiBzdHJpbmcgfT47XG4gICAgICAgIHJlbmFtZXM6IEFycmF5PHsgb2xkUGF0aDogc3RyaW5nIHwgbnVsbDsgbmV3UGF0aDogc3RyaW5nIHwgbnVsbDsgY29udGVudDogc3RyaW5nIH0+O1xuICAgIH0gPSB7IGN1cnJlbnRQYXRoOiB1cmlUb1dvcmtzcGFjZVBhdGgoY3VycmVudFVyaSksIGN1cnJlbnRDb250ZW50OiBudWxsLCBjdXJyZW50TmV3UGF0aDogbnVsbCwgd3JpdGVzOiBbXSwgcmVuYW1lczogW10gfTtcblxuICAgIGNvbnN0IHdhdGNoZWRDaGFuZ2VzOiBBcnJheTx7IHVyaTogc3RyaW5nOyB0eXBlOiBudW1iZXIgfT4gPSBbXTtcbiAgICBjb25zdCBvbGRVcmlzID0gbmV3IFNldDxzdHJpbmc+KFsuLi5PYmplY3Qua2V5cyhlZGl0c0J5T2xkKSwgLi4uT2JqZWN0LmtleXMocmVuYW1lQnlVcmkpXSk7XG4gICAgZm9yIChjb25zdCBvbGRVcmkgb2Ygb2xkVXJpcykge1xuICAgICAgICBjb25zdCBlZGl0cyA9IGVkaXRzQnlPbGRbb2xkVXJpXSA/PyBbXTtcbiAgICAgICAgbGV0IGNvbnRlbnQ6IHN0cmluZztcbiAgICAgICAgaWYgKG9sZFVyaSA9PT0gY3VycmVudFVyaSkge1xuICAgICAgICAgICAgaWYgKGVkaXRzLmxlbmd0aCkge1xuICAgICAgICAgICAgICAgIG1vZGVsLnB1c2hFZGl0T3BlcmF0aW9ucyhbXSwgZWRpdHMubWFwKGUgPT4gKHsgcmFuZ2U6IGxzcFJhbmdlVG9Nb25hY28oZS5yYW5nZSksIHRleHQ6IGUubmV3VGV4dCwgZm9yY2VNb3ZlTWFya2VyczogdHJ1ZSB9KSksICgpID0+IG51bGwpO1xuICAgICAgICAgICAgfVxuICAgICAgICAgICAgY29udGVudCA9IG1vZGVsLmdldFZhbHVlKCk7XG4gICAgICAgIH0gZWxzZSB7XG4gICAgICAgICAgICBjb25zdCBzb3VyY2UgPSBhd2FpdCBmZXRjaFdvcmtzcGFjZUZpbGVUZXh0KHVyaVRvV29ya3NwYWNlUGF0aChvbGRVcmkpID8/IG9sZFVyaSk7XG4gICAgICAgICAgICBjb250ZW50ID0gZWRpdHMubGVuZ3RoID8gYXBwbHlFZGl0c1RvVGV4dChzb3VyY2UsIGVkaXRzKSA6IHNvdXJjZTtcbiAgICAgICAgfVxuICAgICAgICBjb25zdCBuZXdVcmkgPSByZW5hbWVCeVVyaVtvbGRVcmldO1xuICAgICAgICBpZiAob2xkVXJpID09PSBjdXJyZW50VXJpKSB7XG4gICAgICAgICAgICBwYXlsb2FkLmN1cnJlbnRDb250ZW50ID0gY29udGVudDtcbiAgICAgICAgICAgIHBheWxvYWQuY3VycmVudE5ld1BhdGggPSBuZXdVcmkgPyB1cmlUb1dvcmtzcGFjZVBhdGgobmV3VXJpKSA6IG51bGw7XG4gICAgICAgIH0gZWxzZSBpZiAobmV3VXJpKSB7XG4gICAgICAgICAgICBwYXlsb2FkLnJlbmFtZXMucHVzaCh7IG9sZFBhdGg6IHVyaVRvV29ya3NwYWNlUGF0aChvbGRVcmkpLCBuZXdQYXRoOiB1cmlUb1dvcmtzcGFjZVBhdGgobmV3VXJpKSwgY29udGVudCB9KTtcbiAgICAgICAgfSBlbHNlIHtcbiAgICAgICAgICAgIHBheWxvYWQud3JpdGVzLnB1c2goeyBwYXRoOiB1cmlUb1dvcmtzcGFjZVBhdGgob2xkVXJpKSwgY29udGVudCB9KTtcbiAgICAgICAgfVxuICAgICAgICAvLyBGaWxlLWNoYW5nZSBldmVudHMgZm9yIEpEVC5MUyBzbyBpdCByZS1zeW5jcyB3aXRob3V0IGEgcGFnZSByZWZyZXNoLlxuICAgICAgICBpZiAobmV3VXJpKSB7XG4gICAgICAgICAgICB3YXRjaGVkQ2hhbmdlcy5wdXNoKHsgdXJpOiBvbGRVcmksIHR5cGU6IDMgLyogRGVsZXRlZCAqLyB9LCB7IHVyaTogbmV3VXJpLCB0eXBlOiAxIC8qIENyZWF0ZWQgKi8gfSk7XG4gICAgICAgICAgICBfb3BlbkZpbGVzLmRlbGV0ZShvbGRVcmkpO1xuICAgICAgICAgICAgX2Nvbm4/LnNlbmROb3RpZmljYXRpb24oJ3RleHREb2N1bWVudC9kaWRDbG9zZScsIHsgdGV4dERvY3VtZW50OiB7IHVyaTogb2xkVXJpIH0gfSk7XG4gICAgICAgIH0gZWxzZSB7XG4gICAgICAgICAgICB3YXRjaGVkQ2hhbmdlcy5wdXNoKHsgdXJpOiBvbGRVcmksIHR5cGU6IDIgLyogQ2hhbmdlZCAqLyB9KTtcbiAgICAgICAgfVxuICAgIH1cblxuICAgIGF3YWl0IChnbG9iYWxUaGlzIGFzIGFueSkuamF2YUxzcFBlcnNpc3RSZW5hbWUocGF5bG9hZCk7XG5cbiAgICAvLyBJbmZvcm0gSkRULkxTIG9mIHRoZSBvbi1kaXNrIGNoYW5nZXMgc28gdGhlIHJlbmFtZWQgdHlwZSdzIGRpYWdub3N0aWNzIGNsZWFyIGltbWVkaWF0ZWx5LlxuICAgIGlmIChfY29ubiAmJiB3YXRjaGVkQ2hhbmdlcy5sZW5ndGgpIHtcbiAgICAgICAgX2Nvbm4uc2VuZE5vdGlmaWNhdGlvbignd29ya3NwYWNlL2RpZENoYW5nZVdhdGNoZWRGaWxlcycsIHsgY2hhbmdlczogd2F0Y2hlZENoYW5nZXMgfSk7XG4gICAgfVxufVxuXG5mdW5jdGlvbiBqZHRsc1NldHRpbmdzKCkge1xuICAgIHJldHVybiB7XG4gICAgICAgIGphdmE6IHtcbiAgICAgICAgICAgIGltcG9ydDoge1xuICAgICAgICAgICAgICAgIG1hdmVuOiAgICAgIHsgZW5hYmxlZDogdHJ1ZSB9LFxuICAgICAgICAgICAgICAgIGdyYWRsZTogICAgIHsgZW5hYmxlZDogZmFsc2UgfSxcbiAgICAgICAgICAgICAgICBleGNsdXNpb25zOiBbJyoqL25vZGVfbW9kdWxlcy8qKicsICcqKi8ubWV0YWRhdGEvKionLCAnKiovYXJjaGV0eXBlLXJlc291cmNlcy8qKiddLFxuICAgICAgICAgICAgfSxcbiAgICAgICAgICAgIGF1dG9idWlsZDogeyBlbmFibGVkOiB0cnVlIH0sXG4gICAgICAgICAgICBjb21wbGV0aW9uOiB7XG4gICAgICAgICAgICAgICAgb3ZlcndyaXRlOiAgICAgICAgICAgIHRydWUsXG4gICAgICAgICAgICAgICAgZ3Vlc3NNZXRob2RBcmd1bWVudHM6IGZhbHNlLFxuICAgICAgICAgICAgICAgIHBvc3RmaXg6ICAgICAgICAgICAgICB7IGVuYWJsZWQ6IHRydWUgfSxcbiAgICAgICAgICAgICAgICBmaWx0ZXJlZFR5cGVzOiBbXG4gICAgICAgICAgICAgICAgICAgICdjb20uc3VuLionLCAnc3VuLionLCAnamRrLionLFxuICAgICAgICAgICAgICAgICAgICAnb3JnLmVjbGlwc2UuamR0LmludGVybmFsLionLFxuICAgICAgICAgICAgICAgICAgICAnb3JnLmVjbGlwc2UuY29yZS5pbnRlcm5hbC4qJyxcbiAgICAgICAgICAgICAgICAgICAgJ29yZy5lY2xpcHNlLm9zZ2kuaW50ZXJuYWwuKicsXG4gICAgICAgICAgICAgICAgXSxcbiAgICAgICAgICAgICAgICBpbXBvcnRPcmRlcjogWydqYXZhJywgJ2phdmF4JywgJ29yZycsICdjb20nLCAnJ10sXG4gICAgICAgICAgICB9LFxuICAgICAgICAgICAgc2lnbmF0dXJlSGVscDogIHsgZW5hYmxlZDogdHJ1ZSB9LFxuICAgICAgICAgICAgZm9ybWF0OiAgICAgICAgIHsgZW5hYmxlZDogdHJ1ZSB9LFxuICAgICAgICAgICAgc2F2ZUFjdGlvbnM6ICAgIHsgb3JnYW5pemVJbXBvcnRzOiBmYWxzZSB9LFxuICAgICAgICAgICAgaW5sYXlIaW50czogICAgIHsgcGFyYW1ldGVyTmFtZXM6IHsgZW5hYmxlZDogJ2FsbCcgfSB9LFxuICAgICAgICAgICAgLy8gT2ZmIGJ5IGRlZmF1bHQ6IHRoZSByZWZlcmVuY2UvaW1wbGVtZW50YXRpb24gc2VhcmNoIGJlaGluZCB0aGVzZSBDb2RlTGVuc2VzIHJ1bnMgZm9yIGV2ZXJ5XG4gICAgICAgICAgICAvLyBkZWNsYXJhdGlvbiBvbiBvcGVuIGFuZCBvbiBldmVyeSBlZGl0IGFuZCBkb21pbmF0ZXMgSkRULkxTIGxvYWQgb24gYSBsYXJnZSBjbGFzc3BhdGguXG4gICAgICAgICAgICByZWZlcmVuY2VzQ29kZUxlbnM6ICAgICB7IGVuYWJsZWQ6IGZhbHNlIH0sXG4gICAgICAgICAgICBpbXBsZW1lbnRhdGlvbnNDb2RlTGVuczogeyBlbmFibGVkOiBmYWxzZSB9LFxuICAgICAgICB9LFxuICAgIH07XG59XG4iLCAiLyogLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS1cbiAqIENvcHlyaWdodCAoYykgMjAyNCBUeXBlRm94IGFuZCBvdGhlcnMuXG4gKiBMaWNlbnNlZCB1bmRlciB0aGUgTUlUIExpY2Vuc2UuIFNlZSBMSUNFTlNFIGluIHRoZSBwYWNrYWdlIHJvb3QgZm9yIGxpY2Vuc2UgaW5mb3JtYXRpb24uXG4gKiAtLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0gKi9cblxuaW1wb3J0IHsgRGlzcG9zYWJsZSB9IGZyb20gJ3ZzY29kZS1qc29ucnBjJztcblxuZXhwb3J0IGNsYXNzIERpc3Bvc2FibGVDb2xsZWN0aW9uIGltcGxlbWVudHMgRGlzcG9zYWJsZSB7XG4gICAgcHJvdGVjdGVkIHJlYWRvbmx5IGRpc3Bvc2FibGVzOiBEaXNwb3NhYmxlW10gPSBbXTtcblxuICAgIGRpc3Bvc2UoKTogdm9pZCB7XG4gICAgICAgIHdoaWxlICh0aGlzLmRpc3Bvc2FibGVzLmxlbmd0aCAhPT0gMCkge1xuICAgICAgICAgICAgdGhpcy5kaXNwb3NhYmxlcy5wb3AoKSEuZGlzcG9zZSgpO1xuICAgICAgICB9XG4gICAgfVxuXG4gICAgcHVzaChkaXNwb3NhYmxlOiBEaXNwb3NhYmxlKTogRGlzcG9zYWJsZSB7XG4gICAgICAgIGNvbnN0IGRpc3Bvc2FibGVzID0gdGhpcy5kaXNwb3NhYmxlcztcbiAgICAgICAgZGlzcG9zYWJsZXMucHVzaChkaXNwb3NhYmxlKTtcbiAgICAgICAgcmV0dXJuIHtcbiAgICAgICAgICAgIGRpc3Bvc2UoKTogdm9pZCB7XG4gICAgICAgICAgICAgICAgY29uc3QgaW5kZXggPSBkaXNwb3NhYmxlcy5pbmRleE9mKGRpc3Bvc2FibGUpO1xuICAgICAgICAgICAgICAgIGlmIChpbmRleCAhPT0gLTEpIHtcbiAgICAgICAgICAgICAgICAgICAgZGlzcG9zYWJsZXMuc3BsaWNlKGluZGV4LCAxKTtcbiAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICB9XG4gICAgICAgIH07XG4gICAgfVxufVxuIiwgIi8qIC0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tXG4gKiBDb3B5cmlnaHQgKGMpIDIwMjQgVHlwZUZveCBhbmQgb3RoZXJzLlxuICogTGljZW5zZWQgdW5kZXIgdGhlIE1JVCBMaWNlbnNlLiBTZWUgTElDRU5TRSBpbiB0aGUgcGFja2FnZSByb290IGZvciBsaWNlbnNlIGluZm9ybWF0aW9uLlxuICogLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tICovXG5cbmltcG9ydCB7IERpc3Bvc2FibGUgfSBmcm9tICd2c2NvZGUtanNvbnJwYyc7XG5pbXBvcnQgdHlwZSB7IElDb25uZWN0aW9uIH0gZnJvbSAnLi4vc2VydmVyL2Nvbm5lY3Rpb24uanMnO1xuXG5leHBvcnQgaW50ZXJmYWNlIElXZWJTb2NrZXQgZXh0ZW5kcyBEaXNwb3NhYmxlIHtcbiAgICBzZW5kKGNvbnRlbnQ6IHN0cmluZyk6IHZvaWQ7XG4gICAgLy8gZXNsaW50LWRpc2FibGUtbmV4dC1saW5lIEB0eXBlc2NyaXB0LWVzbGludC9uby1leHBsaWNpdC1hbnlcbiAgICBvbk1lc3NhZ2UoY2I6IChkYXRhOiBhbnkpID0+IHZvaWQpOiB2b2lkO1xuICAgIC8vIGVzbGludC1kaXNhYmxlLW5leHQtbGluZSBAdHlwZXNjcmlwdC1lc2xpbnQvbm8tZXhwbGljaXQtYW55XG4gICAgb25FcnJvcihjYjogKHJlYXNvbjogYW55KSA9PiB2b2lkKTogdm9pZDtcbiAgICBvbkNsb3NlKGNiOiAoY29kZTogbnVtYmVyLCByZWFzb246IHN0cmluZykgPT4gdm9pZCk6IHZvaWQ7XG59XG5cbmV4cG9ydCBpbnRlcmZhY2UgSVdlYlNvY2tldENvbm5lY3Rpb24gZXh0ZW5kcyBJQ29ubmVjdGlvbiB7XG4gICAgcmVhZG9ubHkgc29ja2V0OiBJV2ViU29ja2V0O1xufVxuIiwgIi8qIC0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tXG4gKiBDb3B5cmlnaHQgKGMpIDIwMjQgVHlwZUZveCBhbmQgb3RoZXJzLlxuICogTGljZW5zZWQgdW5kZXIgdGhlIE1JVCBMaWNlbnNlLiBTZWUgTElDRU5TRSBpbiB0aGUgcGFja2FnZSByb290IGZvciBsaWNlbnNlIGluZm9ybWF0aW9uLlxuICogLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tICovXG5cbmltcG9ydCB7IERpc3Bvc2FibGUgfSBmcm9tICd2c2NvZGUtanNvbnJwYyc7XG4vLyBUT0RPOiBVc2UgZW52aXJvbm1lbnQtc3BlY2lmaWMgaW1wb3J0cyAodnNjb2RlLWpzb25ycGMvYnJvd3NlciBvciB2c2NvZGUtanNvbnJwYy9ub2RlKVxuLy8gd2hlbiB1cGdyYWRpbmcgdG8gdnNjb2RlLWpzb25ycGNAOS54LngtbmV4dC5YIHdoaWNoIHN1cHBvcnRzIHByb3BlciBleHBvcnQgbWFwc1xuaW1wb3J0IHsgdHlwZSBEYXRhQ2FsbGJhY2ssIEFic3RyYWN0TWVzc2FnZVJlYWRlciwgTWVzc2FnZVJlYWRlciB9IGZyb20gJ3ZzY29kZS1qc29ucnBjJztcbmltcG9ydCB0eXBlIHsgSVdlYlNvY2tldCB9IGZyb20gJy4vc29ja2V0LmpzJztcblxuZXhwb3J0IGNsYXNzIFdlYlNvY2tldE1lc3NhZ2VSZWFkZXIgZXh0ZW5kcyBBYnN0cmFjdE1lc3NhZ2VSZWFkZXIgaW1wbGVtZW50cyBNZXNzYWdlUmVhZGVyIHtcbiAgICBwcm90ZWN0ZWQgcmVhZG9ubHkgc29ja2V0OiBJV2ViU29ja2V0O1xuICAgIHByb3RlY3RlZCBzdGF0ZTogJ2luaXRpYWwnIHwgJ2xpc3RlbmluZycgfCAnY2xvc2VkJyA9ICdpbml0aWFsJztcbiAgICBwcm90ZWN0ZWQgY2FsbGJhY2s6IERhdGFDYWxsYmFjayB8IHVuZGVmaW5lZDtcbiAgICAvLyBlc2xpbnQtZGlzYWJsZS1uZXh0LWxpbmUgQHR5cGVzY3JpcHQtZXNsaW50L25vLWV4cGxpY2l0LWFueVxuICAgIHByb3RlY3RlZCByZWFkb25seSBldmVudHM6IEFycmF5PHsgbWVzc2FnZT86IGFueSwgZXJyb3I/OiBhbnkgfT4gPSBbXTtcblxuICAgIGNvbnN0cnVjdG9yKHNvY2tldDogSVdlYlNvY2tldCkge1xuICAgICAgICBzdXBlcigpO1xuICAgICAgICB0aGlzLnNvY2tldCA9IHNvY2tldDtcbiAgICAgICAgdGhpcy5zb2NrZXQub25NZXNzYWdlKG1lc3NhZ2UgPT5cbiAgICAgICAgICAgIHRoaXMucmVhZE1lc3NhZ2UobWVzc2FnZSlcbiAgICAgICAgKTtcbiAgICAgICAgdGhpcy5zb2NrZXQub25FcnJvcihlcnJvciA9PlxuICAgICAgICAgICAgdGhpcy5maXJlRXJyb3IoZXJyb3IpXG4gICAgICAgICk7XG4gICAgICAgIHRoaXMuc29ja2V0Lm9uQ2xvc2UoKGNvZGUsIHJlYXNvbikgPT4ge1xuICAgICAgICAgICAgaWYgKGNvZGUgIT09IDEwMDApIHtcbiAgICAgICAgICAgICAgICBjb25zdCBlcnJvcjogRXJyb3IgPSB7XG4gICAgICAgICAgICAgICAgICAgIG5hbWU6ICcnICsgY29kZSxcbiAgICAgICAgICAgICAgICAgICAgbWVzc2FnZTogYEVycm9yIGR1cmluZyBzb2NrZXQgcmVjb25uZWN0OiBjb2RlID0gJHtjb2RlfSwgcmVhc29uID0gJHtyZWFzb259YFxuICAgICAgICAgICAgICAgIH07XG4gICAgICAgICAgICAgICAgdGhpcy5maXJlRXJyb3IoZXJyb3IpO1xuICAgICAgICAgICAgfVxuICAgICAgICAgICAgdGhpcy5maXJlQ2xvc2UoKTtcbiAgICAgICAgfSk7XG4gICAgfVxuXG4gICAgbGlzdGVuKGNhbGxiYWNrOiBEYXRhQ2FsbGJhY2spOiBEaXNwb3NhYmxlIHtcbiAgICAgICAgaWYgKHRoaXMuc3RhdGUgPT09ICdpbml0aWFsJykge1xuICAgICAgICAgICAgdGhpcy5zdGF0ZSA9ICdsaXN0ZW5pbmcnO1xuICAgICAgICAgICAgdGhpcy5jYWxsYmFjayA9IGNhbGxiYWNrO1xuICAgICAgICAgICAgd2hpbGUgKHRoaXMuZXZlbnRzLmxlbmd0aCAhPT0gMCkge1xuICAgICAgICAgICAgICAgIGNvbnN0IGV2ZW50ID0gdGhpcy5ldmVudHMucG9wKCkhO1xuICAgICAgICAgICAgICAgIGlmIChldmVudC5tZXNzYWdlICE9PSB1bmRlZmluZWQpIHtcbiAgICAgICAgICAgICAgICAgICAgdGhpcy5yZWFkTWVzc2FnZShldmVudC5tZXNzYWdlKTtcbiAgICAgICAgICAgICAgICB9IGVsc2UgaWYgKGV2ZW50LmVycm9yICE9PSB1bmRlZmluZWQpIHtcbiAgICAgICAgICAgICAgICAgICAgdGhpcy5maXJlRXJyb3IoZXZlbnQuZXJyb3IpO1xuICAgICAgICAgICAgICAgIH0gZWxzZSB7XG4gICAgICAgICAgICAgICAgICAgIHRoaXMuZmlyZUNsb3NlKCk7XG4gICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgfVxuICAgICAgICB9XG4gICAgICAgIHJldHVybiB7XG4gICAgICAgICAgICBkaXNwb3NlOiAoKSA9PiB7XG4gICAgICAgICAgICAgICAgaWYgKHRoaXMuY2FsbGJhY2sgPT09IGNhbGxiYWNrKSB7XG4gICAgICAgICAgICAgICAgICAgIHRoaXMuc3RhdGUgPSAnaW5pdGlhbCc7XG4gICAgICAgICAgICAgICAgICAgIHRoaXMuY2FsbGJhY2sgPSB1bmRlZmluZWQ7XG4gICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgfVxuICAgICAgICB9O1xuICAgIH1cblxuICAgIG92ZXJyaWRlIGRpc3Bvc2UoKSB7XG4gICAgICAgIHN1cGVyLmRpc3Bvc2UoKTtcbiAgICAgICAgdGhpcy5zdGF0ZSA9ICdpbml0aWFsJztcbiAgICAgICAgdGhpcy5jYWxsYmFjayA9IHVuZGVmaW5lZDtcbiAgICAgICAgdGhpcy5ldmVudHMuc3BsaWNlKDAsIHRoaXMuZXZlbnRzLmxlbmd0aCk7XG4gICAgfVxuXG4gICAgLy8gZXNsaW50LWRpc2FibGUtbmV4dC1saW5lIEB0eXBlc2NyaXB0LWVzbGludC9uby1leHBsaWNpdC1hbnlcbiAgICBwcm90ZWN0ZWQgcmVhZE1lc3NhZ2UobWVzc2FnZTogYW55KTogdm9pZCB7XG4gICAgICAgIGlmICh0aGlzLnN0YXRlID09PSAnaW5pdGlhbCcpIHtcbiAgICAgICAgICAgIHRoaXMuZXZlbnRzLnNwbGljZSgwLCAwLCB7IG1lc3NhZ2UgfSk7XG4gICAgICAgIH0gZWxzZSBpZiAodGhpcy5zdGF0ZSA9PT0gJ2xpc3RlbmluZycpIHtcbiAgICAgICAgICAgIHRyeSB7XG4gICAgICAgICAgICAgICAgY29uc3QgZGF0YSA9IEpTT04ucGFyc2UobWVzc2FnZSk7XG4gICAgICAgICAgICAgICAgdGhpcy5jYWxsYmFjayEoZGF0YSk7XG4gICAgICAgICAgICB9IGNhdGNoIChlcnIpIHtcbiAgICAgICAgICAgICAgICBjb25zdCBlcnJvcjogRXJyb3IgPSB7XG4gICAgICAgICAgICAgICAgICAgIG5hbWU6ICcnICsgNDAwLFxuICAgICAgICAgICAgICAgICAgICAvLyBlc2xpbnQtZGlzYWJsZS1uZXh0LWxpbmUgQHR5cGVzY3JpcHQtZXNsaW50L25vLWV4cGxpY2l0LWFueVxuICAgICAgICAgICAgICAgICAgICBtZXNzYWdlOiBgRXJyb3IgZHVyaW5nIG1lc3NhZ2UgcGFyc2luZywgcmVhc29uID0gJHt0eXBlb2YgZXJyID09PSAnb2JqZWN0JyA/IChlcnIgYXMgYW55KS5tZXNzYWdlIDogJ3Vua25vd24nfWBcbiAgICAgICAgICAgICAgICB9O1xuICAgICAgICAgICAgICAgIHRoaXMuZmlyZUVycm9yKGVycm9yKTtcbiAgICAgICAgICAgIH1cbiAgICAgICAgfVxuICAgIH1cblxuICAgIC8vIGVzbGludC1kaXNhYmxlLW5leHQtbGluZSBAdHlwZXNjcmlwdC1lc2xpbnQvbm8tZXhwbGljaXQtYW55XG4gICAgcHJvdGVjdGVkIG92ZXJyaWRlIGZpcmVFcnJvcihlcnJvcjogYW55KTogdm9pZCB7XG4gICAgICAgIGlmICh0aGlzLnN0YXRlID09PSAnaW5pdGlhbCcpIHtcbiAgICAgICAgICAgIHRoaXMuZXZlbnRzLnNwbGljZSgwLCAwLCB7IGVycm9yIH0pO1xuICAgICAgICB9IGVsc2UgaWYgKHRoaXMuc3RhdGUgPT09ICdsaXN0ZW5pbmcnKSB7XG4gICAgICAgICAgICBzdXBlci5maXJlRXJyb3IoZXJyb3IpO1xuICAgICAgICB9XG4gICAgfVxuXG4gICAgcHJvdGVjdGVkIG92ZXJyaWRlIGZpcmVDbG9zZSgpOiB2b2lkIHtcbiAgICAgICAgaWYgKHRoaXMuc3RhdGUgPT09ICdpbml0aWFsJykge1xuICAgICAgICAgICAgdGhpcy5ldmVudHMuc3BsaWNlKDAsIDAsIHt9KTtcbiAgICAgICAgfSBlbHNlIGlmICh0aGlzLnN0YXRlID09PSAnbGlzdGVuaW5nJykge1xuICAgICAgICAgICAgc3VwZXIuZmlyZUNsb3NlKCk7XG4gICAgICAgIH1cbiAgICAgICAgdGhpcy5zdGF0ZSA9ICdjbG9zZWQnO1xuICAgIH1cbn1cbiIsICIvKiAtLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLVxuICogQ29weXJpZ2h0IChjKSAyMDI0IFR5cGVGb3ggYW5kIG90aGVycy5cbiAqIExpY2Vuc2VkIHVuZGVyIHRoZSBNSVQgTGljZW5zZS4gU2VlIExJQ0VOU0UgaW4gdGhlIHBhY2thZ2Ugcm9vdCBmb3IgbGljZW5zZSBpbmZvcm1hdGlvbi5cbiAqIC0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLSAqL1xuXG5pbXBvcnQgeyBNZXNzYWdlIH0gZnJvbSAndnNjb2RlLWpzb25ycGMnO1xuaW1wb3J0IHsgQWJzdHJhY3RNZXNzYWdlV3JpdGVyLCBNZXNzYWdlV3JpdGVyIH0gZnJvbSAndnNjb2RlLWpzb25ycGMnO1xuaW1wb3J0IHR5cGUgeyBJV2ViU29ja2V0IH0gZnJvbSAnLi9zb2NrZXQuanMnO1xuXG5leHBvcnQgY2xhc3MgV2ViU29ja2V0TWVzc2FnZVdyaXRlciBleHRlbmRzIEFic3RyYWN0TWVzc2FnZVdyaXRlciBpbXBsZW1lbnRzIE1lc3NhZ2VXcml0ZXIge1xuICAgIHByb3RlY3RlZCBlcnJvckNvdW50ID0gMDtcbiAgICBwcm90ZWN0ZWQgcmVhZG9ubHkgc29ja2V0OiBJV2ViU29ja2V0O1xuXG4gICAgY29uc3RydWN0b3Ioc29ja2V0OiBJV2ViU29ja2V0KSB7XG4gICAgICAgIHN1cGVyKCk7XG4gICAgICAgIHRoaXMuc29ja2V0ID0gc29ja2V0O1xuICAgIH1cblxuICAgIGVuZCgpOiB2b2lkIHtcbiAgICB9XG5cbiAgICBhc3luYyB3cml0ZShtc2c6IE1lc3NhZ2UpOiBQcm9taXNlPHZvaWQ+IHtcbiAgICAgICAgdHJ5IHtcbiAgICAgICAgICAgIGNvbnN0IGNvbnRlbnQgPSBKU09OLnN0cmluZ2lmeShtc2cpO1xuICAgICAgICAgICAgdGhpcy5zb2NrZXQuc2VuZChjb250ZW50KTtcbiAgICAgICAgfSBjYXRjaCAoZSkge1xuICAgICAgICAgICAgdGhpcy5lcnJvckNvdW50Kys7XG4gICAgICAgICAgICB0aGlzLmZpcmVFcnJvcihlLCBtc2csIHRoaXMuZXJyb3JDb3VudCk7XG4gICAgICAgIH1cbiAgICB9XG59XG4iLCAiLyogLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS1cbiAqIENvcHlyaWdodCAoYykgMjAyNCBUeXBlRm94IGFuZCBvdGhlcnMuXG4gKiBMaWNlbnNlZCB1bmRlciB0aGUgTUlUIExpY2Vuc2UuIFNlZSBMSUNFTlNFIGluIHRoZSBwYWNrYWdlIHJvb3QgZm9yIGxpY2Vuc2UgaW5mb3JtYXRpb24uXG4gKiAtLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0gKi9cblxuaW1wb3J0IHR5cGUgeyBNZXNzYWdlQ29ubmVjdGlvbiwgTG9nZ2VyIH0gZnJvbSAndnNjb2RlLWpzb25ycGMnO1xuaW1wb3J0IHsgY3JlYXRlTWVzc2FnZUNvbm5lY3Rpb24gfSBmcm9tICd2c2NvZGUtanNvbnJwYyc7XG5pbXBvcnQgdHlwZSB7IElXZWJTb2NrZXQgfSBmcm9tICcuL3NvY2tldC5qcyc7XG5pbXBvcnQgeyBXZWJTb2NrZXRNZXNzYWdlUmVhZGVyIH0gZnJvbSAnLi9yZWFkZXIuanMnO1xuaW1wb3J0IHsgV2ViU29ja2V0TWVzc2FnZVdyaXRlciB9IGZyb20gJy4vd3JpdGVyLmpzJztcblxuZXhwb3J0IGZ1bmN0aW9uIGNyZWF0ZVdlYlNvY2tldENvbm5lY3Rpb24oc29ja2V0OiBJV2ViU29ja2V0LCBsb2dnZXI6IExvZ2dlcik6IE1lc3NhZ2VDb25uZWN0aW9uIHtcbiAgICBjb25zdCBtZXNzYWdlUmVhZGVyID0gbmV3IFdlYlNvY2tldE1lc3NhZ2VSZWFkZXIoc29ja2V0KTtcbiAgICBjb25zdCBtZXNzYWdlV3JpdGVyID0gbmV3IFdlYlNvY2tldE1lc3NhZ2VXcml0ZXIoc29ja2V0KTtcbiAgICBjb25zdCBjb25uZWN0aW9uID0gY3JlYXRlTWVzc2FnZUNvbm5lY3Rpb24obWVzc2FnZVJlYWRlciwgbWVzc2FnZVdyaXRlciwgbG9nZ2VyKTtcbiAgICBjb25uZWN0aW9uLm9uQ2xvc2UoKCkgPT4gY29ubmVjdGlvbi5kaXNwb3NlKCkpO1xuICAgIHJldHVybiBjb25uZWN0aW9uO1xufVxuIiwgIi8qIC0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tXG4gKiBDb3B5cmlnaHQgKGMpIDIwMjQgVHlwZUZveCBhbmQgb3RoZXJzLlxuICogTGljZW5zZWQgdW5kZXIgdGhlIE1JVCBMaWNlbnNlLiBTZWUgTElDRU5TRSBpbiB0aGUgcGFja2FnZSByb290IGZvciBsaWNlbnNlIGluZm9ybWF0aW9uLlxuICogLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tICovXG5cbmltcG9ydCB0eXBlIHsgTWVzc2FnZUNvbm5lY3Rpb24sIExvZ2dlciB9IGZyb20gJ3ZzY29kZS1qc29ucnBjJztcbmltcG9ydCB7IGNyZWF0ZVdlYlNvY2tldENvbm5lY3Rpb24gfSBmcm9tICcuL3NvY2tldC9jb25uZWN0aW9uLmpzJztcbmltcG9ydCB0eXBlIHsgSVdlYlNvY2tldCB9IGZyb20gJy4vc29ja2V0L3NvY2tldC5qcyc7XG5pbXBvcnQgeyBDb25zb2xlTG9nZ2VyIH0gZnJvbSAnLi9sb2dnZXIuanMnO1xuXG5leHBvcnQgZnVuY3Rpb24gbGlzdGVuKG9wdGlvbnM6IHtcbiAgICB3ZWJTb2NrZXQ6IFdlYlNvY2tldDtcbiAgICBsb2dnZXI/OiBMb2dnZXI7XG4gICAgb25Db25uZWN0aW9uOiAoY29ubmVjdGlvbjogTWVzc2FnZUNvbm5lY3Rpb24pID0+IHZvaWQ7XG59KSB7XG4gICAgY29uc3QgeyB3ZWJTb2NrZXQsIG9uQ29ubmVjdGlvbiB9ID0gb3B0aW9ucztcbiAgICBjb25zdCBsb2dnZXIgPSBvcHRpb25zLmxvZ2dlciB8fCBuZXcgQ29uc29sZUxvZ2dlcigpO1xuICAgIHdlYlNvY2tldC5vbm9wZW4gPSAoKSA9PiB7XG4gICAgICAgIGNvbnN0IHNvY2tldCA9IHRvU29ja2V0KHdlYlNvY2tldCk7XG4gICAgICAgIGNvbnN0IGNvbm5lY3Rpb24gPSBjcmVhdGVXZWJTb2NrZXRDb25uZWN0aW9uKHNvY2tldCwgbG9nZ2VyKTtcbiAgICAgICAgb25Db25uZWN0aW9uKGNvbm5lY3Rpb24pO1xuICAgIH07XG59XG5cbmV4cG9ydCBmdW5jdGlvbiB0b1NvY2tldCh3ZWJTb2NrZXQ6IFdlYlNvY2tldCk6IElXZWJTb2NrZXQge1xuICAgIHJldHVybiB7XG4gICAgICAgIHNlbmQ6IGNvbnRlbnQgPT4gd2ViU29ja2V0LnNlbmQoY29udGVudCksXG4gICAgICAgIG9uTWVzc2FnZTogY2IgPT4ge1xuICAgICAgICAgICAgd2ViU29ja2V0Lm9ubWVzc2FnZSA9IGV2ZW50ID0+IGNiKGV2ZW50LmRhdGEpO1xuICAgICAgICB9LFxuICAgICAgICBvbkVycm9yOiBjYiA9PiB7XG4gICAgICAgICAgICAvLyBlc2xpbnQtZGlzYWJsZS1uZXh0LWxpbmUgQHR5cGVzY3JpcHQtZXNsaW50L25vLWV4cGxpY2l0LWFueVxuICAgICAgICAgICAgd2ViU29ja2V0Lm9uZXJyb3IgPSAoZXZlbnQ6IGFueSkgPT4ge1xuICAgICAgICAgICAgICAgIGlmIChPYmplY3QuaGFzT3duKGV2ZW50LCAnbWVzc2FnZScpKSB7XG4gICAgICAgICAgICAgICAgICAgIGNiKGV2ZW50Lm1lc3NhZ2UpO1xuICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgIH07XG4gICAgICAgIH0sXG4gICAgICAgIG9uQ2xvc2U6IGNiID0+IHtcbiAgICAgICAgICAgIHdlYlNvY2tldC5vbmNsb3NlID0gZXZlbnQgPT4gY2IoZXZlbnQuY29kZSwgZXZlbnQucmVhc29uKTtcbiAgICAgICAgfSxcbiAgICAgICAgZGlzcG9zZTogKCkgPT4gd2ViU29ja2V0LmNsb3NlKClcbiAgICB9O1xufVxuIiwgIi8qXG4gKiBDb3B5cmlnaHQgKGMpIDIwMTAtMjAyNiBFY2xpcHNlIERpcmlnaWJsZSBjb250cmlidXRvcnNcbiAqXG4gKiBBbGwgcmlnaHRzIHJlc2VydmVkLiBUaGlzIHByb2dyYW0gYW5kIHRoZSBhY2NvbXBhbnlpbmcgbWF0ZXJpYWxzIGFyZSBtYWRlIGF2YWlsYWJsZSB1bmRlciB0aGVcbiAqIHRlcm1zIG9mIHRoZSBFY2xpcHNlIFB1YmxpYyBMaWNlbnNlIHYyLjAgd2hpY2ggYWNjb21wYW5pZXMgdGhpcyBkaXN0cmlidXRpb24sIGFuZCBpcyBhdmFpbGFibGUgYXRcbiAqIGh0dHA6Ly93d3cuZWNsaXBzZS5vcmcvbGVnYWwvZXBsLXYyMC5odG1sXG4gKlxuICogU1BEWC1GaWxlQ29weXJpZ2h0VGV4dDogRWNsaXBzZSBEaXJpZ2libGUgY29udHJpYnV0b3JzIFNQRFgtTGljZW5zZS1JZGVudGlmaWVyOiBFUEwtMi4wXG4gKi9cblxuLyoqXG4gKiBMYXp5IFByb3h5IHNoaW0gZm9yIE1vbmFjbyBFZGl0b3IgbG9hZGVkIHZpYSBBTUQuXG4gKlxuICogZWRpdG9yLmpzIHNldHMgZ2xvYmFsVGhpcy5tb25hY28gaW5zaWRlIHRoZSBBTUQgcmVxdWlyZSgpIGNhbGxiYWNrLCBiZWZvcmUgYW55XG4gKiBKYXZhTHNwQ2xpZW50TGliLmNvbm5lY3QoKSBjYWxsLiBBbGwgcHJvcGVydHkgYWNjZXNzZXMgZ28gdGhyb3VnaCBQcm94eS5nZXQoKSB0cmFwc1xuICogc28gdGhleSByZXNvbHZlIGFnYWluc3QgdGhlIGxpdmUgd2luZG93Lm1vbmFjbyBhdCBjYWxsIHRpbWUsIG5vdCBhdCBidW5kbGUgbG9hZCB0aW1lLlxuICpcbiAqIFR5cGVTY3JpcHQgcmVzb2x2ZXMgdHlwZXMgZnJvbSB0aGUgcmVhbCBtb25hY28tZWRpdG9yIGRldkRlcGVuZGVuY3k7IGVzYnVpbGQgcmVwbGFjZXNcbiAqIHRoZSBcIm1vbmFjby1lZGl0b3JcIiBpbXBvcnQgd2l0aCB0aGlzIGZpbGUgYXQgYnVuZGxlIHRpbWUgdmlhIHRoZSBhbGlhcyBvcHRpb24uXG4gKi9cblxudHlwZSBNID0gdHlwZW9mIGltcG9ydCgnbW9uYWNvLWVkaXRvcicpO1xuXG5mdW5jdGlvbiBtKCk6IE0ge1xuICAgIHJldHVybiAoZ2xvYmFsVGhpcyBhcyBhbnkpLm1vbmFjbyBhcyBNO1xufVxuXG5mdW5jdGlvbiBuczxUIGV4dGVuZHMgb2JqZWN0PihnZXR0ZXI6ICgpID0+IFQpOiBUIHtcbiAgICByZXR1cm4gbmV3IFByb3h5KHt9IGFzIFQsIHtcbiAgICAgICAgZ2V0OiAoXywgaykgPT4gKGdldHRlcigpIGFzIGFueSlbayBhcyBzdHJpbmddLFxuICAgIH0pO1xufVxuXG5mdW5jdGlvbiBjbHM8VD4oZ2V0dGVyOiAoKSA9PiBUKTogVCB7XG4gICAgcmV0dXJuIG5ldyBQcm94eShmdW5jdGlvbiAoKSB7fSBhcyBhbnksIHtcbiAgICAgICAgY29uc3RydWN0OiAoXywgYXJncykgPT4gbmV3IChnZXR0ZXIoKSBhcyBhbnkpKC4uLmFyZ3MpLFxuICAgICAgICBnZXQ6ICAgICAgIChfLCBrKSAgICA9PiAoZ2V0dGVyKCkgYXMgYW55KVtrIGFzIHN0cmluZ10sXG4gICAgfSkgYXMgVDtcbn1cblxuZXhwb3J0IGNvbnN0IGVkaXRvciAgICAgICAgID0gbnMoKCkgPT4gbSgpLmVkaXRvcik7XG5leHBvcnQgY29uc3QgbGFuZ3VhZ2VzICAgICAgPSBucygoKSA9PiBtKCkubGFuZ3VhZ2VzKTtcbmV4cG9ydCBjb25zdCBNYXJrZXJTZXZlcml0eSA9IG5zKCgpID0+IG0oKS5NYXJrZXJTZXZlcml0eSk7XG5leHBvcnQgY29uc3QgTWFya2VyVGFnICAgICAgPSBucygoKSA9PiAobSgpIGFzIGFueSkuTWFya2VyVGFnKTtcbmV4cG9ydCBjb25zdCBVcmkgICAgICAgICAgICA9IGNscygoKSA9PiBtKCkuVXJpKTtcbmV4cG9ydCBjb25zdCBSYW5nZSAgICAgICAgICA9IGNscygoKSA9PiBtKCkuUmFuZ2UpO1xuZXhwb3J0IGNvbnN0IFBvc2l0aW9uICAgICAgID0gY2xzKCgpID0+IG0oKS5Qb3NpdGlvbik7XG5leHBvcnQgY29uc3QgU2VsZWN0aW9uICAgICAgPSBjbHMoKCkgPT4gbSgpLlNlbGVjdGlvbik7XG5leHBvcnQgY29uc3QgS2V5Q29kZSAgICAgICAgPSBucygoKSA9PiBtKCkuS2V5Q29kZSk7XG5leHBvcnQgY29uc3QgS2V5TW9kICAgICAgICAgPSBucygoKSA9PiBtKCkuS2V5TW9kKTtcbiIsICIvKiAtLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLVxuICogQ29weXJpZ2h0IChjKSBNaWNyb3NvZnQgQ29ycG9yYXRpb24uIEFsbCByaWdodHMgcmVzZXJ2ZWQuXG4gKiBMaWNlbnNlZCB1bmRlciB0aGUgTUlUIExpY2Vuc2UuIFNlZSBMaWNlbnNlLnR4dCBpbiB0aGUgcHJvamVjdCByb290IGZvciBsaWNlbnNlIGluZm9ybWF0aW9uLlxuICogLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tICovXG4ndXNlIHN0cmljdCc7XG5leHBvcnQgdmFyIERvY3VtZW50VXJpO1xuKGZ1bmN0aW9uIChEb2N1bWVudFVyaSkge1xuICAgIGZ1bmN0aW9uIGlzKHZhbHVlKSB7XG4gICAgICAgIHJldHVybiB0eXBlb2YgdmFsdWUgPT09ICdzdHJpbmcnO1xuICAgIH1cbiAgICBEb2N1bWVudFVyaS5pcyA9IGlzO1xufSkoRG9jdW1lbnRVcmkgfHwgKERvY3VtZW50VXJpID0ge30pKTtcbmV4cG9ydCB2YXIgVVJJO1xuKGZ1bmN0aW9uIChVUkkpIHtcbiAgICBmdW5jdGlvbiBpcyh2YWx1ZSkge1xuICAgICAgICByZXR1cm4gdHlwZW9mIHZhbHVlID09PSAnc3RyaW5nJztcbiAgICB9XG4gICAgVVJJLmlzID0gaXM7XG59KShVUkkgfHwgKFVSSSA9IHt9KSk7XG5leHBvcnQgdmFyIGludGVnZXI7XG4oZnVuY3Rpb24gKGludGVnZXIpIHtcbiAgICBpbnRlZ2VyLk1JTl9WQUxVRSA9IC0yMTQ3NDgzNjQ4O1xuICAgIGludGVnZXIuTUFYX1ZBTFVFID0gMjE0NzQ4MzY0NztcbiAgICBmdW5jdGlvbiBpcyh2YWx1ZSkge1xuICAgICAgICByZXR1cm4gdHlwZW9mIHZhbHVlID09PSAnbnVtYmVyJyAmJiBpbnRlZ2VyLk1JTl9WQUxVRSA8PSB2YWx1ZSAmJiB2YWx1ZSA8PSBpbnRlZ2VyLk1BWF9WQUxVRTtcbiAgICB9XG4gICAgaW50ZWdlci5pcyA9IGlzO1xufSkoaW50ZWdlciB8fCAoaW50ZWdlciA9IHt9KSk7XG5leHBvcnQgdmFyIHVpbnRlZ2VyO1xuKGZ1bmN0aW9uICh1aW50ZWdlcikge1xuICAgIHVpbnRlZ2VyLk1JTl9WQUxVRSA9IDA7XG4gICAgdWludGVnZXIuTUFYX1ZBTFVFID0gMjE0NzQ4MzY0NztcbiAgICBmdW5jdGlvbiBpcyh2YWx1ZSkge1xuICAgICAgICByZXR1cm4gdHlwZW9mIHZhbHVlID09PSAnbnVtYmVyJyAmJiB1aW50ZWdlci5NSU5fVkFMVUUgPD0gdmFsdWUgJiYgdmFsdWUgPD0gdWludGVnZXIuTUFYX1ZBTFVFO1xuICAgIH1cbiAgICB1aW50ZWdlci5pcyA9IGlzO1xufSkodWludGVnZXIgfHwgKHVpbnRlZ2VyID0ge30pKTtcbi8qKlxuICogVGhlIFBvc2l0aW9uIG5hbWVzcGFjZSBwcm92aWRlcyBoZWxwZXIgZnVuY3Rpb25zIHRvIHdvcmsgd2l0aFxuICoge0BsaW5rIFBvc2l0aW9ufSBsaXRlcmFscy5cbiAqL1xuZXhwb3J0IHZhciBQb3NpdGlvbjtcbihmdW5jdGlvbiAoUG9zaXRpb24pIHtcbiAgICAvKipcbiAgICAgKiBDcmVhdGVzIGEgbmV3IFBvc2l0aW9uIGxpdGVyYWwgZnJvbSB0aGUgZ2l2ZW4gbGluZSBhbmQgY2hhcmFjdGVyLlxuICAgICAqIEBwYXJhbSBsaW5lIFRoZSBwb3NpdGlvbidzIGxpbmUuXG4gICAgICogQHBhcmFtIGNoYXJhY3RlciBUaGUgcG9zaXRpb24ncyBjaGFyYWN0ZXIuXG4gICAgICovXG4gICAgZnVuY3Rpb24gY3JlYXRlKGxpbmUsIGNoYXJhY3Rlcikge1xuICAgICAgICBpZiAobGluZSA9PT0gTnVtYmVyLk1BWF9WQUxVRSkge1xuICAgICAgICAgICAgbGluZSA9IHVpbnRlZ2VyLk1BWF9WQUxVRTtcbiAgICAgICAgfVxuICAgICAgICBpZiAoY2hhcmFjdGVyID09PSBOdW1iZXIuTUFYX1ZBTFVFKSB7XG4gICAgICAgICAgICBjaGFyYWN0ZXIgPSB1aW50ZWdlci5NQVhfVkFMVUU7XG4gICAgICAgIH1cbiAgICAgICAgcmV0dXJuIHsgbGluZSwgY2hhcmFjdGVyIH07XG4gICAgfVxuICAgIFBvc2l0aW9uLmNyZWF0ZSA9IGNyZWF0ZTtcbiAgICAvKipcbiAgICAgKiBDaGVja3Mgd2hldGhlciB0aGUgZ2l2ZW4gbGl0ZXJhbCBjb25mb3JtcyB0byB0aGUge0BsaW5rIFBvc2l0aW9ufSBpbnRlcmZhY2UuXG4gICAgICovXG4gICAgZnVuY3Rpb24gaXModmFsdWUpIHtcbiAgICAgICAgY29uc3QgY2FuZGlkYXRlID0gdmFsdWU7XG4gICAgICAgIHJldHVybiBJcy5vYmplY3RMaXRlcmFsKGNhbmRpZGF0ZSkgJiYgSXMudWludGVnZXIoY2FuZGlkYXRlLmxpbmUpICYmIElzLnVpbnRlZ2VyKGNhbmRpZGF0ZS5jaGFyYWN0ZXIpO1xuICAgIH1cbiAgICBQb3NpdGlvbi5pcyA9IGlzO1xufSkoUG9zaXRpb24gfHwgKFBvc2l0aW9uID0ge30pKTtcbi8qKlxuICogVGhlIFJhbmdlIG5hbWVzcGFjZSBwcm92aWRlcyBoZWxwZXIgZnVuY3Rpb25zIHRvIHdvcmsgd2l0aFxuICoge0BsaW5rIFJhbmdlfSBsaXRlcmFscy5cbiAqL1xuZXhwb3J0IHZhciBSYW5nZTtcbihmdW5jdGlvbiAoUmFuZ2UpIHtcbiAgICBmdW5jdGlvbiBjcmVhdGUob25lLCB0d28sIHRocmVlLCBmb3VyKSB7XG4gICAgICAgIGlmIChJcy51aW50ZWdlcihvbmUpICYmIElzLnVpbnRlZ2VyKHR3bykgJiYgSXMudWludGVnZXIodGhyZWUpICYmIElzLnVpbnRlZ2VyKGZvdXIpKSB7XG4gICAgICAgICAgICByZXR1cm4geyBzdGFydDogUG9zaXRpb24uY3JlYXRlKG9uZSwgdHdvKSwgZW5kOiBQb3NpdGlvbi5jcmVhdGUodGhyZWUsIGZvdXIpIH07XG4gICAgICAgIH1cbiAgICAgICAgZWxzZSBpZiAoUG9zaXRpb24uaXMob25lKSAmJiBQb3NpdGlvbi5pcyh0d28pKSB7XG4gICAgICAgICAgICByZXR1cm4geyBzdGFydDogb25lLCBlbmQ6IHR3byB9O1xuICAgICAgICB9XG4gICAgICAgIGVsc2Uge1xuICAgICAgICAgICAgdGhyb3cgbmV3IEVycm9yKGBSYW5nZSNjcmVhdGUgY2FsbGVkIHdpdGggaW52YWxpZCBhcmd1bWVudHNbJHtvbmV9LCAke3R3b30sICR7dGhyZWV9LCAke2ZvdXJ9XWApO1xuICAgICAgICB9XG4gICAgfVxuICAgIFJhbmdlLmNyZWF0ZSA9IGNyZWF0ZTtcbiAgICAvKipcbiAgICAgKiBDaGVja3Mgd2hldGhlciB0aGUgZ2l2ZW4gbGl0ZXJhbCBjb25mb3JtcyB0byB0aGUge0BsaW5rIFJhbmdlfSBpbnRlcmZhY2UuXG4gICAgICovXG4gICAgZnVuY3Rpb24gaXModmFsdWUpIHtcbiAgICAgICAgY29uc3QgY2FuZGlkYXRlID0gdmFsdWU7XG4gICAgICAgIHJldHVybiBJcy5vYmplY3RMaXRlcmFsKGNhbmRpZGF0ZSkgJiYgUG9zaXRpb24uaXMoY2FuZGlkYXRlLnN0YXJ0KSAmJiBQb3NpdGlvbi5pcyhjYW5kaWRhdGUuZW5kKTtcbiAgICB9XG4gICAgUmFuZ2UuaXMgPSBpcztcbn0pKFJhbmdlIHx8IChSYW5nZSA9IHt9KSk7XG4vKipcbiAqIFRoZSBMb2NhdGlvbiBuYW1lc3BhY2UgcHJvdmlkZXMgaGVscGVyIGZ1bmN0aW9ucyB0byB3b3JrIHdpdGhcbiAqIHtAbGluayBMb2NhdGlvbn0gbGl0ZXJhbHMuXG4gKi9cbmV4cG9ydCB2YXIgTG9jYXRpb247XG4oZnVuY3Rpb24gKExvY2F0aW9uKSB7XG4gICAgLyoqXG4gICAgICogQ3JlYXRlcyBhIExvY2F0aW9uIGxpdGVyYWwuXG4gICAgICogQHBhcmFtIHVyaSBUaGUgbG9jYXRpb24ncyB1cmkuXG4gICAgICogQHBhcmFtIHJhbmdlIFRoZSBsb2NhdGlvbidzIHJhbmdlLlxuICAgICAqL1xuICAgIGZ1bmN0aW9uIGNyZWF0ZSh1cmksIHJhbmdlKSB7XG4gICAgICAgIHJldHVybiB7IHVyaSwgcmFuZ2UgfTtcbiAgICB9XG4gICAgTG9jYXRpb24uY3JlYXRlID0gY3JlYXRlO1xuICAgIC8qKlxuICAgICAqIENoZWNrcyB3aGV0aGVyIHRoZSBnaXZlbiBsaXRlcmFsIGNvbmZvcm1zIHRvIHRoZSB7QGxpbmsgTG9jYXRpb259IGludGVyZmFjZS5cbiAgICAgKi9cbiAgICBmdW5jdGlvbiBpcyh2YWx1ZSkge1xuICAgICAgICBjb25zdCBjYW5kaWRhdGUgPSB2YWx1ZTtcbiAgICAgICAgcmV0dXJuIElzLm9iamVjdExpdGVyYWwoY2FuZGlkYXRlKSAmJiBSYW5nZS5pcyhjYW5kaWRhdGUucmFuZ2UpICYmIChJcy5zdHJpbmcoY2FuZGlkYXRlLnVyaSkgfHwgSXMudW5kZWZpbmVkKGNhbmRpZGF0ZS51cmkpKTtcbiAgICB9XG4gICAgTG9jYXRpb24uaXMgPSBpcztcbn0pKExvY2F0aW9uIHx8IChMb2NhdGlvbiA9IHt9KSk7XG4vKipcbiAqIFRoZSBMb2NhdGlvbkxpbmsgbmFtZXNwYWNlIHByb3ZpZGVzIGhlbHBlciBmdW5jdGlvbnMgdG8gd29yayB3aXRoXG4gKiB7QGxpbmsgTG9jYXRpb25MaW5rfSBsaXRlcmFscy5cbiAqL1xuZXhwb3J0IHZhciBMb2NhdGlvbkxpbms7XG4oZnVuY3Rpb24gKExvY2F0aW9uTGluaykge1xuICAgIC8qKlxuICAgICAqIENyZWF0ZXMgYSBMb2NhdGlvbkxpbmsgbGl0ZXJhbC5cbiAgICAgKiBAcGFyYW0gdGFyZ2V0VXJpIFRoZSBkZWZpbml0aW9uJ3MgdXJpLlxuICAgICAqIEBwYXJhbSB0YXJnZXRSYW5nZSBUaGUgZnVsbCByYW5nZSBvZiB0aGUgZGVmaW5pdGlvbi5cbiAgICAgKiBAcGFyYW0gdGFyZ2V0U2VsZWN0aW9uUmFuZ2UgVGhlIHNwYW4gb2YgdGhlIHN5bWJvbCBkZWZpbml0aW9uIGF0IHRoZSB0YXJnZXQuXG4gICAgICogQHBhcmFtIG9yaWdpblNlbGVjdGlvblJhbmdlIFRoZSBzcGFuIG9mIHRoZSBzeW1ib2wgYmVpbmcgZGVmaW5lZCBpbiB0aGUgb3JpZ2luYXRpbmcgc291cmNlIGZpbGUuXG4gICAgICovXG4gICAgZnVuY3Rpb24gY3JlYXRlKHRhcmdldFVyaSwgdGFyZ2V0UmFuZ2UsIHRhcmdldFNlbGVjdGlvblJhbmdlLCBvcmlnaW5TZWxlY3Rpb25SYW5nZSkge1xuICAgICAgICByZXR1cm4geyB0YXJnZXRVcmksIHRhcmdldFJhbmdlLCB0YXJnZXRTZWxlY3Rpb25SYW5nZSwgb3JpZ2luU2VsZWN0aW9uUmFuZ2UgfTtcbiAgICB9XG4gICAgTG9jYXRpb25MaW5rLmNyZWF0ZSA9IGNyZWF0ZTtcbiAgICAvKipcbiAgICAgKiBDaGVja3Mgd2hldGhlciB0aGUgZ2l2ZW4gbGl0ZXJhbCBjb25mb3JtcyB0byB0aGUge0BsaW5rIExvY2F0aW9uTGlua30gaW50ZXJmYWNlLlxuICAgICAqL1xuICAgIGZ1bmN0aW9uIGlzKHZhbHVlKSB7XG4gICAgICAgIGNvbnN0IGNhbmRpZGF0ZSA9IHZhbHVlO1xuICAgICAgICByZXR1cm4gSXMub2JqZWN0TGl0ZXJhbChjYW5kaWRhdGUpICYmIFJhbmdlLmlzKGNhbmRpZGF0ZS50YXJnZXRSYW5nZSkgJiYgSXMuc3RyaW5nKGNhbmRpZGF0ZS50YXJnZXRVcmkpXG4gICAgICAgICAgICAmJiBSYW5nZS5pcyhjYW5kaWRhdGUudGFyZ2V0U2VsZWN0aW9uUmFuZ2UpXG4gICAgICAgICAgICAmJiAoUmFuZ2UuaXMoY2FuZGlkYXRlLm9yaWdpblNlbGVjdGlvblJhbmdlKSB8fCBJcy51bmRlZmluZWQoY2FuZGlkYXRlLm9yaWdpblNlbGVjdGlvblJhbmdlKSk7XG4gICAgfVxuICAgIExvY2F0aW9uTGluay5pcyA9IGlzO1xufSkoTG9jYXRpb25MaW5rIHx8IChMb2NhdGlvbkxpbmsgPSB7fSkpO1xuLyoqXG4gKiBUaGUgQ29sb3IgbmFtZXNwYWNlIHByb3ZpZGVzIGhlbHBlciBmdW5jdGlvbnMgdG8gd29yayB3aXRoXG4gKiB7QGxpbmsgQ29sb3J9IGxpdGVyYWxzLlxuICovXG5leHBvcnQgdmFyIENvbG9yO1xuKGZ1bmN0aW9uIChDb2xvcikge1xuICAgIC8qKlxuICAgICAqIENyZWF0ZXMgYSBuZXcgQ29sb3IgbGl0ZXJhbC5cbiAgICAgKi9cbiAgICBmdW5jdGlvbiBjcmVhdGUocmVkLCBncmVlbiwgYmx1ZSwgYWxwaGEpIHtcbiAgICAgICAgcmV0dXJuIHtcbiAgICAgICAgICAgIHJlZCxcbiAgICAgICAgICAgIGdyZWVuLFxuICAgICAgICAgICAgYmx1ZSxcbiAgICAgICAgICAgIGFscGhhLFxuICAgICAgICB9O1xuICAgIH1cbiAgICBDb2xvci5jcmVhdGUgPSBjcmVhdGU7XG4gICAgLyoqXG4gICAgICogQ2hlY2tzIHdoZXRoZXIgdGhlIGdpdmVuIGxpdGVyYWwgY29uZm9ybXMgdG8gdGhlIHtAbGluayBDb2xvcn0gaW50ZXJmYWNlLlxuICAgICAqL1xuICAgIGZ1bmN0aW9uIGlzKHZhbHVlKSB7XG4gICAgICAgIGNvbnN0IGNhbmRpZGF0ZSA9IHZhbHVlO1xuICAgICAgICByZXR1cm4gSXMub2JqZWN0TGl0ZXJhbChjYW5kaWRhdGUpICYmIElzLm51bWJlclJhbmdlKGNhbmRpZGF0ZS5yZWQsIDAsIDEpXG4gICAgICAgICAgICAmJiBJcy5udW1iZXJSYW5nZShjYW5kaWRhdGUuZ3JlZW4sIDAsIDEpXG4gICAgICAgICAgICAmJiBJcy5udW1iZXJSYW5nZShjYW5kaWRhdGUuYmx1ZSwgMCwgMSlcbiAgICAgICAgICAgICYmIElzLm51bWJlclJhbmdlKGNhbmRpZGF0ZS5hbHBoYSwgMCwgMSk7XG4gICAgfVxuICAgIENvbG9yLmlzID0gaXM7XG59KShDb2xvciB8fCAoQ29sb3IgPSB7fSkpO1xuLyoqXG4gKiBUaGUgQ29sb3JJbmZvcm1hdGlvbiBuYW1lc3BhY2UgcHJvdmlkZXMgaGVscGVyIGZ1bmN0aW9ucyB0byB3b3JrIHdpdGhcbiAqIHtAbGluayBDb2xvckluZm9ybWF0aW9ufSBsaXRlcmFscy5cbiAqL1xuZXhwb3J0IHZhciBDb2xvckluZm9ybWF0aW9uO1xuKGZ1bmN0aW9uIChDb2xvckluZm9ybWF0aW9uKSB7XG4gICAgLyoqXG4gICAgICogQ3JlYXRlcyBhIG5ldyBDb2xvckluZm9ybWF0aW9uIGxpdGVyYWwuXG4gICAgICovXG4gICAgZnVuY3Rpb24gY3JlYXRlKHJhbmdlLCBjb2xvcikge1xuICAgICAgICByZXR1cm4ge1xuICAgICAgICAgICAgcmFuZ2UsXG4gICAgICAgICAgICBjb2xvcixcbiAgICAgICAgfTtcbiAgICB9XG4gICAgQ29sb3JJbmZvcm1hdGlvbi5jcmVhdGUgPSBjcmVhdGU7XG4gICAgLyoqXG4gICAgICogQ2hlY2tzIHdoZXRoZXIgdGhlIGdpdmVuIGxpdGVyYWwgY29uZm9ybXMgdG8gdGhlIHtAbGluayBDb2xvckluZm9ybWF0aW9ufSBpbnRlcmZhY2UuXG4gICAgICovXG4gICAgZnVuY3Rpb24gaXModmFsdWUpIHtcbiAgICAgICAgY29uc3QgY2FuZGlkYXRlID0gdmFsdWU7XG4gICAgICAgIHJldHVybiBJcy5vYmplY3RMaXRlcmFsKGNhbmRpZGF0ZSkgJiYgUmFuZ2UuaXMoY2FuZGlkYXRlLnJhbmdlKSAmJiBDb2xvci5pcyhjYW5kaWRhdGUuY29sb3IpO1xuICAgIH1cbiAgICBDb2xvckluZm9ybWF0aW9uLmlzID0gaXM7XG59KShDb2xvckluZm9ybWF0aW9uIHx8IChDb2xvckluZm9ybWF0aW9uID0ge30pKTtcbi8qKlxuICogVGhlIENvbG9yIG5hbWVzcGFjZSBwcm92aWRlcyBoZWxwZXIgZnVuY3Rpb25zIHRvIHdvcmsgd2l0aFxuICoge0BsaW5rIENvbG9yUHJlc2VudGF0aW9ufSBsaXRlcmFscy5cbiAqL1xuZXhwb3J0IHZhciBDb2xvclByZXNlbnRhdGlvbjtcbihmdW5jdGlvbiAoQ29sb3JQcmVzZW50YXRpb24pIHtcbiAgICAvKipcbiAgICAgKiBDcmVhdGVzIGEgbmV3IENvbG9ySW5mb3JtYXRpb24gbGl0ZXJhbC5cbiAgICAgKi9cbiAgICBmdW5jdGlvbiBjcmVhdGUobGFiZWwsIHRleHRFZGl0LCBhZGRpdGlvbmFsVGV4dEVkaXRzKSB7XG4gICAgICAgIHJldHVybiB7XG4gICAgICAgICAgICBsYWJlbCxcbiAgICAgICAgICAgIHRleHRFZGl0LFxuICAgICAgICAgICAgYWRkaXRpb25hbFRleHRFZGl0cyxcbiAgICAgICAgfTtcbiAgICB9XG4gICAgQ29sb3JQcmVzZW50YXRpb24uY3JlYXRlID0gY3JlYXRlO1xuICAgIC8qKlxuICAgICAqIENoZWNrcyB3aGV0aGVyIHRoZSBnaXZlbiBsaXRlcmFsIGNvbmZvcm1zIHRvIHRoZSB7QGxpbmsgQ29sb3JJbmZvcm1hdGlvbn0gaW50ZXJmYWNlLlxuICAgICAqL1xuICAgIGZ1bmN0aW9uIGlzKHZhbHVlKSB7XG4gICAgICAgIGNvbnN0IGNhbmRpZGF0ZSA9IHZhbHVlO1xuICAgICAgICByZXR1cm4gSXMub2JqZWN0TGl0ZXJhbChjYW5kaWRhdGUpICYmIElzLnN0cmluZyhjYW5kaWRhdGUubGFiZWwpXG4gICAgICAgICAgICAmJiAoSXMudW5kZWZpbmVkKGNhbmRpZGF0ZS50ZXh0RWRpdCkgfHwgVGV4dEVkaXQuaXMoY2FuZGlkYXRlKSlcbiAgICAgICAgICAgICYmIChJcy51bmRlZmluZWQoY2FuZGlkYXRlLmFkZGl0aW9uYWxUZXh0RWRpdHMpIHx8IElzLnR5cGVkQXJyYXkoY2FuZGlkYXRlLmFkZGl0aW9uYWxUZXh0RWRpdHMsIFRleHRFZGl0LmlzKSk7XG4gICAgfVxuICAgIENvbG9yUHJlc2VudGF0aW9uLmlzID0gaXM7XG59KShDb2xvclByZXNlbnRhdGlvbiB8fCAoQ29sb3JQcmVzZW50YXRpb24gPSB7fSkpO1xuLyoqXG4gKiBBIHNldCBvZiBwcmVkZWZpbmVkIHJhbmdlIGtpbmRzLlxuICovXG5leHBvcnQgdmFyIEZvbGRpbmdSYW5nZUtpbmQ7XG4oZnVuY3Rpb24gKEZvbGRpbmdSYW5nZUtpbmQpIHtcbiAgICAvKipcbiAgICAgKiBGb2xkaW5nIHJhbmdlIGZvciBhIGNvbW1lbnRcbiAgICAgKi9cbiAgICBGb2xkaW5nUmFuZ2VLaW5kLkNvbW1lbnQgPSAnY29tbWVudCc7XG4gICAgLyoqXG4gICAgICogRm9sZGluZyByYW5nZSBmb3IgYW4gaW1wb3J0IG9yIGluY2x1ZGVcbiAgICAgKi9cbiAgICBGb2xkaW5nUmFuZ2VLaW5kLkltcG9ydHMgPSAnaW1wb3J0cyc7XG4gICAgLyoqXG4gICAgICogRm9sZGluZyByYW5nZSBmb3IgYSByZWdpb24gKGUuZy4gYCNyZWdpb25gKVxuICAgICAqL1xuICAgIEZvbGRpbmdSYW5nZUtpbmQuUmVnaW9uID0gJ3JlZ2lvbic7XG59KShGb2xkaW5nUmFuZ2VLaW5kIHx8IChGb2xkaW5nUmFuZ2VLaW5kID0ge30pKTtcbi8qKlxuICogVGhlIGZvbGRpbmcgcmFuZ2UgbmFtZXNwYWNlIHByb3ZpZGVzIGhlbHBlciBmdW5jdGlvbnMgdG8gd29yayB3aXRoXG4gKiB7QGxpbmsgRm9sZGluZ1JhbmdlfSBsaXRlcmFscy5cbiAqL1xuZXhwb3J0IHZhciBGb2xkaW5nUmFuZ2U7XG4oZnVuY3Rpb24gKEZvbGRpbmdSYW5nZSkge1xuICAgIC8qKlxuICAgICAqIENyZWF0ZXMgYSBuZXcgRm9sZGluZ1JhbmdlIGxpdGVyYWwuXG4gICAgICovXG4gICAgZnVuY3Rpb24gY3JlYXRlKHN0YXJ0TGluZSwgZW5kTGluZSwgc3RhcnRDaGFyYWN0ZXIsIGVuZENoYXJhY3Rlciwga2luZCwgY29sbGFwc2VkVGV4dCkge1xuICAgICAgICBjb25zdCByZXN1bHQgPSB7XG4gICAgICAgICAgICBzdGFydExpbmUsXG4gICAgICAgICAgICBlbmRMaW5lXG4gICAgICAgIH07XG4gICAgICAgIGlmIChJcy5kZWZpbmVkKHN0YXJ0Q2hhcmFjdGVyKSkge1xuICAgICAgICAgICAgcmVzdWx0LnN0YXJ0Q2hhcmFjdGVyID0gc3RhcnRDaGFyYWN0ZXI7XG4gICAgICAgIH1cbiAgICAgICAgaWYgKElzLmRlZmluZWQoZW5kQ2hhcmFjdGVyKSkge1xuICAgICAgICAgICAgcmVzdWx0LmVuZENoYXJhY3RlciA9IGVuZENoYXJhY3RlcjtcbiAgICAgICAgfVxuICAgICAgICBpZiAoSXMuZGVmaW5lZChraW5kKSkge1xuICAgICAgICAgICAgcmVzdWx0LmtpbmQgPSBraW5kO1xuICAgICAgICB9XG4gICAgICAgIGlmIChJcy5kZWZpbmVkKGNvbGxhcHNlZFRleHQpKSB7XG4gICAgICAgICAgICByZXN1bHQuY29sbGFwc2VkVGV4dCA9IGNvbGxhcHNlZFRleHQ7XG4gICAgICAgIH1cbiAgICAgICAgcmV0dXJuIHJlc3VsdDtcbiAgICB9XG4gICAgRm9sZGluZ1JhbmdlLmNyZWF0ZSA9IGNyZWF0ZTtcbiAgICAvKipcbiAgICAgKiBDaGVja3Mgd2hldGhlciB0aGUgZ2l2ZW4gbGl0ZXJhbCBjb25mb3JtcyB0byB0aGUge0BsaW5rIEZvbGRpbmdSYW5nZX0gaW50ZXJmYWNlLlxuICAgICAqL1xuICAgIGZ1bmN0aW9uIGlzKHZhbHVlKSB7XG4gICAgICAgIGNvbnN0IGNhbmRpZGF0ZSA9IHZhbHVlO1xuICAgICAgICByZXR1cm4gSXMub2JqZWN0TGl0ZXJhbChjYW5kaWRhdGUpICYmIElzLnVpbnRlZ2VyKGNhbmRpZGF0ZS5zdGFydExpbmUpICYmIElzLnVpbnRlZ2VyKGNhbmRpZGF0ZS5zdGFydExpbmUpXG4gICAgICAgICAgICAmJiAoSXMudW5kZWZpbmVkKGNhbmRpZGF0ZS5zdGFydENoYXJhY3RlcikgfHwgSXMudWludGVnZXIoY2FuZGlkYXRlLnN0YXJ0Q2hhcmFjdGVyKSlcbiAgICAgICAgICAgICYmIChJcy51bmRlZmluZWQoY2FuZGlkYXRlLmVuZENoYXJhY3RlcikgfHwgSXMudWludGVnZXIoY2FuZGlkYXRlLmVuZENoYXJhY3RlcikpXG4gICAgICAgICAgICAmJiAoSXMudW5kZWZpbmVkKGNhbmRpZGF0ZS5raW5kKSB8fCBJcy5zdHJpbmcoY2FuZGlkYXRlLmtpbmQpKTtcbiAgICB9XG4gICAgRm9sZGluZ1JhbmdlLmlzID0gaXM7XG59KShGb2xkaW5nUmFuZ2UgfHwgKEZvbGRpbmdSYW5nZSA9IHt9KSk7XG4vKipcbiAqIFRoZSBEaWFnbm9zdGljUmVsYXRlZEluZm9ybWF0aW9uIG5hbWVzcGFjZSBwcm92aWRlcyBoZWxwZXIgZnVuY3Rpb25zIHRvIHdvcmsgd2l0aFxuICoge0BsaW5rIERpYWdub3N0aWNSZWxhdGVkSW5mb3JtYXRpb259IGxpdGVyYWxzLlxuICovXG5leHBvcnQgdmFyIERpYWdub3N0aWNSZWxhdGVkSW5mb3JtYXRpb247XG4oZnVuY3Rpb24gKERpYWdub3N0aWNSZWxhdGVkSW5mb3JtYXRpb24pIHtcbiAgICAvKipcbiAgICAgKiBDcmVhdGVzIGEgbmV3IERpYWdub3N0aWNSZWxhdGVkSW5mb3JtYXRpb24gbGl0ZXJhbC5cbiAgICAgKi9cbiAgICBmdW5jdGlvbiBjcmVhdGUobG9jYXRpb24sIG1lc3NhZ2UpIHtcbiAgICAgICAgcmV0dXJuIHtcbiAgICAgICAgICAgIGxvY2F0aW9uLFxuICAgICAgICAgICAgbWVzc2FnZVxuICAgICAgICB9O1xuICAgIH1cbiAgICBEaWFnbm9zdGljUmVsYXRlZEluZm9ybWF0aW9uLmNyZWF0ZSA9IGNyZWF0ZTtcbiAgICAvKipcbiAgICAgKiBDaGVja3Mgd2hldGhlciB0aGUgZ2l2ZW4gbGl0ZXJhbCBjb25mb3JtcyB0byB0aGUge0BsaW5rIERpYWdub3N0aWNSZWxhdGVkSW5mb3JtYXRpb259IGludGVyZmFjZS5cbiAgICAgKi9cbiAgICBmdW5jdGlvbiBpcyh2YWx1ZSkge1xuICAgICAgICBjb25zdCBjYW5kaWRhdGUgPSB2YWx1ZTtcbiAgICAgICAgcmV0dXJuIElzLmRlZmluZWQoY2FuZGlkYXRlKSAmJiBMb2NhdGlvbi5pcyhjYW5kaWRhdGUubG9jYXRpb24pICYmIElzLnN0cmluZyhjYW5kaWRhdGUubWVzc2FnZSk7XG4gICAgfVxuICAgIERpYWdub3N0aWNSZWxhdGVkSW5mb3JtYXRpb24uaXMgPSBpcztcbn0pKERpYWdub3N0aWNSZWxhdGVkSW5mb3JtYXRpb24gfHwgKERpYWdub3N0aWNSZWxhdGVkSW5mb3JtYXRpb24gPSB7fSkpO1xuLyoqXG4gKiBUaGUgZGlhZ25vc3RpYydzIHNldmVyaXR5LlxuICovXG5leHBvcnQgdmFyIERpYWdub3N0aWNTZXZlcml0eTtcbihmdW5jdGlvbiAoRGlhZ25vc3RpY1NldmVyaXR5KSB7XG4gICAgLyoqXG4gICAgICogUmVwb3J0cyBhbiBlcnJvci5cbiAgICAgKi9cbiAgICBEaWFnbm9zdGljU2V2ZXJpdHkuRXJyb3IgPSAxO1xuICAgIC8qKlxuICAgICAqIFJlcG9ydHMgYSB3YXJuaW5nLlxuICAgICAqL1xuICAgIERpYWdub3N0aWNTZXZlcml0eS5XYXJuaW5nID0gMjtcbiAgICAvKipcbiAgICAgKiBSZXBvcnRzIGFuIGluZm9ybWF0aW9uLlxuICAgICAqL1xuICAgIERpYWdub3N0aWNTZXZlcml0eS5JbmZvcm1hdGlvbiA9IDM7XG4gICAgLyoqXG4gICAgICogUmVwb3J0cyBhIGhpbnQuXG4gICAgICovXG4gICAgRGlhZ25vc3RpY1NldmVyaXR5LkhpbnQgPSA0O1xufSkoRGlhZ25vc3RpY1NldmVyaXR5IHx8IChEaWFnbm9zdGljU2V2ZXJpdHkgPSB7fSkpO1xuLyoqXG4gKiBUaGUgZGlhZ25vc3RpYyB0YWdzLlxuICpcbiAqIEBzaW5jZSAzLjE1LjBcbiAqL1xuZXhwb3J0IHZhciBEaWFnbm9zdGljVGFnO1xuKGZ1bmN0aW9uIChEaWFnbm9zdGljVGFnKSB7XG4gICAgLyoqXG4gICAgICogVW51c2VkIG9yIHVubmVjZXNzYXJ5IGNvZGUuXG4gICAgICpcbiAgICAgKiBDbGllbnRzIGFyZSBhbGxvd2VkIHRvIHJlbmRlciBkaWFnbm9zdGljcyB3aXRoIHRoaXMgdGFnIGZhZGVkIG91dCBpbnN0ZWFkIG9mIGhhdmluZ1xuICAgICAqIGFuIGVycm9yIHNxdWlnZ2xlLlxuICAgICAqL1xuICAgIERpYWdub3N0aWNUYWcuVW5uZWNlc3NhcnkgPSAxO1xuICAgIC8qKlxuICAgICAqIERlcHJlY2F0ZWQgb3Igb2Jzb2xldGUgY29kZS5cbiAgICAgKlxuICAgICAqIENsaWVudHMgYXJlIGFsbG93ZWQgdG8gcmVuZGVyZWQgZGlhZ25vc3RpY3Mgd2l0aCB0aGlzIHRhZyBzdHJpa2UgdGhyb3VnaC5cbiAgICAgKi9cbiAgICBEaWFnbm9zdGljVGFnLkRlcHJlY2F0ZWQgPSAyO1xufSkoRGlhZ25vc3RpY1RhZyB8fCAoRGlhZ25vc3RpY1RhZyA9IHt9KSk7XG4vKipcbiAqIFRoZSBDb2RlRGVzY3JpcHRpb24gbmFtZXNwYWNlIHByb3ZpZGVzIGZ1bmN0aW9ucyB0byBkZWFsIHdpdGggZGVzY3JpcHRpb25zIGZvciBkaWFnbm9zdGljIGNvZGVzLlxuICpcbiAqIEBzaW5jZSAzLjE2LjBcbiAqL1xuZXhwb3J0IHZhciBDb2RlRGVzY3JpcHRpb247XG4oZnVuY3Rpb24gKENvZGVEZXNjcmlwdGlvbikge1xuICAgIGZ1bmN0aW9uIGlzKHZhbHVlKSB7XG4gICAgICAgIGNvbnN0IGNhbmRpZGF0ZSA9IHZhbHVlO1xuICAgICAgICByZXR1cm4gSXMub2JqZWN0TGl0ZXJhbChjYW5kaWRhdGUpICYmIElzLnN0cmluZyhjYW5kaWRhdGUuaHJlZik7XG4gICAgfVxuICAgIENvZGVEZXNjcmlwdGlvbi5pcyA9IGlzO1xufSkoQ29kZURlc2NyaXB0aW9uIHx8IChDb2RlRGVzY3JpcHRpb24gPSB7fSkpO1xuLyoqXG4gKiBUaGUgRGlhZ25vc3RpYyBuYW1lc3BhY2UgcHJvdmlkZXMgaGVscGVyIGZ1bmN0aW9ucyB0byB3b3JrIHdpdGhcbiAqIHtAbGluayBEaWFnbm9zdGljfSBsaXRlcmFscy5cbiAqL1xuZXhwb3J0IHZhciBEaWFnbm9zdGljO1xuKGZ1bmN0aW9uIChEaWFnbm9zdGljKSB7XG4gICAgLyoqXG4gICAgICogQ3JlYXRlcyBhIG5ldyBEaWFnbm9zdGljIGxpdGVyYWwuXG4gICAgICovXG4gICAgZnVuY3Rpb24gY3JlYXRlKHJhbmdlLCBtZXNzYWdlLCBzZXZlcml0eSwgY29kZSwgc291cmNlLCByZWxhdGVkSW5mb3JtYXRpb24pIHtcbiAgICAgICAgY29uc3QgcmVzdWx0ID0geyByYW5nZSwgbWVzc2FnZSB9O1xuICAgICAgICBpZiAoSXMuZGVmaW5lZChzZXZlcml0eSkpIHtcbiAgICAgICAgICAgIHJlc3VsdC5zZXZlcml0eSA9IHNldmVyaXR5O1xuICAgICAgICB9XG4gICAgICAgIGlmIChJcy5kZWZpbmVkKGNvZGUpKSB7XG4gICAgICAgICAgICByZXN1bHQuY29kZSA9IGNvZGU7XG4gICAgICAgIH1cbiAgICAgICAgaWYgKElzLmRlZmluZWQoc291cmNlKSkge1xuICAgICAgICAgICAgcmVzdWx0LnNvdXJjZSA9IHNvdXJjZTtcbiAgICAgICAgfVxuICAgICAgICBpZiAoSXMuZGVmaW5lZChyZWxhdGVkSW5mb3JtYXRpb24pKSB7XG4gICAgICAgICAgICByZXN1bHQucmVsYXRlZEluZm9ybWF0aW9uID0gcmVsYXRlZEluZm9ybWF0aW9uO1xuICAgICAgICB9XG4gICAgICAgIHJldHVybiByZXN1bHQ7XG4gICAgfVxuICAgIERpYWdub3N0aWMuY3JlYXRlID0gY3JlYXRlO1xuICAgIC8qKlxuICAgICAqIENoZWNrcyB3aGV0aGVyIHRoZSBnaXZlbiBsaXRlcmFsIGNvbmZvcm1zIHRvIHRoZSB7QGxpbmsgRGlhZ25vc3RpY30gaW50ZXJmYWNlLlxuICAgICAqL1xuICAgIGZ1bmN0aW9uIGlzKHZhbHVlKSB7XG4gICAgICAgIHZhciBfYTtcbiAgICAgICAgY29uc3QgY2FuZGlkYXRlID0gdmFsdWU7XG4gICAgICAgIHJldHVybiBJcy5kZWZpbmVkKGNhbmRpZGF0ZSlcbiAgICAgICAgICAgICYmIFJhbmdlLmlzKGNhbmRpZGF0ZS5yYW5nZSlcbiAgICAgICAgICAgICYmIChJcy5zdHJpbmcoY2FuZGlkYXRlLm1lc3NhZ2UpIHx8IE1hcmt1cENvbnRlbnQuaXMoY2FuZGlkYXRlLm1lc3NhZ2UpKVxuICAgICAgICAgICAgJiYgKElzLm51bWJlcihjYW5kaWRhdGUuc2V2ZXJpdHkpIHx8IElzLnVuZGVmaW5lZChjYW5kaWRhdGUuc2V2ZXJpdHkpKVxuICAgICAgICAgICAgJiYgKElzLmludGVnZXIoY2FuZGlkYXRlLmNvZGUpIHx8IElzLnN0cmluZyhjYW5kaWRhdGUuY29kZSkgfHwgSXMudW5kZWZpbmVkKGNhbmRpZGF0ZS5jb2RlKSlcbiAgICAgICAgICAgICYmIChJcy51bmRlZmluZWQoY2FuZGlkYXRlLmNvZGVEZXNjcmlwdGlvbikgfHwgKElzLnN0cmluZygoX2EgPSBjYW5kaWRhdGUuY29kZURlc2NyaXB0aW9uKSA9PT0gbnVsbCB8fCBfYSA9PT0gdm9pZCAwID8gdm9pZCAwIDogX2EuaHJlZikpKVxuICAgICAgICAgICAgJiYgKElzLnN0cmluZyhjYW5kaWRhdGUuc291cmNlKSB8fCBJcy51bmRlZmluZWQoY2FuZGlkYXRlLnNvdXJjZSkpXG4gICAgICAgICAgICAmJiAoSXMudW5kZWZpbmVkKGNhbmRpZGF0ZS5yZWxhdGVkSW5mb3JtYXRpb24pIHx8IElzLnR5cGVkQXJyYXkoY2FuZGlkYXRlLnJlbGF0ZWRJbmZvcm1hdGlvbiwgRGlhZ25vc3RpY1JlbGF0ZWRJbmZvcm1hdGlvbi5pcykpO1xuICAgIH1cbiAgICBEaWFnbm9zdGljLmlzID0gaXM7XG4gICAgLyoqXG4gICAgICogQ2hlY2tzIHdoZXRoZXIgdGhlIGdpdmVuIGRpYWdub3N0aWMncyBtZXNzYWdlIGNvbmZvcm1zIHRvIHRoZSAzLjE3LjBcbiAgICAgKiB2ZXJzaW9uIG9mIHRoZSBwcm90b2NvbCB3aGVyZSB0aGUgbWVzc2FnZSBpcyBhIHN0cmluZy5cbiAgICAgKlxuICAgICAqIEBwYXJhbSB2YWx1ZSB0aGUgZGlhZ25vc3RpY1xuICAgICAqIEByZXR1cm5zIHRydWUgaWYgdGhlIGRpYWdub3N0aWMncyBtZXNzYWdlIGlzIGEgc3RyaW5nLCBmYWxzZSBvdGhlcndpc2UuXG4gICAgICovXG4gICAgZnVuY3Rpb24gaXMzXzE3KHZhbHVlKSB7XG4gICAgICAgIHJldHVybiBJcy5zdHJpbmcodmFsdWUubWVzc2FnZSk7XG4gICAgfVxuICAgIERpYWdub3N0aWMuaXMzXzE3ID0gaXMzXzE3O1xuICAgIC8qKlxuICAgICAqIEdldHMgdGhlIG1lc3NhZ2Ugc3RyaW5nIG9mIGEgZGlhZ25vc3RpYy4gSWYgdGhlIG1lc3NhZ2UgaXMgYWxyZWFkeSBhXG4gICAgICogc3RyaW5nLCBpdCBpcyByZXR1cm5lZCBhcyBpcy4gSWYgdGhlIG1lc3NhZ2UgaXMgYSBNYXJrdXBDb250ZW50LFxuICAgICAqIHRoZSB2YWx1ZSBvZiB0aGUgTWFya3VwQ29udGVudCBpcyByZXR1cm5lZC4gT3RoZXJ3aXNlIGFuIGVycm9yIGlzIHRocm93bi5cbiAgICAgKlxuICAgICAqIEBwYXJhbSBkaWFnbm9zdGljIHRoZSBkaWFnbm9zdGljIHRvIGdldCB0aGUgbWVzc2FnZSBzdHJpbmcgZnJvbS5cbiAgICAgKiBAcmV0dXJucyB0aGUgbWVzc2FnZSBzdHJpbmcgb2YgdGhlIGdpdmVuIGRpYWdub3N0aWMuXG4gICAgICovXG4gICAgZnVuY3Rpb24gZ2V0TWVzc2FnZVN0cmluZyhkaWFnbm9zdGljKSB7XG4gICAgICAgIGlmIChJcy5zdHJpbmcoZGlhZ25vc3RpYy5tZXNzYWdlKSkge1xuICAgICAgICAgICAgcmV0dXJuIGRpYWdub3N0aWMubWVzc2FnZTtcbiAgICAgICAgfVxuICAgICAgICBlbHNlIGlmIChNYXJrdXBDb250ZW50LmlzKGRpYWdub3N0aWMubWVzc2FnZSkpIHtcbiAgICAgICAgICAgIHJldHVybiBkaWFnbm9zdGljLm1lc3NhZ2UudmFsdWU7XG4gICAgICAgIH1cbiAgICAgICAgZWxzZSB7XG4gICAgICAgICAgICB0aHJvdyBuZXcgRXJyb3IoYFVua25vd24gbWVzc2FnZSB0eXBlICR7dHlwZW9mIGRpYWdub3N0aWMubWVzc2FnZX1gKTtcbiAgICAgICAgfVxuICAgIH1cbiAgICBEaWFnbm9zdGljLmdldE1lc3NhZ2VTdHJpbmcgPSBnZXRNZXNzYWdlU3RyaW5nO1xufSkoRGlhZ25vc3RpYyB8fCAoRGlhZ25vc3RpYyA9IHt9KSk7XG4vKipcbiAqIFRoZSBDb21tYW5kIG5hbWVzcGFjZSBwcm92aWRlcyBoZWxwZXIgZnVuY3Rpb25zIHRvIHdvcmsgd2l0aFxuICoge0BsaW5rIENvbW1hbmR9IGxpdGVyYWxzLlxuICovXG5leHBvcnQgdmFyIENvbW1hbmQ7XG4oZnVuY3Rpb24gKENvbW1hbmQpIHtcbiAgICAvKipcbiAgICAgKiBDcmVhdGVzIGEgbmV3IENvbW1hbmQgbGl0ZXJhbC5cbiAgICAgKi9cbiAgICBmdW5jdGlvbiBjcmVhdGUodGl0bGUsIGNvbW1hbmQsIC4uLmFyZ3MpIHtcbiAgICAgICAgY29uc3QgcmVzdWx0ID0geyB0aXRsZSwgY29tbWFuZCB9O1xuICAgICAgICBpZiAoSXMuZGVmaW5lZChhcmdzKSAmJiBhcmdzLmxlbmd0aCA+IDApIHtcbiAgICAgICAgICAgIHJlc3VsdC5hcmd1bWVudHMgPSBhcmdzO1xuICAgICAgICB9XG4gICAgICAgIHJldHVybiByZXN1bHQ7XG4gICAgfVxuICAgIENvbW1hbmQuY3JlYXRlID0gY3JlYXRlO1xuICAgIC8qKlxuICAgICAqIENoZWNrcyB3aGV0aGVyIHRoZSBnaXZlbiBsaXRlcmFsIGNvbmZvcm1zIHRvIHRoZSB7QGxpbmsgQ29tbWFuZH0gaW50ZXJmYWNlLlxuICAgICAqL1xuICAgIGZ1bmN0aW9uIGlzKHZhbHVlKSB7XG4gICAgICAgIGNvbnN0IGNhbmRpZGF0ZSA9IHZhbHVlO1xuICAgICAgICByZXR1cm4gSXMuZGVmaW5lZChjYW5kaWRhdGUpICYmIElzLnN0cmluZyhjYW5kaWRhdGUudGl0bGUpICYmIChjYW5kaWRhdGUudG9vbHRpcCA9PT0gdW5kZWZpbmVkIHx8IElzLnN0cmluZyhjYW5kaWRhdGUudG9vbHRpcCkpICYmIElzLnN0cmluZyhjYW5kaWRhdGUuY29tbWFuZCk7XG4gICAgfVxuICAgIENvbW1hbmQuaXMgPSBpcztcbn0pKENvbW1hbmQgfHwgKENvbW1hbmQgPSB7fSkpO1xuLyoqXG4gKiBUaGUgVGV4dEVkaXQgbmFtZXNwYWNlIHByb3ZpZGVzIGhlbHBlciBmdW5jdGlvbiB0byBjcmVhdGUgcmVwbGFjZSxcbiAqIGluc2VydCBhbmQgZGVsZXRlIGVkaXRzIG1vcmUgZWFzaWx5LlxuICovXG5leHBvcnQgdmFyIFRleHRFZGl0O1xuKGZ1bmN0aW9uIChUZXh0RWRpdCkge1xuICAgIC8qKlxuICAgICAqIENyZWF0ZXMgYSByZXBsYWNlIHRleHQgZWRpdC5cbiAgICAgKiBAcGFyYW0gcmFuZ2UgVGhlIHJhbmdlIG9mIHRleHQgdG8gYmUgcmVwbGFjZWQuXG4gICAgICogQHBhcmFtIG5ld1RleHQgVGhlIG5ldyB0ZXh0LlxuICAgICAqL1xuICAgIGZ1bmN0aW9uIHJlcGxhY2UocmFuZ2UsIG5ld1RleHQpIHtcbiAgICAgICAgcmV0dXJuIHsgcmFuZ2UsIG5ld1RleHQgfTtcbiAgICB9XG4gICAgVGV4dEVkaXQucmVwbGFjZSA9IHJlcGxhY2U7XG4gICAgLyoqXG4gICAgICogQ3JlYXRlcyBhbiBpbnNlcnQgdGV4dCBlZGl0LlxuICAgICAqIEBwYXJhbSBwb3NpdGlvbiBUaGUgcG9zaXRpb24gdG8gaW5zZXJ0IHRoZSB0ZXh0IGF0LlxuICAgICAqIEBwYXJhbSBuZXdUZXh0IFRoZSB0ZXh0IHRvIGJlIGluc2VydGVkLlxuICAgICAqL1xuICAgIGZ1bmN0aW9uIGluc2VydChwb3NpdGlvbiwgbmV3VGV4dCkge1xuICAgICAgICByZXR1cm4geyByYW5nZTogeyBzdGFydDogcG9zaXRpb24sIGVuZDogcG9zaXRpb24gfSwgbmV3VGV4dCB9O1xuICAgIH1cbiAgICBUZXh0RWRpdC5pbnNlcnQgPSBpbnNlcnQ7XG4gICAgLyoqXG4gICAgICogQ3JlYXRlcyBhIGRlbGV0ZSB0ZXh0IGVkaXQuXG4gICAgICogQHBhcmFtIHJhbmdlIFRoZSByYW5nZSBvZiB0ZXh0IHRvIGJlIGRlbGV0ZWQuXG4gICAgICovXG4gICAgZnVuY3Rpb24gZGVsKHJhbmdlKSB7XG4gICAgICAgIHJldHVybiB7IHJhbmdlLCBuZXdUZXh0OiAnJyB9O1xuICAgIH1cbiAgICBUZXh0RWRpdC5kZWwgPSBkZWw7XG4gICAgZnVuY3Rpb24gaXModmFsdWUpIHtcbiAgICAgICAgY29uc3QgY2FuZGlkYXRlID0gdmFsdWU7XG4gICAgICAgIHJldHVybiBJcy5vYmplY3RMaXRlcmFsKGNhbmRpZGF0ZSlcbiAgICAgICAgICAgICYmIElzLnN0cmluZyhjYW5kaWRhdGUubmV3VGV4dClcbiAgICAgICAgICAgICYmIFJhbmdlLmlzKGNhbmRpZGF0ZS5yYW5nZSk7XG4gICAgfVxuICAgIFRleHRFZGl0LmlzID0gaXM7XG59KShUZXh0RWRpdCB8fCAoVGV4dEVkaXQgPSB7fSkpO1xuZXhwb3J0IHZhciBDaGFuZ2VBbm5vdGF0aW9uO1xuKGZ1bmN0aW9uIChDaGFuZ2VBbm5vdGF0aW9uKSB7XG4gICAgZnVuY3Rpb24gY3JlYXRlKGxhYmVsLCBuZWVkc0NvbmZpcm1hdGlvbiwgZGVzY3JpcHRpb24pIHtcbiAgICAgICAgY29uc3QgcmVzdWx0ID0geyBsYWJlbCB9O1xuICAgICAgICBpZiAobmVlZHNDb25maXJtYXRpb24gIT09IHVuZGVmaW5lZCkge1xuICAgICAgICAgICAgcmVzdWx0Lm5lZWRzQ29uZmlybWF0aW9uID0gbmVlZHNDb25maXJtYXRpb247XG4gICAgICAgIH1cbiAgICAgICAgaWYgKGRlc2NyaXB0aW9uICE9PSB1bmRlZmluZWQpIHtcbiAgICAgICAgICAgIHJlc3VsdC5kZXNjcmlwdGlvbiA9IGRlc2NyaXB0aW9uO1xuICAgICAgICB9XG4gICAgICAgIHJldHVybiByZXN1bHQ7XG4gICAgfVxuICAgIENoYW5nZUFubm90YXRpb24uY3JlYXRlID0gY3JlYXRlO1xuICAgIGZ1bmN0aW9uIGlzKHZhbHVlKSB7XG4gICAgICAgIGNvbnN0IGNhbmRpZGF0ZSA9IHZhbHVlO1xuICAgICAgICByZXR1cm4gSXMub2JqZWN0TGl0ZXJhbChjYW5kaWRhdGUpICYmIElzLnN0cmluZyhjYW5kaWRhdGUubGFiZWwpICYmXG4gICAgICAgICAgICAoSXMuYm9vbGVhbihjYW5kaWRhdGUubmVlZHNDb25maXJtYXRpb24pIHx8IGNhbmRpZGF0ZS5uZWVkc0NvbmZpcm1hdGlvbiA9PT0gdW5kZWZpbmVkKSAmJlxuICAgICAgICAgICAgKElzLnN0cmluZyhjYW5kaWRhdGUuZGVzY3JpcHRpb24pIHx8IGNhbmRpZGF0ZS5kZXNjcmlwdGlvbiA9PT0gdW5kZWZpbmVkKTtcbiAgICB9XG4gICAgQ2hhbmdlQW5ub3RhdGlvbi5pcyA9IGlzO1xufSkoQ2hhbmdlQW5ub3RhdGlvbiB8fCAoQ2hhbmdlQW5ub3RhdGlvbiA9IHt9KSk7XG5leHBvcnQgdmFyIENoYW5nZUFubm90YXRpb25JZGVudGlmaWVyO1xuKGZ1bmN0aW9uIChDaGFuZ2VBbm5vdGF0aW9uSWRlbnRpZmllcikge1xuICAgIGZ1bmN0aW9uIGlzKHZhbHVlKSB7XG4gICAgICAgIGNvbnN0IGNhbmRpZGF0ZSA9IHZhbHVlO1xuICAgICAgICByZXR1cm4gSXMuc3RyaW5nKGNhbmRpZGF0ZSk7XG4gICAgfVxuICAgIENoYW5nZUFubm90YXRpb25JZGVudGlmaWVyLmlzID0gaXM7XG59KShDaGFuZ2VBbm5vdGF0aW9uSWRlbnRpZmllciB8fCAoQ2hhbmdlQW5ub3RhdGlvbklkZW50aWZpZXIgPSB7fSkpO1xuZXhwb3J0IHZhciBBbm5vdGF0ZWRUZXh0RWRpdDtcbihmdW5jdGlvbiAoQW5ub3RhdGVkVGV4dEVkaXQpIHtcbiAgICAvKipcbiAgICAgKiBDcmVhdGVzIGFuIGFubm90YXRlZCByZXBsYWNlIHRleHQgZWRpdC5cbiAgICAgKlxuICAgICAqIEBwYXJhbSByYW5nZSBUaGUgcmFuZ2Ugb2YgdGV4dCB0byBiZSByZXBsYWNlZC5cbiAgICAgKiBAcGFyYW0gbmV3VGV4dCBUaGUgbmV3IHRleHQuXG4gICAgICogQHBhcmFtIGFubm90YXRpb24gVGhlIGFubm90YXRpb24uXG4gICAgICovXG4gICAgZnVuY3Rpb24gcmVwbGFjZShyYW5nZSwgbmV3VGV4dCwgYW5ub3RhdGlvbikge1xuICAgICAgICByZXR1cm4geyByYW5nZSwgbmV3VGV4dCwgYW5ub3RhdGlvbklkOiBhbm5vdGF0aW9uIH07XG4gICAgfVxuICAgIEFubm90YXRlZFRleHRFZGl0LnJlcGxhY2UgPSByZXBsYWNlO1xuICAgIC8qKlxuICAgICAqIENyZWF0ZXMgYW4gYW5ub3RhdGVkIGluc2VydCB0ZXh0IGVkaXQuXG4gICAgICpcbiAgICAgKiBAcGFyYW0gcG9zaXRpb24gVGhlIHBvc2l0aW9uIHRvIGluc2VydCB0aGUgdGV4dCBhdC5cbiAgICAgKiBAcGFyYW0gbmV3VGV4dCBUaGUgdGV4dCB0byBiZSBpbnNlcnRlZC5cbiAgICAgKiBAcGFyYW0gYW5ub3RhdGlvbiBUaGUgYW5ub3RhdGlvbi5cbiAgICAgKi9cbiAgICBmdW5jdGlvbiBpbnNlcnQocG9zaXRpb24sIG5ld1RleHQsIGFubm90YXRpb24pIHtcbiAgICAgICAgcmV0dXJuIHsgcmFuZ2U6IHsgc3RhcnQ6IHBvc2l0aW9uLCBlbmQ6IHBvc2l0aW9uIH0sIG5ld1RleHQsIGFubm90YXRpb25JZDogYW5ub3RhdGlvbiB9O1xuICAgIH1cbiAgICBBbm5vdGF0ZWRUZXh0RWRpdC5pbnNlcnQgPSBpbnNlcnQ7XG4gICAgLyoqXG4gICAgICogQ3JlYXRlcyBhbiBhbm5vdGF0ZWQgZGVsZXRlIHRleHQgZWRpdC5cbiAgICAgKlxuICAgICAqIEBwYXJhbSByYW5nZSBUaGUgcmFuZ2Ugb2YgdGV4dCB0byBiZSBkZWxldGVkLlxuICAgICAqIEBwYXJhbSBhbm5vdGF0aW9uIFRoZSBhbm5vdGF0aW9uLlxuICAgICAqL1xuICAgIGZ1bmN0aW9uIGRlbChyYW5nZSwgYW5ub3RhdGlvbikge1xuICAgICAgICByZXR1cm4geyByYW5nZSwgbmV3VGV4dDogJycsIGFubm90YXRpb25JZDogYW5ub3RhdGlvbiB9O1xuICAgIH1cbiAgICBBbm5vdGF0ZWRUZXh0RWRpdC5kZWwgPSBkZWw7XG4gICAgZnVuY3Rpb24gaXModmFsdWUpIHtcbiAgICAgICAgY29uc3QgY2FuZGlkYXRlID0gdmFsdWU7XG4gICAgICAgIHJldHVybiBUZXh0RWRpdC5pcyhjYW5kaWRhdGUpICYmIChDaGFuZ2VBbm5vdGF0aW9uLmlzKGNhbmRpZGF0ZS5hbm5vdGF0aW9uSWQpIHx8IENoYW5nZUFubm90YXRpb25JZGVudGlmaWVyLmlzKGNhbmRpZGF0ZS5hbm5vdGF0aW9uSWQpKTtcbiAgICB9XG4gICAgQW5ub3RhdGVkVGV4dEVkaXQuaXMgPSBpcztcbn0pKEFubm90YXRlZFRleHRFZGl0IHx8IChBbm5vdGF0ZWRUZXh0RWRpdCA9IHt9KSk7XG4vKipcbiAqIFRoZSBUZXh0RG9jdW1lbnRFZGl0IG5hbWVzcGFjZSBwcm92aWRlcyBoZWxwZXIgZnVuY3Rpb24gdG8gY3JlYXRlXG4gKiBhbiBlZGl0IHRoYXQgbWFuaXB1bGF0ZXMgYSB0ZXh0IGRvY3VtZW50LlxuICovXG5leHBvcnQgdmFyIFRleHREb2N1bWVudEVkaXQ7XG4oZnVuY3Rpb24gKFRleHREb2N1bWVudEVkaXQpIHtcbiAgICAvKipcbiAgICAgKiBDcmVhdGVzIGEgbmV3IGBUZXh0RG9jdW1lbnRFZGl0YFxuICAgICAqL1xuICAgIGZ1bmN0aW9uIGNyZWF0ZSh0ZXh0RG9jdW1lbnQsIGVkaXRzKSB7XG4gICAgICAgIHJldHVybiB7IHRleHREb2N1bWVudCwgZWRpdHMgfTtcbiAgICB9XG4gICAgVGV4dERvY3VtZW50RWRpdC5jcmVhdGUgPSBjcmVhdGU7XG4gICAgZnVuY3Rpb24gaXModmFsdWUpIHtcbiAgICAgICAgY29uc3QgY2FuZGlkYXRlID0gdmFsdWU7XG4gICAgICAgIHJldHVybiBJcy5kZWZpbmVkKGNhbmRpZGF0ZSlcbiAgICAgICAgICAgICYmIE9wdGlvbmFsVmVyc2lvbmVkVGV4dERvY3VtZW50SWRlbnRpZmllci5pcyhjYW5kaWRhdGUudGV4dERvY3VtZW50KVxuICAgICAgICAgICAgJiYgQXJyYXkuaXNBcnJheShjYW5kaWRhdGUuZWRpdHMpO1xuICAgIH1cbiAgICBUZXh0RG9jdW1lbnRFZGl0LmlzID0gaXM7XG59KShUZXh0RG9jdW1lbnRFZGl0IHx8IChUZXh0RG9jdW1lbnRFZGl0ID0ge30pKTtcbmV4cG9ydCB2YXIgQ3JlYXRlRmlsZTtcbihmdW5jdGlvbiAoQ3JlYXRlRmlsZSkge1xuICAgIGZ1bmN0aW9uIGNyZWF0ZSh1cmksIG9wdGlvbnMsIGFubm90YXRpb24pIHtcbiAgICAgICAgY29uc3QgcmVzdWx0ID0ge1xuICAgICAgICAgICAga2luZDogJ2NyZWF0ZScsXG4gICAgICAgICAgICB1cmlcbiAgICAgICAgfTtcbiAgICAgICAgaWYgKG9wdGlvbnMgIT09IHVuZGVmaW5lZCAmJiAob3B0aW9ucy5vdmVyd3JpdGUgIT09IHVuZGVmaW5lZCB8fCBvcHRpb25zLmlnbm9yZUlmRXhpc3RzICE9PSB1bmRlZmluZWQpKSB7XG4gICAgICAgICAgICByZXN1bHQub3B0aW9ucyA9IG9wdGlvbnM7XG4gICAgICAgIH1cbiAgICAgICAgaWYgKGFubm90YXRpb24gIT09IHVuZGVmaW5lZCkge1xuICAgICAgICAgICAgcmVzdWx0LmFubm90YXRpb25JZCA9IGFubm90YXRpb247XG4gICAgICAgIH1cbiAgICAgICAgcmV0dXJuIHJlc3VsdDtcbiAgICB9XG4gICAgQ3JlYXRlRmlsZS5jcmVhdGUgPSBjcmVhdGU7XG4gICAgZnVuY3Rpb24gaXModmFsdWUpIHtcbiAgICAgICAgY29uc3QgY2FuZGlkYXRlID0gdmFsdWU7XG4gICAgICAgIHJldHVybiBjYW5kaWRhdGUgJiYgY2FuZGlkYXRlLmtpbmQgPT09ICdjcmVhdGUnICYmIElzLnN0cmluZyhjYW5kaWRhdGUudXJpKSAmJiAoY2FuZGlkYXRlLm9wdGlvbnMgPT09IHVuZGVmaW5lZCB8fFxuICAgICAgICAgICAgKChjYW5kaWRhdGUub3B0aW9ucy5vdmVyd3JpdGUgPT09IHVuZGVmaW5lZCB8fCBJcy5ib29sZWFuKGNhbmRpZGF0ZS5vcHRpb25zLm92ZXJ3cml0ZSkpICYmIChjYW5kaWRhdGUub3B0aW9ucy5pZ25vcmVJZkV4aXN0cyA9PT0gdW5kZWZpbmVkIHx8IElzLmJvb2xlYW4oY2FuZGlkYXRlLm9wdGlvbnMuaWdub3JlSWZFeGlzdHMpKSkpICYmIChjYW5kaWRhdGUuYW5ub3RhdGlvbklkID09PSB1bmRlZmluZWQgfHwgQ2hhbmdlQW5ub3RhdGlvbklkZW50aWZpZXIuaXMoY2FuZGlkYXRlLmFubm90YXRpb25JZCkpO1xuICAgIH1cbiAgICBDcmVhdGVGaWxlLmlzID0gaXM7XG59KShDcmVhdGVGaWxlIHx8IChDcmVhdGVGaWxlID0ge30pKTtcbmV4cG9ydCB2YXIgUmVuYW1lRmlsZTtcbihmdW5jdGlvbiAoUmVuYW1lRmlsZSkge1xuICAgIGZ1bmN0aW9uIGNyZWF0ZShvbGRVcmksIG5ld1VyaSwgb3B0aW9ucywgYW5ub3RhdGlvbikge1xuICAgICAgICBjb25zdCByZXN1bHQgPSB7XG4gICAgICAgICAgICBraW5kOiAncmVuYW1lJyxcbiAgICAgICAgICAgIG9sZFVyaSxcbiAgICAgICAgICAgIG5ld1VyaVxuICAgICAgICB9O1xuICAgICAgICBpZiAob3B0aW9ucyAhPT0gdW5kZWZpbmVkICYmIChvcHRpb25zLm92ZXJ3cml0ZSAhPT0gdW5kZWZpbmVkIHx8IG9wdGlvbnMuaWdub3JlSWZFeGlzdHMgIT09IHVuZGVmaW5lZCkpIHtcbiAgICAgICAgICAgIHJlc3VsdC5vcHRpb25zID0gb3B0aW9ucztcbiAgICAgICAgfVxuICAgICAgICBpZiAoYW5ub3RhdGlvbiAhPT0gdW5kZWZpbmVkKSB7XG4gICAgICAgICAgICByZXN1bHQuYW5ub3RhdGlvbklkID0gYW5ub3RhdGlvbjtcbiAgICAgICAgfVxuICAgICAgICByZXR1cm4gcmVzdWx0O1xuICAgIH1cbiAgICBSZW5hbWVGaWxlLmNyZWF0ZSA9IGNyZWF0ZTtcbiAgICBmdW5jdGlvbiBpcyh2YWx1ZSkge1xuICAgICAgICBjb25zdCBjYW5kaWRhdGUgPSB2YWx1ZTtcbiAgICAgICAgcmV0dXJuIGNhbmRpZGF0ZSAmJiBjYW5kaWRhdGUua2luZCA9PT0gJ3JlbmFtZScgJiYgSXMuc3RyaW5nKGNhbmRpZGF0ZS5vbGRVcmkpICYmIElzLnN0cmluZyhjYW5kaWRhdGUubmV3VXJpKSAmJiAoY2FuZGlkYXRlLm9wdGlvbnMgPT09IHVuZGVmaW5lZCB8fFxuICAgICAgICAgICAgKChjYW5kaWRhdGUub3B0aW9ucy5vdmVyd3JpdGUgPT09IHVuZGVmaW5lZCB8fCBJcy5ib29sZWFuKGNhbmRpZGF0ZS5vcHRpb25zLm92ZXJ3cml0ZSkpICYmIChjYW5kaWRhdGUub3B0aW9ucy5pZ25vcmVJZkV4aXN0cyA9PT0gdW5kZWZpbmVkIHx8IElzLmJvb2xlYW4oY2FuZGlkYXRlLm9wdGlvbnMuaWdub3JlSWZFeGlzdHMpKSkpICYmIChjYW5kaWRhdGUuYW5ub3RhdGlvbklkID09PSB1bmRlZmluZWQgfHwgQ2hhbmdlQW5ub3RhdGlvbklkZW50aWZpZXIuaXMoY2FuZGlkYXRlLmFubm90YXRpb25JZCkpO1xuICAgIH1cbiAgICBSZW5hbWVGaWxlLmlzID0gaXM7XG59KShSZW5hbWVGaWxlIHx8IChSZW5hbWVGaWxlID0ge30pKTtcbmV4cG9ydCB2YXIgRGVsZXRlRmlsZTtcbihmdW5jdGlvbiAoRGVsZXRlRmlsZSkge1xuICAgIGZ1bmN0aW9uIGNyZWF0ZSh1cmksIG9wdGlvbnMsIGFubm90YXRpb24pIHtcbiAgICAgICAgY29uc3QgcmVzdWx0ID0ge1xuICAgICAgICAgICAga2luZDogJ2RlbGV0ZScsXG4gICAgICAgICAgICB1cmlcbiAgICAgICAgfTtcbiAgICAgICAgaWYgKG9wdGlvbnMgIT09IHVuZGVmaW5lZCAmJiAob3B0aW9ucy5yZWN1cnNpdmUgIT09IHVuZGVmaW5lZCB8fCBvcHRpb25zLmlnbm9yZUlmTm90RXhpc3RzICE9PSB1bmRlZmluZWQpKSB7XG4gICAgICAgICAgICByZXN1bHQub3B0aW9ucyA9IG9wdGlvbnM7XG4gICAgICAgIH1cbiAgICAgICAgaWYgKGFubm90YXRpb24gIT09IHVuZGVmaW5lZCkge1xuICAgICAgICAgICAgcmVzdWx0LmFubm90YXRpb25JZCA9IGFubm90YXRpb247XG4gICAgICAgIH1cbiAgICAgICAgcmV0dXJuIHJlc3VsdDtcbiAgICB9XG4gICAgRGVsZXRlRmlsZS5jcmVhdGUgPSBjcmVhdGU7XG4gICAgZnVuY3Rpb24gaXModmFsdWUpIHtcbiAgICAgICAgY29uc3QgY2FuZGlkYXRlID0gdmFsdWU7XG4gICAgICAgIHJldHVybiBjYW5kaWRhdGUgJiYgY2FuZGlkYXRlLmtpbmQgPT09ICdkZWxldGUnICYmIElzLnN0cmluZyhjYW5kaWRhdGUudXJpKSAmJiAoY2FuZGlkYXRlLm9wdGlvbnMgPT09IHVuZGVmaW5lZCB8fFxuICAgICAgICAgICAgKChjYW5kaWRhdGUub3B0aW9ucy5yZWN1cnNpdmUgPT09IHVuZGVmaW5lZCB8fCBJcy5ib29sZWFuKGNhbmRpZGF0ZS5vcHRpb25zLnJlY3Vyc2l2ZSkpICYmIChjYW5kaWRhdGUub3B0aW9ucy5pZ25vcmVJZk5vdEV4aXN0cyA9PT0gdW5kZWZpbmVkIHx8IElzLmJvb2xlYW4oY2FuZGlkYXRlLm9wdGlvbnMuaWdub3JlSWZOb3RFeGlzdHMpKSkpICYmIChjYW5kaWRhdGUuYW5ub3RhdGlvbklkID09PSB1bmRlZmluZWQgfHwgQ2hhbmdlQW5ub3RhdGlvbklkZW50aWZpZXIuaXMoY2FuZGlkYXRlLmFubm90YXRpb25JZCkpO1xuICAgIH1cbiAgICBEZWxldGVGaWxlLmlzID0gaXM7XG59KShEZWxldGVGaWxlIHx8IChEZWxldGVGaWxlID0ge30pKTtcbmV4cG9ydCB2YXIgV29ya3NwYWNlRWRpdDtcbihmdW5jdGlvbiAoV29ya3NwYWNlRWRpdCkge1xuICAgIGZ1bmN0aW9uIGlzKHZhbHVlKSB7XG4gICAgICAgIGNvbnN0IGNhbmRpZGF0ZSA9IHZhbHVlO1xuICAgICAgICByZXR1cm4gY2FuZGlkYXRlICYmXG4gICAgICAgICAgICAoY2FuZGlkYXRlLmNoYW5nZXMgIT09IHVuZGVmaW5lZCB8fCBjYW5kaWRhdGUuZG9jdW1lbnRDaGFuZ2VzICE9PSB1bmRlZmluZWQpICYmXG4gICAgICAgICAgICAoY2FuZGlkYXRlLmRvY3VtZW50Q2hhbmdlcyA9PT0gdW5kZWZpbmVkIHx8IGNhbmRpZGF0ZS5kb2N1bWVudENoYW5nZXMuZXZlcnkoKGNoYW5nZSkgPT4ge1xuICAgICAgICAgICAgICAgIGlmIChJcy5zdHJpbmcoY2hhbmdlLmtpbmQpKSB7XG4gICAgICAgICAgICAgICAgICAgIHJldHVybiBDcmVhdGVGaWxlLmlzKGNoYW5nZSkgfHwgUmVuYW1lRmlsZS5pcyhjaGFuZ2UpIHx8IERlbGV0ZUZpbGUuaXMoY2hhbmdlKTtcbiAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgZWxzZSB7XG4gICAgICAgICAgICAgICAgICAgIHJldHVybiBUZXh0RG9jdW1lbnRFZGl0LmlzKGNoYW5nZSk7XG4gICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgfSkpO1xuICAgIH1cbiAgICBXb3Jrc3BhY2VFZGl0LmlzID0gaXM7XG59KShXb3Jrc3BhY2VFZGl0IHx8IChXb3Jrc3BhY2VFZGl0ID0ge30pKTtcbmNsYXNzIFRleHRFZGl0Q2hhbmdlSW1wbCB7XG4gICAgY29uc3RydWN0b3IoZWRpdHMsIGNoYW5nZUFubm90YXRpb25zKSB7XG4gICAgICAgIHRoaXMuZWRpdHMgPSBlZGl0cztcbiAgICAgICAgdGhpcy5jaGFuZ2VBbm5vdGF0aW9ucyA9IGNoYW5nZUFubm90YXRpb25zO1xuICAgIH1cbiAgICBpbnNlcnQocG9zaXRpb24sIG5ld1RleHQsIGFubm90YXRpb24pIHtcbiAgICAgICAgbGV0IGVkaXQ7XG4gICAgICAgIGxldCBpZDtcbiAgICAgICAgaWYgKGFubm90YXRpb24gPT09IHVuZGVmaW5lZCkge1xuICAgICAgICAgICAgZWRpdCA9IFRleHRFZGl0Lmluc2VydChwb3NpdGlvbiwgbmV3VGV4dCk7XG4gICAgICAgIH1cbiAgICAgICAgZWxzZSBpZiAoQ2hhbmdlQW5ub3RhdGlvbklkZW50aWZpZXIuaXMoYW5ub3RhdGlvbikpIHtcbiAgICAgICAgICAgIGlkID0gYW5ub3RhdGlvbjtcbiAgICAgICAgICAgIGVkaXQgPSBBbm5vdGF0ZWRUZXh0RWRpdC5pbnNlcnQocG9zaXRpb24sIG5ld1RleHQsIGFubm90YXRpb24pO1xuICAgICAgICB9XG4gICAgICAgIGVsc2Uge1xuICAgICAgICAgICAgdGhpcy5hc3NlcnRDaGFuZ2VBbm5vdGF0aW9ucyh0aGlzLmNoYW5nZUFubm90YXRpb25zKTtcbiAgICAgICAgICAgIGlkID0gdGhpcy5jaGFuZ2VBbm5vdGF0aW9ucy5tYW5hZ2UoYW5ub3RhdGlvbik7XG4gICAgICAgICAgICBlZGl0ID0gQW5ub3RhdGVkVGV4dEVkaXQuaW5zZXJ0KHBvc2l0aW9uLCBuZXdUZXh0LCBpZCk7XG4gICAgICAgIH1cbiAgICAgICAgdGhpcy5lZGl0cy5wdXNoKGVkaXQpO1xuICAgICAgICBpZiAoaWQgIT09IHVuZGVmaW5lZCkge1xuICAgICAgICAgICAgcmV0dXJuIGlkO1xuICAgICAgICB9XG4gICAgfVxuICAgIHJlcGxhY2UocmFuZ2UsIG5ld1RleHQsIGFubm90YXRpb24pIHtcbiAgICAgICAgbGV0IGVkaXQ7XG4gICAgICAgIGxldCBpZDtcbiAgICAgICAgaWYgKGFubm90YXRpb24gPT09IHVuZGVmaW5lZCkge1xuICAgICAgICAgICAgZWRpdCA9IFRleHRFZGl0LnJlcGxhY2UocmFuZ2UsIG5ld1RleHQpO1xuICAgICAgICB9XG4gICAgICAgIGVsc2UgaWYgKENoYW5nZUFubm90YXRpb25JZGVudGlmaWVyLmlzKGFubm90YXRpb24pKSB7XG4gICAgICAgICAgICBpZCA9IGFubm90YXRpb247XG4gICAgICAgICAgICBlZGl0ID0gQW5ub3RhdGVkVGV4dEVkaXQucmVwbGFjZShyYW5nZSwgbmV3VGV4dCwgYW5ub3RhdGlvbik7XG4gICAgICAgIH1cbiAgICAgICAgZWxzZSB7XG4gICAgICAgICAgICB0aGlzLmFzc2VydENoYW5nZUFubm90YXRpb25zKHRoaXMuY2hhbmdlQW5ub3RhdGlvbnMpO1xuICAgICAgICAgICAgaWQgPSB0aGlzLmNoYW5nZUFubm90YXRpb25zLm1hbmFnZShhbm5vdGF0aW9uKTtcbiAgICAgICAgICAgIGVkaXQgPSBBbm5vdGF0ZWRUZXh0RWRpdC5yZXBsYWNlKHJhbmdlLCBuZXdUZXh0LCBpZCk7XG4gICAgICAgIH1cbiAgICAgICAgdGhpcy5lZGl0cy5wdXNoKGVkaXQpO1xuICAgICAgICBpZiAoaWQgIT09IHVuZGVmaW5lZCkge1xuICAgICAgICAgICAgcmV0dXJuIGlkO1xuICAgICAgICB9XG4gICAgfVxuICAgIGRlbGV0ZShyYW5nZSwgYW5ub3RhdGlvbikge1xuICAgICAgICBsZXQgZWRpdDtcbiAgICAgICAgbGV0IGlkO1xuICAgICAgICBpZiAoYW5ub3RhdGlvbiA9PT0gdW5kZWZpbmVkKSB7XG4gICAgICAgICAgICBlZGl0ID0gVGV4dEVkaXQuZGVsKHJhbmdlKTtcbiAgICAgICAgfVxuICAgICAgICBlbHNlIGlmIChDaGFuZ2VBbm5vdGF0aW9uSWRlbnRpZmllci5pcyhhbm5vdGF0aW9uKSkge1xuICAgICAgICAgICAgaWQgPSBhbm5vdGF0aW9uO1xuICAgICAgICAgICAgZWRpdCA9IEFubm90YXRlZFRleHRFZGl0LmRlbChyYW5nZSwgYW5ub3RhdGlvbik7XG4gICAgICAgIH1cbiAgICAgICAgZWxzZSB7XG4gICAgICAgICAgICB0aGlzLmFzc2VydENoYW5nZUFubm90YXRpb25zKHRoaXMuY2hhbmdlQW5ub3RhdGlvbnMpO1xuICAgICAgICAgICAgaWQgPSB0aGlzLmNoYW5nZUFubm90YXRpb25zLm1hbmFnZShhbm5vdGF0aW9uKTtcbiAgICAgICAgICAgIGVkaXQgPSBBbm5vdGF0ZWRUZXh0RWRpdC5kZWwocmFuZ2UsIGlkKTtcbiAgICAgICAgfVxuICAgICAgICB0aGlzLmVkaXRzLnB1c2goZWRpdCk7XG4gICAgICAgIGlmIChpZCAhPT0gdW5kZWZpbmVkKSB7XG4gICAgICAgICAgICByZXR1cm4gaWQ7XG4gICAgICAgIH1cbiAgICB9XG4gICAgYWRkKGVkaXQpIHtcbiAgICAgICAgdGhpcy5lZGl0cy5wdXNoKGVkaXQpO1xuICAgIH1cbiAgICBhbGwoKSB7XG4gICAgICAgIHJldHVybiB0aGlzLmVkaXRzO1xuICAgIH1cbiAgICBjbGVhcigpIHtcbiAgICAgICAgdGhpcy5lZGl0cy5zcGxpY2UoMCwgdGhpcy5lZGl0cy5sZW5ndGgpO1xuICAgIH1cbiAgICBhc3NlcnRDaGFuZ2VBbm5vdGF0aW9ucyh2YWx1ZSkge1xuICAgICAgICBpZiAodmFsdWUgPT09IHVuZGVmaW5lZCkge1xuICAgICAgICAgICAgdGhyb3cgbmV3IEVycm9yKGBUZXh0IGVkaXQgY2hhbmdlIGlzIG5vdCBjb25maWd1cmVkIHRvIG1hbmFnZSBjaGFuZ2UgYW5ub3RhdGlvbnMuYCk7XG4gICAgICAgIH1cbiAgICB9XG59XG5leHBvcnQgdmFyIFNuaXBwZXRUZXh0RWRpdDtcbihmdW5jdGlvbiAoU25pcHBldFRleHRFZGl0KSB7XG4gICAgZnVuY3Rpb24gaXModmFsdWUpIHtcbiAgICAgICAgY29uc3QgY2FuZGlkYXRlID0gdmFsdWU7XG4gICAgICAgIHJldHVybiBJcy5vYmplY3RMaXRlcmFsKGNhbmRpZGF0ZSlcbiAgICAgICAgICAgICYmIFJhbmdlLmlzKGNhbmRpZGF0ZS5yYW5nZSlcbiAgICAgICAgICAgICYmIFN0cmluZ1ZhbHVlLmlzU25pcHBldChjYW5kaWRhdGUuc25pcHBldClcbiAgICAgICAgICAgICYmIChjYW5kaWRhdGUuYW5ub3RhdGlvbklkID09PSB1bmRlZmluZWQgfHxcbiAgICAgICAgICAgICAgICAoQ2hhbmdlQW5ub3RhdGlvbi5pcyhjYW5kaWRhdGUuYW5ub3RhdGlvbklkKSB8fCBDaGFuZ2VBbm5vdGF0aW9uSWRlbnRpZmllci5pcyhjYW5kaWRhdGUuYW5ub3RhdGlvbklkKSkpO1xuICAgIH1cbiAgICBTbmlwcGV0VGV4dEVkaXQuaXMgPSBpcztcbn0pKFNuaXBwZXRUZXh0RWRpdCB8fCAoU25pcHBldFRleHRFZGl0ID0ge30pKTtcbi8qKlxuICogQSBoZWxwZXIgY2xhc3NcbiAqL1xuY2xhc3MgQ2hhbmdlQW5ub3RhdGlvbnMge1xuICAgIGNvbnN0cnVjdG9yKGFubm90YXRpb25zKSB7XG4gICAgICAgIHRoaXMuX2Fubm90YXRpb25zID0gYW5ub3RhdGlvbnMgPT09IHVuZGVmaW5lZCA/IE9iamVjdC5jcmVhdGUobnVsbCkgOiBhbm5vdGF0aW9ucztcbiAgICAgICAgdGhpcy5fY291bnRlciA9IDA7XG4gICAgICAgIHRoaXMuX3NpemUgPSAwO1xuICAgIH1cbiAgICBhbGwoKSB7XG4gICAgICAgIHJldHVybiB0aGlzLl9hbm5vdGF0aW9ucztcbiAgICB9XG4gICAgZ2V0IHNpemUoKSB7XG4gICAgICAgIHJldHVybiB0aGlzLl9zaXplO1xuICAgIH1cbiAgICBtYW5hZ2UoaWRPckFubm90YXRpb24sIGFubm90YXRpb24pIHtcbiAgICAgICAgbGV0IGlkO1xuICAgICAgICBpZiAoQ2hhbmdlQW5ub3RhdGlvbklkZW50aWZpZXIuaXMoaWRPckFubm90YXRpb24pKSB7XG4gICAgICAgICAgICBpZCA9IGlkT3JBbm5vdGF0aW9uO1xuICAgICAgICB9XG4gICAgICAgIGVsc2Uge1xuICAgICAgICAgICAgaWQgPSB0aGlzLm5leHRJZCgpO1xuICAgICAgICAgICAgYW5ub3RhdGlvbiA9IGlkT3JBbm5vdGF0aW9uO1xuICAgICAgICB9XG4gICAgICAgIGlmICh0aGlzLl9hbm5vdGF0aW9uc1tpZF0gIT09IHVuZGVmaW5lZCkge1xuICAgICAgICAgICAgdGhyb3cgbmV3IEVycm9yKGBJZCAke2lkfSBpcyBhbHJlYWR5IGluIHVzZS5gKTtcbiAgICAgICAgfVxuICAgICAgICBpZiAoYW5ub3RhdGlvbiA9PT0gdW5kZWZpbmVkKSB7XG4gICAgICAgICAgICB0aHJvdyBuZXcgRXJyb3IoYE5vIGFubm90YXRpb24gcHJvdmlkZWQgZm9yIGlkICR7aWR9YCk7XG4gICAgICAgIH1cbiAgICAgICAgdGhpcy5fYW5ub3RhdGlvbnNbaWRdID0gYW5ub3RhdGlvbjtcbiAgICAgICAgdGhpcy5fc2l6ZSsrO1xuICAgICAgICByZXR1cm4gaWQ7XG4gICAgfVxuICAgIG5leHRJZCgpIHtcbiAgICAgICAgdGhpcy5fY291bnRlcisrO1xuICAgICAgICByZXR1cm4gdGhpcy5fY291bnRlci50b1N0cmluZygpO1xuICAgIH1cbn1cbi8qKlxuICogQSB3b3Jrc3BhY2UgY2hhbmdlIGhlbHBzIGNvbnN0cnVjdGluZyBjaGFuZ2VzIHRvIGEgd29ya3NwYWNlLlxuICovXG5leHBvcnQgY2xhc3MgV29ya3NwYWNlQ2hhbmdlIHtcbiAgICBjb25zdHJ1Y3Rvcih3b3Jrc3BhY2VFZGl0KSB7XG4gICAgICAgIHRoaXMuX3RleHRFZGl0Q2hhbmdlcyA9IE9iamVjdC5jcmVhdGUobnVsbCk7XG4gICAgICAgIGlmICh3b3Jrc3BhY2VFZGl0ICE9PSB1bmRlZmluZWQpIHtcbiAgICAgICAgICAgIHRoaXMuX3dvcmtzcGFjZUVkaXQgPSB3b3Jrc3BhY2VFZGl0O1xuICAgICAgICAgICAgaWYgKHdvcmtzcGFjZUVkaXQuZG9jdW1lbnRDaGFuZ2VzKSB7XG4gICAgICAgICAgICAgICAgdGhpcy5fY2hhbmdlQW5ub3RhdGlvbnMgPSBuZXcgQ2hhbmdlQW5ub3RhdGlvbnMod29ya3NwYWNlRWRpdC5jaGFuZ2VBbm5vdGF0aW9ucyk7XG4gICAgICAgICAgICAgICAgd29ya3NwYWNlRWRpdC5jaGFuZ2VBbm5vdGF0aW9ucyA9IHRoaXMuX2NoYW5nZUFubm90YXRpb25zLmFsbCgpO1xuICAgICAgICAgICAgICAgIHdvcmtzcGFjZUVkaXQuZG9jdW1lbnRDaGFuZ2VzLmZvckVhY2goKGNoYW5nZSkgPT4ge1xuICAgICAgICAgICAgICAgICAgICBpZiAoVGV4dERvY3VtZW50RWRpdC5pcyhjaGFuZ2UpKSB7XG4gICAgICAgICAgICAgICAgICAgICAgICBjb25zdCB0ZXh0RWRpdENoYW5nZSA9IG5ldyBUZXh0RWRpdENoYW5nZUltcGwoY2hhbmdlLmVkaXRzLCB0aGlzLl9jaGFuZ2VBbm5vdGF0aW9ucyk7XG4gICAgICAgICAgICAgICAgICAgICAgICB0aGlzLl90ZXh0RWRpdENoYW5nZXNbY2hhbmdlLnRleHREb2N1bWVudC51cmldID0gdGV4dEVkaXRDaGFuZ2U7XG4gICAgICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICB9KTtcbiAgICAgICAgICAgIH1cbiAgICAgICAgICAgIGVsc2UgaWYgKHdvcmtzcGFjZUVkaXQuY2hhbmdlcykge1xuICAgICAgICAgICAgICAgIE9iamVjdC5rZXlzKHdvcmtzcGFjZUVkaXQuY2hhbmdlcykuZm9yRWFjaCgoa2V5KSA9PiB7XG4gICAgICAgICAgICAgICAgICAgIGNvbnN0IHRleHRFZGl0Q2hhbmdlID0gbmV3IFRleHRFZGl0Q2hhbmdlSW1wbCh3b3Jrc3BhY2VFZGl0LmNoYW5nZXNba2V5XSk7XG4gICAgICAgICAgICAgICAgICAgIHRoaXMuX3RleHRFZGl0Q2hhbmdlc1trZXldID0gdGV4dEVkaXRDaGFuZ2U7XG4gICAgICAgICAgICAgICAgfSk7XG4gICAgICAgICAgICB9XG4gICAgICAgIH1cbiAgICAgICAgZWxzZSB7XG4gICAgICAgICAgICB0aGlzLl93b3Jrc3BhY2VFZGl0ID0ge307XG4gICAgICAgIH1cbiAgICB9XG4gICAgLyoqXG4gICAgICogUmV0dXJucyB0aGUgdW5kZXJseWluZyB7QGxpbmsgV29ya3NwYWNlRWRpdH0gbGl0ZXJhbFxuICAgICAqIHVzZSB0byBiZSByZXR1cm5lZCBmcm9tIGEgd29ya3NwYWNlIGVkaXQgb3BlcmF0aW9uIGxpa2UgcmVuYW1lLlxuICAgICAqL1xuICAgIGdldCBlZGl0KCkge1xuICAgICAgICB0aGlzLmluaXREb2N1bWVudENoYW5nZXMoKTtcbiAgICAgICAgaWYgKHRoaXMuX2NoYW5nZUFubm90YXRpb25zICE9PSB1bmRlZmluZWQpIHtcbiAgICAgICAgICAgIGlmICh0aGlzLl9jaGFuZ2VBbm5vdGF0aW9ucy5zaXplID09PSAwKSB7XG4gICAgICAgICAgICAgICAgdGhpcy5fd29ya3NwYWNlRWRpdC5jaGFuZ2VBbm5vdGF0aW9ucyA9IHVuZGVmaW5lZDtcbiAgICAgICAgICAgIH1cbiAgICAgICAgICAgIGVsc2Uge1xuICAgICAgICAgICAgICAgIHRoaXMuX3dvcmtzcGFjZUVkaXQuY2hhbmdlQW5ub3RhdGlvbnMgPSB0aGlzLl9jaGFuZ2VBbm5vdGF0aW9ucy5hbGwoKTtcbiAgICAgICAgICAgIH1cbiAgICAgICAgfVxuICAgICAgICByZXR1cm4gdGhpcy5fd29ya3NwYWNlRWRpdDtcbiAgICB9XG4gICAgZ2V0VGV4dEVkaXRDaGFuZ2Uoa2V5KSB7XG4gICAgICAgIGlmIChPcHRpb25hbFZlcnNpb25lZFRleHREb2N1bWVudElkZW50aWZpZXIuaXMoa2V5KSkge1xuICAgICAgICAgICAgdGhpcy5pbml0RG9jdW1lbnRDaGFuZ2VzKCk7XG4gICAgICAgICAgICBpZiAodGhpcy5fd29ya3NwYWNlRWRpdC5kb2N1bWVudENoYW5nZXMgPT09IHVuZGVmaW5lZCkge1xuICAgICAgICAgICAgICAgIHRocm93IG5ldyBFcnJvcignV29ya3NwYWNlIGVkaXQgaXMgbm90IGNvbmZpZ3VyZWQgZm9yIGRvY3VtZW50IGNoYW5nZXMuJyk7XG4gICAgICAgICAgICB9XG4gICAgICAgICAgICBjb25zdCB0ZXh0RG9jdW1lbnQgPSB7IHVyaToga2V5LnVyaSwgdmVyc2lvbjoga2V5LnZlcnNpb24gfTtcbiAgICAgICAgICAgIGxldCByZXN1bHQgPSB0aGlzLl90ZXh0RWRpdENoYW5nZXNbdGV4dERvY3VtZW50LnVyaV07XG4gICAgICAgICAgICBpZiAoIXJlc3VsdCkge1xuICAgICAgICAgICAgICAgIGNvbnN0IGVkaXRzID0gW107XG4gICAgICAgICAgICAgICAgY29uc3QgdGV4dERvY3VtZW50RWRpdCA9IHtcbiAgICAgICAgICAgICAgICAgICAgdGV4dERvY3VtZW50LFxuICAgICAgICAgICAgICAgICAgICBlZGl0c1xuICAgICAgICAgICAgICAgIH07XG4gICAgICAgICAgICAgICAgdGhpcy5fd29ya3NwYWNlRWRpdC5kb2N1bWVudENoYW5nZXMucHVzaCh0ZXh0RG9jdW1lbnRFZGl0KTtcbiAgICAgICAgICAgICAgICByZXN1bHQgPSBuZXcgVGV4dEVkaXRDaGFuZ2VJbXBsKGVkaXRzLCB0aGlzLl9jaGFuZ2VBbm5vdGF0aW9ucyk7XG4gICAgICAgICAgICAgICAgdGhpcy5fdGV4dEVkaXRDaGFuZ2VzW3RleHREb2N1bWVudC51cmldID0gcmVzdWx0O1xuICAgICAgICAgICAgfVxuICAgICAgICAgICAgcmV0dXJuIHJlc3VsdDtcbiAgICAgICAgfVxuICAgICAgICBlbHNlIHtcbiAgICAgICAgICAgIHRoaXMuaW5pdENoYW5nZXMoKTtcbiAgICAgICAgICAgIGlmICh0aGlzLl93b3Jrc3BhY2VFZGl0LmNoYW5nZXMgPT09IHVuZGVmaW5lZCkge1xuICAgICAgICAgICAgICAgIHRocm93IG5ldyBFcnJvcignV29ya3NwYWNlIGVkaXQgaXMgbm90IGNvbmZpZ3VyZWQgZm9yIG5vcm1hbCB0ZXh0IGVkaXQgY2hhbmdlcy4nKTtcbiAgICAgICAgICAgIH1cbiAgICAgICAgICAgIGxldCByZXN1bHQgPSB0aGlzLl90ZXh0RWRpdENoYW5nZXNba2V5XTtcbiAgICAgICAgICAgIGlmICghcmVzdWx0KSB7XG4gICAgICAgICAgICAgICAgY29uc3QgZWRpdHMgPSBbXTtcbiAgICAgICAgICAgICAgICB0aGlzLl93b3Jrc3BhY2VFZGl0LmNoYW5nZXNba2V5XSA9IGVkaXRzO1xuICAgICAgICAgICAgICAgIHJlc3VsdCA9IG5ldyBUZXh0RWRpdENoYW5nZUltcGwoZWRpdHMpO1xuICAgICAgICAgICAgICAgIHRoaXMuX3RleHRFZGl0Q2hhbmdlc1trZXldID0gcmVzdWx0O1xuICAgICAgICAgICAgfVxuICAgICAgICAgICAgcmV0dXJuIHJlc3VsdDtcbiAgICAgICAgfVxuICAgIH1cbiAgICBpbml0RG9jdW1lbnRDaGFuZ2VzKCkge1xuICAgICAgICBpZiAodGhpcy5fd29ya3NwYWNlRWRpdC5kb2N1bWVudENoYW5nZXMgPT09IHVuZGVmaW5lZCAmJiB0aGlzLl93b3Jrc3BhY2VFZGl0LmNoYW5nZXMgPT09IHVuZGVmaW5lZCkge1xuICAgICAgICAgICAgdGhpcy5fY2hhbmdlQW5ub3RhdGlvbnMgPSBuZXcgQ2hhbmdlQW5ub3RhdGlvbnMoKTtcbiAgICAgICAgICAgIHRoaXMuX3dvcmtzcGFjZUVkaXQuZG9jdW1lbnRDaGFuZ2VzID0gW107XG4gICAgICAgICAgICB0aGlzLl93b3Jrc3BhY2VFZGl0LmNoYW5nZUFubm90YXRpb25zID0gdGhpcy5fY2hhbmdlQW5ub3RhdGlvbnMuYWxsKCk7XG4gICAgICAgIH1cbiAgICB9XG4gICAgaW5pdENoYW5nZXMoKSB7XG4gICAgICAgIGlmICh0aGlzLl93b3Jrc3BhY2VFZGl0LmRvY3VtZW50Q2hhbmdlcyA9PT0gdW5kZWZpbmVkICYmIHRoaXMuX3dvcmtzcGFjZUVkaXQuY2hhbmdlcyA9PT0gdW5kZWZpbmVkKSB7XG4gICAgICAgICAgICB0aGlzLl93b3Jrc3BhY2VFZGl0LmNoYW5nZXMgPSBPYmplY3QuY3JlYXRlKG51bGwpO1xuICAgICAgICB9XG4gICAgfVxuICAgIGNyZWF0ZUZpbGUodXJpLCBvcHRpb25zT3JBbm5vdGF0aW9uLCBvcHRpb25zKSB7XG4gICAgICAgIHRoaXMuaW5pdERvY3VtZW50Q2hhbmdlcygpO1xuICAgICAgICBpZiAodGhpcy5fd29ya3NwYWNlRWRpdC5kb2N1bWVudENoYW5nZXMgPT09IHVuZGVmaW5lZCkge1xuICAgICAgICAgICAgdGhyb3cgbmV3IEVycm9yKCdXb3Jrc3BhY2UgZWRpdCBpcyBub3QgY29uZmlndXJlZCBmb3IgZG9jdW1lbnQgY2hhbmdlcy4nKTtcbiAgICAgICAgfVxuICAgICAgICBsZXQgYW5ub3RhdGlvbjtcbiAgICAgICAgaWYgKENoYW5nZUFubm90YXRpb24uaXMob3B0aW9uc09yQW5ub3RhdGlvbikgfHwgQ2hhbmdlQW5ub3RhdGlvbklkZW50aWZpZXIuaXMob3B0aW9uc09yQW5ub3RhdGlvbikpIHtcbiAgICAgICAgICAgIGFubm90YXRpb24gPSBvcHRpb25zT3JBbm5vdGF0aW9uO1xuICAgICAgICB9XG4gICAgICAgIGVsc2Uge1xuICAgICAgICAgICAgb3B0aW9ucyA9IG9wdGlvbnNPckFubm90YXRpb247XG4gICAgICAgIH1cbiAgICAgICAgbGV0IG9wZXJhdGlvbjtcbiAgICAgICAgbGV0IGlkO1xuICAgICAgICBpZiAoYW5ub3RhdGlvbiA9PT0gdW5kZWZpbmVkKSB7XG4gICAgICAgICAgICBvcGVyYXRpb24gPSBDcmVhdGVGaWxlLmNyZWF0ZSh1cmksIG9wdGlvbnMpO1xuICAgICAgICB9XG4gICAgICAgIGVsc2Uge1xuICAgICAgICAgICAgaWQgPSBDaGFuZ2VBbm5vdGF0aW9uSWRlbnRpZmllci5pcyhhbm5vdGF0aW9uKSA/IGFubm90YXRpb24gOiB0aGlzLl9jaGFuZ2VBbm5vdGF0aW9ucy5tYW5hZ2UoYW5ub3RhdGlvbik7XG4gICAgICAgICAgICBvcGVyYXRpb24gPSBDcmVhdGVGaWxlLmNyZWF0ZSh1cmksIG9wdGlvbnMsIGlkKTtcbiAgICAgICAgfVxuICAgICAgICB0aGlzLl93b3Jrc3BhY2VFZGl0LmRvY3VtZW50Q2hhbmdlcy5wdXNoKG9wZXJhdGlvbik7XG4gICAgICAgIGlmIChpZCAhPT0gdW5kZWZpbmVkKSB7XG4gICAgICAgICAgICByZXR1cm4gaWQ7XG4gICAgICAgIH1cbiAgICB9XG4gICAgcmVuYW1lRmlsZShvbGRVcmksIG5ld1VyaSwgb3B0aW9uc09yQW5ub3RhdGlvbiwgb3B0aW9ucykge1xuICAgICAgICB0aGlzLmluaXREb2N1bWVudENoYW5nZXMoKTtcbiAgICAgICAgaWYgKHRoaXMuX3dvcmtzcGFjZUVkaXQuZG9jdW1lbnRDaGFuZ2VzID09PSB1bmRlZmluZWQpIHtcbiAgICAgICAgICAgIHRocm93IG5ldyBFcnJvcignV29ya3NwYWNlIGVkaXQgaXMgbm90IGNvbmZpZ3VyZWQgZm9yIGRvY3VtZW50IGNoYW5nZXMuJyk7XG4gICAgICAgIH1cbiAgICAgICAgbGV0IGFubm90YXRpb247XG4gICAgICAgIGlmIChDaGFuZ2VBbm5vdGF0aW9uLmlzKG9wdGlvbnNPckFubm90YXRpb24pIHx8IENoYW5nZUFubm90YXRpb25JZGVudGlmaWVyLmlzKG9wdGlvbnNPckFubm90YXRpb24pKSB7XG4gICAgICAgICAgICBhbm5vdGF0aW9uID0gb3B0aW9uc09yQW5ub3RhdGlvbjtcbiAgICAgICAgfVxuICAgICAgICBlbHNlIHtcbiAgICAgICAgICAgIG9wdGlvbnMgPSBvcHRpb25zT3JBbm5vdGF0aW9uO1xuICAgICAgICB9XG4gICAgICAgIGxldCBvcGVyYXRpb247XG4gICAgICAgIGxldCBpZDtcbiAgICAgICAgaWYgKGFubm90YXRpb24gPT09IHVuZGVmaW5lZCkge1xuICAgICAgICAgICAgb3BlcmF0aW9uID0gUmVuYW1lRmlsZS5jcmVhdGUob2xkVXJpLCBuZXdVcmksIG9wdGlvbnMpO1xuICAgICAgICB9XG4gICAgICAgIGVsc2Uge1xuICAgICAgICAgICAgaWQgPSBDaGFuZ2VBbm5vdGF0aW9uSWRlbnRpZmllci5pcyhhbm5vdGF0aW9uKSA/IGFubm90YXRpb24gOiB0aGlzLl9jaGFuZ2VBbm5vdGF0aW9ucy5tYW5hZ2UoYW5ub3RhdGlvbik7XG4gICAgICAgICAgICBvcGVyYXRpb24gPSBSZW5hbWVGaWxlLmNyZWF0ZShvbGRVcmksIG5ld1VyaSwgb3B0aW9ucywgaWQpO1xuICAgICAgICB9XG4gICAgICAgIHRoaXMuX3dvcmtzcGFjZUVkaXQuZG9jdW1lbnRDaGFuZ2VzLnB1c2gob3BlcmF0aW9uKTtcbiAgICAgICAgaWYgKGlkICE9PSB1bmRlZmluZWQpIHtcbiAgICAgICAgICAgIHJldHVybiBpZDtcbiAgICAgICAgfVxuICAgIH1cbiAgICBkZWxldGVGaWxlKHVyaSwgb3B0aW9uc09yQW5ub3RhdGlvbiwgb3B0aW9ucykge1xuICAgICAgICB0aGlzLmluaXREb2N1bWVudENoYW5nZXMoKTtcbiAgICAgICAgaWYgKHRoaXMuX3dvcmtzcGFjZUVkaXQuZG9jdW1lbnRDaGFuZ2VzID09PSB1bmRlZmluZWQpIHtcbiAgICAgICAgICAgIHRocm93IG5ldyBFcnJvcignV29ya3NwYWNlIGVkaXQgaXMgbm90IGNvbmZpZ3VyZWQgZm9yIGRvY3VtZW50IGNoYW5nZXMuJyk7XG4gICAgICAgIH1cbiAgICAgICAgbGV0IGFubm90YXRpb247XG4gICAgICAgIGlmIChDaGFuZ2VBbm5vdGF0aW9uLmlzKG9wdGlvbnNPckFubm90YXRpb24pIHx8IENoYW5nZUFubm90YXRpb25JZGVudGlmaWVyLmlzKG9wdGlvbnNPckFubm90YXRpb24pKSB7XG4gICAgICAgICAgICBhbm5vdGF0aW9uID0gb3B0aW9uc09yQW5ub3RhdGlvbjtcbiAgICAgICAgfVxuICAgICAgICBlbHNlIHtcbiAgICAgICAgICAgIG9wdGlvbnMgPSBvcHRpb25zT3JBbm5vdGF0aW9uO1xuICAgICAgICB9XG4gICAgICAgIGxldCBvcGVyYXRpb247XG4gICAgICAgIGxldCBpZDtcbiAgICAgICAgaWYgKGFubm90YXRpb24gPT09IHVuZGVmaW5lZCkge1xuICAgICAgICAgICAgb3BlcmF0aW9uID0gRGVsZXRlRmlsZS5jcmVhdGUodXJpLCBvcHRpb25zKTtcbiAgICAgICAgfVxuICAgICAgICBlbHNlIHtcbiAgICAgICAgICAgIGlkID0gQ2hhbmdlQW5ub3RhdGlvbklkZW50aWZpZXIuaXMoYW5ub3RhdGlvbikgPyBhbm5vdGF0aW9uIDogdGhpcy5fY2hhbmdlQW5ub3RhdGlvbnMubWFuYWdlKGFubm90YXRpb24pO1xuICAgICAgICAgICAgb3BlcmF0aW9uID0gRGVsZXRlRmlsZS5jcmVhdGUodXJpLCBvcHRpb25zLCBpZCk7XG4gICAgICAgIH1cbiAgICAgICAgdGhpcy5fd29ya3NwYWNlRWRpdC5kb2N1bWVudENoYW5nZXMucHVzaChvcGVyYXRpb24pO1xuICAgICAgICBpZiAoaWQgIT09IHVuZGVmaW5lZCkge1xuICAgICAgICAgICAgcmV0dXJuIGlkO1xuICAgICAgICB9XG4gICAgfVxufVxuLyoqXG4gKiBUaGUgVGV4dERvY3VtZW50SWRlbnRpZmllciBuYW1lc3BhY2UgcHJvdmlkZXMgaGVscGVyIGZ1bmN0aW9ucyB0byB3b3JrIHdpdGhcbiAqIHtAbGluayBUZXh0RG9jdW1lbnRJZGVudGlmaWVyfSBsaXRlcmFscy5cbiAqL1xuZXhwb3J0IHZhciBUZXh0RG9jdW1lbnRJZGVudGlmaWVyO1xuKGZ1bmN0aW9uIChUZXh0RG9jdW1lbnRJZGVudGlmaWVyKSB7XG4gICAgLyoqXG4gICAgICogQ3JlYXRlcyBhIG5ldyBUZXh0RG9jdW1lbnRJZGVudGlmaWVyIGxpdGVyYWwuXG4gICAgICogQHBhcmFtIHVyaSBUaGUgZG9jdW1lbnQncyB1cmkuXG4gICAgICovXG4gICAgZnVuY3Rpb24gY3JlYXRlKHVyaSkge1xuICAgICAgICByZXR1cm4geyB1cmkgfTtcbiAgICB9XG4gICAgVGV4dERvY3VtZW50SWRlbnRpZmllci5jcmVhdGUgPSBjcmVhdGU7XG4gICAgLyoqXG4gICAgICogQ2hlY2tzIHdoZXRoZXIgdGhlIGdpdmVuIGxpdGVyYWwgY29uZm9ybXMgdG8gdGhlIHtAbGluayBUZXh0RG9jdW1lbnRJZGVudGlmaWVyfSBpbnRlcmZhY2UuXG4gICAgICovXG4gICAgZnVuY3Rpb24gaXModmFsdWUpIHtcbiAgICAgICAgY29uc3QgY2FuZGlkYXRlID0gdmFsdWU7XG4gICAgICAgIHJldHVybiBJcy5kZWZpbmVkKGNhbmRpZGF0ZSkgJiYgSXMuc3RyaW5nKGNhbmRpZGF0ZS51cmkpO1xuICAgIH1cbiAgICBUZXh0RG9jdW1lbnRJZGVudGlmaWVyLmlzID0gaXM7XG59KShUZXh0RG9jdW1lbnRJZGVudGlmaWVyIHx8IChUZXh0RG9jdW1lbnRJZGVudGlmaWVyID0ge30pKTtcbi8qKlxuICogVGhlIFZlcnNpb25lZFRleHREb2N1bWVudElkZW50aWZpZXIgbmFtZXNwYWNlIHByb3ZpZGVzIGhlbHBlciBmdW5jdGlvbnMgdG8gd29yayB3aXRoXG4gKiB7QGxpbmsgVmVyc2lvbmVkVGV4dERvY3VtZW50SWRlbnRpZmllcn0gbGl0ZXJhbHMuXG4gKi9cbmV4cG9ydCB2YXIgVmVyc2lvbmVkVGV4dERvY3VtZW50SWRlbnRpZmllcjtcbihmdW5jdGlvbiAoVmVyc2lvbmVkVGV4dERvY3VtZW50SWRlbnRpZmllcikge1xuICAgIC8qKlxuICAgICAqIENyZWF0ZXMgYSBuZXcgVmVyc2lvbmVkVGV4dERvY3VtZW50SWRlbnRpZmllciBsaXRlcmFsLlxuICAgICAqIEBwYXJhbSB1cmkgVGhlIGRvY3VtZW50J3MgdXJpLlxuICAgICAqIEBwYXJhbSB2ZXJzaW9uIFRoZSBkb2N1bWVudCdzIHZlcnNpb24uXG4gICAgICovXG4gICAgZnVuY3Rpb24gY3JlYXRlKHVyaSwgdmVyc2lvbikge1xuICAgICAgICByZXR1cm4geyB1cmksIHZlcnNpb24gfTtcbiAgICB9XG4gICAgVmVyc2lvbmVkVGV4dERvY3VtZW50SWRlbnRpZmllci5jcmVhdGUgPSBjcmVhdGU7XG4gICAgLyoqXG4gICAgICogQ2hlY2tzIHdoZXRoZXIgdGhlIGdpdmVuIGxpdGVyYWwgY29uZm9ybXMgdG8gdGhlIHtAbGluayBWZXJzaW9uZWRUZXh0RG9jdW1lbnRJZGVudGlmaWVyfSBpbnRlcmZhY2UuXG4gICAgICovXG4gICAgZnVuY3Rpb24gaXModmFsdWUpIHtcbiAgICAgICAgY29uc3QgY2FuZGlkYXRlID0gdmFsdWU7XG4gICAgICAgIHJldHVybiBJcy5kZWZpbmVkKGNhbmRpZGF0ZSkgJiYgSXMuc3RyaW5nKGNhbmRpZGF0ZS51cmkpICYmIElzLmludGVnZXIoY2FuZGlkYXRlLnZlcnNpb24pO1xuICAgIH1cbiAgICBWZXJzaW9uZWRUZXh0RG9jdW1lbnRJZGVudGlmaWVyLmlzID0gaXM7XG59KShWZXJzaW9uZWRUZXh0RG9jdW1lbnRJZGVudGlmaWVyIHx8IChWZXJzaW9uZWRUZXh0RG9jdW1lbnRJZGVudGlmaWVyID0ge30pKTtcbi8qKlxuICogVGhlIE9wdGlvbmFsVmVyc2lvbmVkVGV4dERvY3VtZW50SWRlbnRpZmllciBuYW1lc3BhY2UgcHJvdmlkZXMgaGVscGVyIGZ1bmN0aW9ucyB0byB3b3JrIHdpdGhcbiAqIHtAbGluayBPcHRpb25hbFZlcnNpb25lZFRleHREb2N1bWVudElkZW50aWZpZXJ9IGxpdGVyYWxzLlxuICovXG5leHBvcnQgdmFyIE9wdGlvbmFsVmVyc2lvbmVkVGV4dERvY3VtZW50SWRlbnRpZmllcjtcbihmdW5jdGlvbiAoT3B0aW9uYWxWZXJzaW9uZWRUZXh0RG9jdW1lbnRJZGVudGlmaWVyKSB7XG4gICAgLyoqXG4gICAgICogQ3JlYXRlcyBhIG5ldyBPcHRpb25hbFZlcnNpb25lZFRleHREb2N1bWVudElkZW50aWZpZXIgbGl0ZXJhbC5cbiAgICAgKiBAcGFyYW0gdXJpIFRoZSBkb2N1bWVudCdzIHVyaS5cbiAgICAgKiBAcGFyYW0gdmVyc2lvbiBUaGUgZG9jdW1lbnQncyB2ZXJzaW9uLlxuICAgICAqL1xuICAgIGZ1bmN0aW9uIGNyZWF0ZSh1cmksIHZlcnNpb24pIHtcbiAgICAgICAgcmV0dXJuIHsgdXJpLCB2ZXJzaW9uIH07XG4gICAgfVxuICAgIE9wdGlvbmFsVmVyc2lvbmVkVGV4dERvY3VtZW50SWRlbnRpZmllci5jcmVhdGUgPSBjcmVhdGU7XG4gICAgLyoqXG4gICAgICogQ2hlY2tzIHdoZXRoZXIgdGhlIGdpdmVuIGxpdGVyYWwgY29uZm9ybXMgdG8gdGhlIHtAbGluayBPcHRpb25hbFZlcnNpb25lZFRleHREb2N1bWVudElkZW50aWZpZXJ9IGludGVyZmFjZS5cbiAgICAgKi9cbiAgICBmdW5jdGlvbiBpcyh2YWx1ZSkge1xuICAgICAgICBjb25zdCBjYW5kaWRhdGUgPSB2YWx1ZTtcbiAgICAgICAgcmV0dXJuIElzLmRlZmluZWQoY2FuZGlkYXRlKSAmJiBJcy5zdHJpbmcoY2FuZGlkYXRlLnVyaSkgJiYgKGNhbmRpZGF0ZS52ZXJzaW9uID09PSBudWxsIHx8IElzLmludGVnZXIoY2FuZGlkYXRlLnZlcnNpb24pKTtcbiAgICB9XG4gICAgT3B0aW9uYWxWZXJzaW9uZWRUZXh0RG9jdW1lbnRJZGVudGlmaWVyLmlzID0gaXM7XG59KShPcHRpb25hbFZlcnNpb25lZFRleHREb2N1bWVudElkZW50aWZpZXIgfHwgKE9wdGlvbmFsVmVyc2lvbmVkVGV4dERvY3VtZW50SWRlbnRpZmllciA9IHt9KSk7XG4vKipcbiAqIFByZWRlZmluZWQgTGFuZ3VhZ2Uga2luZHNcbiAqIEBzaW5jZSAzLjE4LjBcbiAqL1xuZXhwb3J0IHZhciBMYW5ndWFnZUtpbmQ7XG4oZnVuY3Rpb24gKExhbmd1YWdlS2luZCkge1xuICAgIExhbmd1YWdlS2luZC5BQkFQID0gJ2FiYXAnO1xuICAgIExhbmd1YWdlS2luZC5XaW5kb3dzQmF0ID0gJ2JhdCc7XG4gICAgTGFuZ3VhZ2VLaW5kLkJpYlRlWCA9ICdiaWJ0ZXgnO1xuICAgIExhbmd1YWdlS2luZC5DbG9qdXJlID0gJ2Nsb2p1cmUnO1xuICAgIExhbmd1YWdlS2luZC5Db2ZmZWVzY3JpcHQgPSAnY29mZmVlc2NyaXB0JztcbiAgICBMYW5ndWFnZUtpbmQuQyA9ICdjJztcbiAgICBMYW5ndWFnZUtpbmQuQ1BQID0gJ2NwcCc7XG4gICAgTGFuZ3VhZ2VLaW5kLkNTaGFycCA9ICdjc2hhcnAnO1xuICAgIExhbmd1YWdlS2luZC5DU1MgPSAnY3NzJztcbiAgICAvKipcbiAgICAgKiBAc2luY2UgMy4xOC4wXG4gICAgICovXG4gICAgTGFuZ3VhZ2VLaW5kLkQgPSAnZCc7XG4gICAgLyoqXG4gICAgICogQHNpbmNlIDMuMTguMFxuICAgICAqL1xuICAgIExhbmd1YWdlS2luZC5EZWxwaGkgPSAncGFzY2FsJztcbiAgICBMYW5ndWFnZUtpbmQuRGlmZiA9ICdkaWZmJztcbiAgICBMYW5ndWFnZUtpbmQuRGFydCA9ICdkYXJ0JztcbiAgICBMYW5ndWFnZUtpbmQuRG9ja2VyZmlsZSA9ICdkb2NrZXJmaWxlJztcbiAgICBMYW5ndWFnZUtpbmQuRWxpeGlyID0gJ2VsaXhpcic7XG4gICAgTGFuZ3VhZ2VLaW5kLkVybGFuZyA9ICdlcmxhbmcnO1xuICAgIExhbmd1YWdlS2luZC5GU2hhcnAgPSAnZnNoYXJwJztcbiAgICBMYW5ndWFnZUtpbmQuR2l0Q29tbWl0ID0gJ2dpdC1jb21taXQnO1xuICAgIExhbmd1YWdlS2luZC5HaXRSZWJhc2UgPSAnZ2l0LXJlYmFzZSc7XG4gICAgTGFuZ3VhZ2VLaW5kLkdvID0gJ2dvJztcbiAgICBMYW5ndWFnZUtpbmQuR3Jvb3Z5ID0gJ2dyb292eSc7XG4gICAgTGFuZ3VhZ2VLaW5kLkhhbmRsZWJhcnMgPSAnaGFuZGxlYmFycyc7XG4gICAgTGFuZ3VhZ2VLaW5kLkhhc2tlbGwgPSAnaGFza2VsbCc7XG4gICAgTGFuZ3VhZ2VLaW5kLkhUTUwgPSAnaHRtbCc7XG4gICAgTGFuZ3VhZ2VLaW5kLkluaSA9ICdpbmknO1xuICAgIExhbmd1YWdlS2luZC5KYXZhID0gJ2phdmEnO1xuICAgIExhbmd1YWdlS2luZC5KYXZhU2NyaXB0ID0gJ2phdmFzY3JpcHQnO1xuICAgIExhbmd1YWdlS2luZC5KYXZhU2NyaXB0UmVhY3QgPSAnamF2YXNjcmlwdHJlYWN0JztcbiAgICBMYW5ndWFnZUtpbmQuSlNPTiA9ICdqc29uJztcbiAgICBMYW5ndWFnZUtpbmQuTGFUZVggPSAnbGF0ZXgnO1xuICAgIExhbmd1YWdlS2luZC5MZXNzID0gJ2xlc3MnO1xuICAgIExhbmd1YWdlS2luZC5MdWEgPSAnbHVhJztcbiAgICBMYW5ndWFnZUtpbmQuTWFrZWZpbGUgPSAnbWFrZWZpbGUnO1xuICAgIExhbmd1YWdlS2luZC5NYXJrZG93biA9ICdtYXJrZG93bic7XG4gICAgTGFuZ3VhZ2VLaW5kLk9iamVjdGl2ZUMgPSAnb2JqZWN0aXZlLWMnO1xuICAgIExhbmd1YWdlS2luZC5PYmplY3RpdmVDUFAgPSAnb2JqZWN0aXZlLWNwcCc7XG4gICAgLyoqXG4gICAgICogQHNpbmNlIDMuMTguMFxuICAgICAqL1xuICAgIExhbmd1YWdlS2luZC5QYXNjYWwgPSAncGFzY2FsJztcbiAgICBMYW5ndWFnZUtpbmQuUGVybCA9ICdwZXJsJztcbiAgICBMYW5ndWFnZUtpbmQuUGVybDYgPSAncGVybDYnO1xuICAgIExhbmd1YWdlS2luZC5QSFAgPSAncGhwJztcbiAgICBMYW5ndWFnZUtpbmQuUGxhaW50ZXh0ID0gJ3BsYWludGV4dCc7XG4gICAgTGFuZ3VhZ2VLaW5kLlBvd2Vyc2hlbGwgPSAncG93ZXJzaGVsbCc7XG4gICAgTGFuZ3VhZ2VLaW5kLlB1ZyA9ICdqYWRlJztcbiAgICBMYW5ndWFnZUtpbmQuUHl0aG9uID0gJ3B5dGhvbic7XG4gICAgTGFuZ3VhZ2VLaW5kLlIgPSAncic7XG4gICAgTGFuZ3VhZ2VLaW5kLlJhem9yID0gJ3Jhem9yJztcbiAgICBMYW5ndWFnZUtpbmQuUnVieSA9ICdydWJ5JztcbiAgICBMYW5ndWFnZUtpbmQuUnVzdCA9ICdydXN0JztcbiAgICBMYW5ndWFnZUtpbmQuU0NTUyA9ICdzY3NzJztcbiAgICBMYW5ndWFnZUtpbmQuU0FTUyA9ICdzYXNzJztcbiAgICBMYW5ndWFnZUtpbmQuU2NhbGEgPSAnc2NhbGEnO1xuICAgIExhbmd1YWdlS2luZC5TaGFkZXJMYWIgPSAnc2hhZGVybGFiJztcbiAgICBMYW5ndWFnZUtpbmQuU2hlbGxTY3JpcHQgPSAnc2hlbGxzY3JpcHQnO1xuICAgIExhbmd1YWdlS2luZC5TUUwgPSAnc3FsJztcbiAgICBMYW5ndWFnZUtpbmQuU3dpZnQgPSAnc3dpZnQnO1xuICAgIExhbmd1YWdlS2luZC5UeXBlU2NyaXB0ID0gJ3R5cGVzY3JpcHQnO1xuICAgIExhbmd1YWdlS2luZC5UeXBlU2NyaXB0UmVhY3QgPSAndHlwZXNjcmlwdHJlYWN0JztcbiAgICBMYW5ndWFnZUtpbmQuVGVYID0gJ3RleCc7XG4gICAgTGFuZ3VhZ2VLaW5kLlZpc3VhbEJhc2ljID0gJ3ZiJztcbiAgICBMYW5ndWFnZUtpbmQuWE1MID0gJ3htbCc7XG4gICAgTGFuZ3VhZ2VLaW5kLlhTTCA9ICd4c2wnO1xuICAgIExhbmd1YWdlS2luZC5ZQU1MID0gJ3lhbWwnO1xufSkoTGFuZ3VhZ2VLaW5kIHx8IChMYW5ndWFnZUtpbmQgPSB7fSkpO1xuLyoqXG4gKiBUaGUgVGV4dERvY3VtZW50SXRlbSBuYW1lc3BhY2UgcHJvdmlkZXMgaGVscGVyIGZ1bmN0aW9ucyB0byB3b3JrIHdpdGhcbiAqIHtAbGluayBUZXh0RG9jdW1lbnRJdGVtfSBsaXRlcmFscy5cbiAqL1xuZXhwb3J0IHZhciBUZXh0RG9jdW1lbnRJdGVtO1xuKGZ1bmN0aW9uIChUZXh0RG9jdW1lbnRJdGVtKSB7XG4gICAgLyoqXG4gICAgICogQ3JlYXRlcyBhIG5ldyBUZXh0RG9jdW1lbnRJdGVtIGxpdGVyYWwuXG4gICAgICogQHBhcmFtIHVyaSBUaGUgZG9jdW1lbnQncyB1cmkuXG4gICAgICogQHBhcmFtIGxhbmd1YWdlSWQgVGhlIGRvY3VtZW50J3MgbGFuZ3VhZ2UgaWRlbnRpZmllci5cbiAgICAgKiBAcGFyYW0gdmVyc2lvbiBUaGUgZG9jdW1lbnQncyB2ZXJzaW9uIG51bWJlci5cbiAgICAgKiBAcGFyYW0gdGV4dCBUaGUgZG9jdW1lbnQncyB0ZXh0LlxuICAgICAqL1xuICAgIGZ1bmN0aW9uIGNyZWF0ZSh1cmksIGxhbmd1YWdlSWQsIHZlcnNpb24sIHRleHQpIHtcbiAgICAgICAgcmV0dXJuIHsgdXJpLCBsYW5ndWFnZUlkLCB2ZXJzaW9uLCB0ZXh0IH07XG4gICAgfVxuICAgIFRleHREb2N1bWVudEl0ZW0uY3JlYXRlID0gY3JlYXRlO1xuICAgIC8qKlxuICAgICAqIENoZWNrcyB3aGV0aGVyIHRoZSBnaXZlbiBsaXRlcmFsIGNvbmZvcm1zIHRvIHRoZSB7QGxpbmsgVGV4dERvY3VtZW50SXRlbX0gaW50ZXJmYWNlLlxuICAgICAqL1xuICAgIGZ1bmN0aW9uIGlzKHZhbHVlKSB7XG4gICAgICAgIGNvbnN0IGNhbmRpZGF0ZSA9IHZhbHVlO1xuICAgICAgICByZXR1cm4gSXMuZGVmaW5lZChjYW5kaWRhdGUpICYmIElzLnN0cmluZyhjYW5kaWRhdGUudXJpKSAmJiBJcy5zdHJpbmcoY2FuZGlkYXRlLmxhbmd1YWdlSWQpICYmIElzLmludGVnZXIoY2FuZGlkYXRlLnZlcnNpb24pICYmIElzLnN0cmluZyhjYW5kaWRhdGUudGV4dCk7XG4gICAgfVxuICAgIFRleHREb2N1bWVudEl0ZW0uaXMgPSBpcztcbn0pKFRleHREb2N1bWVudEl0ZW0gfHwgKFRleHREb2N1bWVudEl0ZW0gPSB7fSkpO1xuLyoqXG4gKiBEZXNjcmliZXMgdGhlIGNvbnRlbnQgdHlwZSB0aGF0IGEgY2xpZW50IHN1cHBvcnRzIGluIHZhcmlvdXNcbiAqIHJlc3VsdCBsaXRlcmFscyBsaWtlIGBIb3ZlcmAsIGBQYXJhbWV0ZXJJbmZvYCBvciBgQ29tcGxldGlvbkl0ZW1gLlxuICpcbiAqIFBsZWFzZSBub3RlIHRoYXQgYE1hcmt1cEtpbmRzYCBtdXN0IG5vdCBzdGFydCB3aXRoIGEgYCRgLiBUaGlzIGtpbmRzXG4gKiBhcmUgcmVzZXJ2ZWQgZm9yIGludGVybmFsIHVzYWdlLlxuICovXG5leHBvcnQgdmFyIE1hcmt1cEtpbmQ7XG4oZnVuY3Rpb24gKE1hcmt1cEtpbmQpIHtcbiAgICAvKipcbiAgICAgKiBQbGFpbiB0ZXh0IGlzIHN1cHBvcnRlZCBhcyBhIGNvbnRlbnQgZm9ybWF0XG4gICAgICovXG4gICAgTWFya3VwS2luZC5QbGFpblRleHQgPSAncGxhaW50ZXh0JztcbiAgICAvKipcbiAgICAgKiBNYXJrZG93biBpcyBzdXBwb3J0ZWQgYXMgYSBjb250ZW50IGZvcm1hdFxuICAgICAqL1xuICAgIE1hcmt1cEtpbmQuTWFya2Rvd24gPSAnbWFya2Rvd24nO1xuICAgIC8qKlxuICAgICAqIENoZWNrcyB3aGV0aGVyIHRoZSBnaXZlbiB2YWx1ZSBpcyBhIHZhbHVlIG9mIHRoZSB7QGxpbmsgTWFya3VwS2luZH0gdHlwZS5cbiAgICAgKi9cbiAgICBmdW5jdGlvbiBpcyh2YWx1ZSkge1xuICAgICAgICBjb25zdCBjYW5kaWRhdGUgPSB2YWx1ZTtcbiAgICAgICAgcmV0dXJuIGNhbmRpZGF0ZSA9PT0gTWFya3VwS2luZC5QbGFpblRleHQgfHwgY2FuZGlkYXRlID09PSBNYXJrdXBLaW5kLk1hcmtkb3duO1xuICAgIH1cbiAgICBNYXJrdXBLaW5kLmlzID0gaXM7XG59KShNYXJrdXBLaW5kIHx8IChNYXJrdXBLaW5kID0ge30pKTtcbmV4cG9ydCB2YXIgTWFya3VwQ29udGVudDtcbihmdW5jdGlvbiAoTWFya3VwQ29udGVudCkge1xuICAgIC8qKlxuICAgICAqIENoZWNrcyB3aGV0aGVyIHRoZSBnaXZlbiB2YWx1ZSBjb25mb3JtcyB0byB0aGUge0BsaW5rIE1hcmt1cENvbnRlbnR9IGludGVyZmFjZS5cbiAgICAgKi9cbiAgICBmdW5jdGlvbiBpcyh2YWx1ZSkge1xuICAgICAgICBjb25zdCBjYW5kaWRhdGUgPSB2YWx1ZTtcbiAgICAgICAgcmV0dXJuIElzLm9iamVjdExpdGVyYWwodmFsdWUpICYmIE1hcmt1cEtpbmQuaXMoY2FuZGlkYXRlLmtpbmQpICYmIElzLnN0cmluZyhjYW5kaWRhdGUudmFsdWUpO1xuICAgIH1cbiAgICBNYXJrdXBDb250ZW50LmlzID0gaXM7XG59KShNYXJrdXBDb250ZW50IHx8IChNYXJrdXBDb250ZW50ID0ge30pKTtcbi8qKlxuICogVGhlIGtpbmQgb2YgYSBjb21wbGV0aW9uIGVudHJ5LlxuICovXG5leHBvcnQgdmFyIENvbXBsZXRpb25JdGVtS2luZDtcbihmdW5jdGlvbiAoQ29tcGxldGlvbkl0ZW1LaW5kKSB7XG4gICAgQ29tcGxldGlvbkl0ZW1LaW5kLlRleHQgPSAxO1xuICAgIENvbXBsZXRpb25JdGVtS2luZC5NZXRob2QgPSAyO1xuICAgIENvbXBsZXRpb25JdGVtS2luZC5GdW5jdGlvbiA9IDM7XG4gICAgQ29tcGxldGlvbkl0ZW1LaW5kLkNvbnN0cnVjdG9yID0gNDtcbiAgICBDb21wbGV0aW9uSXRlbUtpbmQuRmllbGQgPSA1O1xuICAgIENvbXBsZXRpb25JdGVtS2luZC5WYXJpYWJsZSA9IDY7XG4gICAgQ29tcGxldGlvbkl0ZW1LaW5kLkNsYXNzID0gNztcbiAgICBDb21wbGV0aW9uSXRlbUtpbmQuSW50ZXJmYWNlID0gODtcbiAgICBDb21wbGV0aW9uSXRlbUtpbmQuTW9kdWxlID0gOTtcbiAgICBDb21wbGV0aW9uSXRlbUtpbmQuUHJvcGVydHkgPSAxMDtcbiAgICBDb21wbGV0aW9uSXRlbUtpbmQuVW5pdCA9IDExO1xuICAgIENvbXBsZXRpb25JdGVtS2luZC5WYWx1ZSA9IDEyO1xuICAgIENvbXBsZXRpb25JdGVtS2luZC5FbnVtID0gMTM7XG4gICAgQ29tcGxldGlvbkl0ZW1LaW5kLktleXdvcmQgPSAxNDtcbiAgICBDb21wbGV0aW9uSXRlbUtpbmQuU25pcHBldCA9IDE1O1xuICAgIENvbXBsZXRpb25JdGVtS2luZC5Db2xvciA9IDE2O1xuICAgIENvbXBsZXRpb25JdGVtS2luZC5GaWxlID0gMTc7XG4gICAgQ29tcGxldGlvbkl0ZW1LaW5kLlJlZmVyZW5jZSA9IDE4O1xuICAgIENvbXBsZXRpb25JdGVtS2luZC5Gb2xkZXIgPSAxOTtcbiAgICBDb21wbGV0aW9uSXRlbUtpbmQuRW51bU1lbWJlciA9IDIwO1xuICAgIENvbXBsZXRpb25JdGVtS2luZC5Db25zdGFudCA9IDIxO1xuICAgIENvbXBsZXRpb25JdGVtS2luZC5TdHJ1Y3QgPSAyMjtcbiAgICBDb21wbGV0aW9uSXRlbUtpbmQuRXZlbnQgPSAyMztcbiAgICBDb21wbGV0aW9uSXRlbUtpbmQuT3BlcmF0b3IgPSAyNDtcbiAgICBDb21wbGV0aW9uSXRlbUtpbmQuVHlwZVBhcmFtZXRlciA9IDI1O1xufSkoQ29tcGxldGlvbkl0ZW1LaW5kIHx8IChDb21wbGV0aW9uSXRlbUtpbmQgPSB7fSkpO1xuLyoqXG4gKiBEZWZpbmVzIHdoZXRoZXIgdGhlIGluc2VydCB0ZXh0IGluIGEgY29tcGxldGlvbiBpdGVtIHNob3VsZCBiZSBpbnRlcnByZXRlZCBhc1xuICogcGxhaW4gdGV4dCBvciBhIHNuaXBwZXQuXG4gKi9cbmV4cG9ydCB2YXIgSW5zZXJ0VGV4dEZvcm1hdDtcbihmdW5jdGlvbiAoSW5zZXJ0VGV4dEZvcm1hdCkge1xuICAgIC8qKlxuICAgICAqIFRoZSBwcmltYXJ5IHRleHQgdG8gYmUgaW5zZXJ0ZWQgaXMgdHJlYXRlZCBhcyBhIHBsYWluIHN0cmluZy5cbiAgICAgKi9cbiAgICBJbnNlcnRUZXh0Rm9ybWF0LlBsYWluVGV4dCA9IDE7XG4gICAgLyoqXG4gICAgICogVGhlIHByaW1hcnkgdGV4dCB0byBiZSBpbnNlcnRlZCBpcyB0cmVhdGVkIGFzIGEgc25pcHBldC5cbiAgICAgKlxuICAgICAqIEEgc25pcHBldCBjYW4gZGVmaW5lIHRhYiBzdG9wcyBhbmQgcGxhY2Vob2xkZXJzIHdpdGggYCQxYCwgYCQyYFxuICAgICAqIGFuZCBgJHszOmZvb31gLiBgJDBgIGRlZmluZXMgdGhlIGZpbmFsIHRhYiBzdG9wLCBpdCBkZWZhdWx0cyB0b1xuICAgICAqIHRoZSBlbmQgb2YgdGhlIHNuaXBwZXQuIFBsYWNlaG9sZGVycyB3aXRoIGVxdWFsIGlkZW50aWZpZXJzIGFyZSBsaW5rZWQsXG4gICAgICogdGhhdCBpcyB0eXBpbmcgaW4gb25lIHdpbGwgdXBkYXRlIG90aGVycyB0b28uXG4gICAgICpcbiAgICAgKiBTZWUgYWxzbzogaHR0cHM6Ly9taWNyb3NvZnQuZ2l0aHViLmlvL2xhbmd1YWdlLXNlcnZlci1wcm90b2NvbC9zcGVjaWZpY2F0aW9ucy9zcGVjaWZpY2F0aW9uLWN1cnJlbnQvI3NuaXBwZXRfc3ludGF4XG4gICAgICovXG4gICAgSW5zZXJ0VGV4dEZvcm1hdC5TbmlwcGV0ID0gMjtcbn0pKEluc2VydFRleHRGb3JtYXQgfHwgKEluc2VydFRleHRGb3JtYXQgPSB7fSkpO1xuLyoqXG4gKiBDb21wbGV0aW9uIGl0ZW0gdGFncyBhcmUgZXh0cmEgYW5ub3RhdGlvbnMgdGhhdCB0d2VhayB0aGUgcmVuZGVyaW5nIG9mIGEgY29tcGxldGlvblxuICogaXRlbS5cbiAqXG4gKiBAc2luY2UgMy4xNS4wXG4gKi9cbmV4cG9ydCB2YXIgQ29tcGxldGlvbkl0ZW1UYWc7XG4oZnVuY3Rpb24gKENvbXBsZXRpb25JdGVtVGFnKSB7XG4gICAgLyoqXG4gICAgICogUmVuZGVyIGEgY29tcGxldGlvbiBhcyBvYnNvbGV0ZSwgdXN1YWxseSB1c2luZyBhIHN0cmlrZS1vdXQuXG4gICAgICovXG4gICAgQ29tcGxldGlvbkl0ZW1UYWcuRGVwcmVjYXRlZCA9IDE7XG59KShDb21wbGV0aW9uSXRlbVRhZyB8fCAoQ29tcGxldGlvbkl0ZW1UYWcgPSB7fSkpO1xuLyoqXG4gKiBUaGUgSW5zZXJ0UmVwbGFjZUVkaXQgbmFtZXNwYWNlIHByb3ZpZGVzIGZ1bmN0aW9ucyB0byBkZWFsIHdpdGggaW5zZXJ0IC8gcmVwbGFjZSBlZGl0cy5cbiAqXG4gKiBAc2luY2UgMy4xNi4wXG4gKi9cbmV4cG9ydCB2YXIgSW5zZXJ0UmVwbGFjZUVkaXQ7XG4oZnVuY3Rpb24gKEluc2VydFJlcGxhY2VFZGl0KSB7XG4gICAgLyoqXG4gICAgICogQ3JlYXRlcyBhIG5ldyBpbnNlcnQgLyByZXBsYWNlIGVkaXRcbiAgICAgKi9cbiAgICBmdW5jdGlvbiBjcmVhdGUobmV3VGV4dCwgaW5zZXJ0LCByZXBsYWNlKSB7XG4gICAgICAgIHJldHVybiB7IG5ld1RleHQsIGluc2VydCwgcmVwbGFjZSB9O1xuICAgIH1cbiAgICBJbnNlcnRSZXBsYWNlRWRpdC5jcmVhdGUgPSBjcmVhdGU7XG4gICAgLyoqXG4gICAgICogQ2hlY2tzIHdoZXRoZXIgdGhlIGdpdmVuIGxpdGVyYWwgY29uZm9ybXMgdG8gdGhlIHtAbGluayBJbnNlcnRSZXBsYWNlRWRpdH0gaW50ZXJmYWNlLlxuICAgICAqL1xuICAgIGZ1bmN0aW9uIGlzKHZhbHVlKSB7XG4gICAgICAgIGNvbnN0IGNhbmRpZGF0ZSA9IHZhbHVlO1xuICAgICAgICByZXR1cm4gY2FuZGlkYXRlICYmIElzLnN0cmluZyhjYW5kaWRhdGUubmV3VGV4dCkgJiYgUmFuZ2UuaXMoY2FuZGlkYXRlLmluc2VydCkgJiYgUmFuZ2UuaXMoY2FuZGlkYXRlLnJlcGxhY2UpO1xuICAgIH1cbiAgICBJbnNlcnRSZXBsYWNlRWRpdC5pcyA9IGlzO1xufSkoSW5zZXJ0UmVwbGFjZUVkaXQgfHwgKEluc2VydFJlcGxhY2VFZGl0ID0ge30pKTtcbi8qKlxuICogSG93IHdoaXRlc3BhY2UgYW5kIGluZGVudGF0aW9uIGlzIGhhbmRsZWQgZHVyaW5nIGNvbXBsZXRpb25cbiAqIGl0ZW0gaW5zZXJ0aW9uLlxuICpcbiAqIEBzaW5jZSAzLjE2LjBcbiAqL1xuZXhwb3J0IHZhciBJbnNlcnRUZXh0TW9kZTtcbihmdW5jdGlvbiAoSW5zZXJ0VGV4dE1vZGUpIHtcbiAgICAvKipcbiAgICAgKiBUaGUgaW5zZXJ0aW9uIG9yIHJlcGxhY2Ugc3RyaW5ncyBpcyB0YWtlbiBhcyBpdCBpcy4gSWYgdGhlXG4gICAgICogdmFsdWUgaXMgbXVsdGkgbGluZSB0aGUgbGluZXMgYmVsb3cgdGhlIGN1cnNvciB3aWxsIGJlXG4gICAgICogaW5zZXJ0ZWQgdXNpbmcgdGhlIGluZGVudGF0aW9uIGRlZmluZWQgaW4gdGhlIHN0cmluZyB2YWx1ZS5cbiAgICAgKiBUaGUgY2xpZW50IHdpbGwgbm90IGFwcGx5IGFueSBraW5kIG9mIGFkanVzdG1lbnRzIHRvIHRoZVxuICAgICAqIHN0cmluZy5cbiAgICAgKi9cbiAgICBJbnNlcnRUZXh0TW9kZS5hc0lzID0gMTtcbiAgICAvKipcbiAgICAgKiBUaGUgZWRpdG9yIGFkanVzdHMgbGVhZGluZyB3aGl0ZXNwYWNlIG9mIG5ldyBsaW5lcyBzbyB0aGF0XG4gICAgICogdGhleSBtYXRjaCB0aGUgaW5kZW50YXRpb24gdXAgdG8gdGhlIGN1cnNvciBvZiB0aGUgbGluZSBmb3JcbiAgICAgKiB3aGljaCB0aGUgaXRlbSBpcyBhY2NlcHRlZC5cbiAgICAgKlxuICAgICAqIENvbnNpZGVyIGEgbGluZSBsaWtlIHRoaXM6IDwydGFicz48Y3Vyc29yPjwzdGFicz5mb28uIEFjY2VwdGluZyBhXG4gICAgICogbXVsdGkgbGluZSBjb21wbGV0aW9uIGl0ZW0gaXMgaW5kZW50ZWQgdXNpbmcgMiB0YWJzIGFuZCBhbGxcbiAgICAgKiBmb2xsb3dpbmcgbGluZXMgaW5zZXJ0ZWQgd2lsbCBiZSBpbmRlbnRlZCB1c2luZyAyIHRhYnMgYXMgd2VsbC5cbiAgICAgKi9cbiAgICBJbnNlcnRUZXh0TW9kZS5hZGp1c3RJbmRlbnRhdGlvbiA9IDI7XG59KShJbnNlcnRUZXh0TW9kZSB8fCAoSW5zZXJ0VGV4dE1vZGUgPSB7fSkpO1xuLyoqXG4gKiBEZWZpbmVzIGhvdyB2YWx1ZXMgZnJvbSBhIHNldCBvZiBkZWZhdWx0cyBhbmQgYW4gaW5kaXZpZHVhbCBpdGVtIHdpbGwgYmVcbiAqIG1lcmdlZC5cbiAqXG4gKiBAc2luY2UgMy4xOC4wXG4gKi9cbmV4cG9ydCB2YXIgQXBwbHlLaW5kO1xuKGZ1bmN0aW9uIChBcHBseUtpbmQpIHtcbiAgICAvKipcbiAgICAgKiBUaGUgdmFsdWUgZnJvbSB0aGUgaW5kaXZpZHVhbCBpdGVtIChpZiBwcm92aWRlZCBhbmQgbm90IGBudWxsYCkgd2lsbCBiZVxuICAgICAqIHVzZWQgaW5zdGVhZCBvZiB0aGUgZGVmYXVsdC5cbiAgICAgKi9cbiAgICBBcHBseUtpbmQuUmVwbGFjZSA9IDE7XG4gICAgLyoqXG4gICAgICogVGhlIHZhbHVlIGZyb20gdGhlIGl0ZW0gd2lsbCBiZSBtZXJnZWQgd2l0aCB0aGUgZGVmYXVsdC5cbiAgICAgKlxuICAgICAqIFRoZSBzcGVjaWZpYyBydWxlcyBmb3IgbWVyZ2VpbmcgdmFsdWVzIGFyZSBkZWZpbmVkIGFnYWluc3QgZWFjaCBmaWVsZFxuICAgICAqIHRoYXQgc3VwcG9ydHMgbWVyZ2luZy5cbiAgICAgKi9cbiAgICBBcHBseUtpbmQuTWVyZ2UgPSAyO1xufSkoQXBwbHlLaW5kIHx8IChBcHBseUtpbmQgPSB7fSkpO1xuZXhwb3J0IHZhciBDb21wbGV0aW9uSXRlbUxhYmVsRGV0YWlscztcbihmdW5jdGlvbiAoQ29tcGxldGlvbkl0ZW1MYWJlbERldGFpbHMpIHtcbiAgICBmdW5jdGlvbiBpcyh2YWx1ZSkge1xuICAgICAgICBjb25zdCBjYW5kaWRhdGUgPSB2YWx1ZTtcbiAgICAgICAgcmV0dXJuIGNhbmRpZGF0ZSAmJiAoSXMuc3RyaW5nKGNhbmRpZGF0ZS5kZXRhaWwpIHx8IGNhbmRpZGF0ZS5kZXRhaWwgPT09IHVuZGVmaW5lZCkgJiZcbiAgICAgICAgICAgIChJcy5zdHJpbmcoY2FuZGlkYXRlLmRlc2NyaXB0aW9uKSB8fCBjYW5kaWRhdGUuZGVzY3JpcHRpb24gPT09IHVuZGVmaW5lZCk7XG4gICAgfVxuICAgIENvbXBsZXRpb25JdGVtTGFiZWxEZXRhaWxzLmlzID0gaXM7XG59KShDb21wbGV0aW9uSXRlbUxhYmVsRGV0YWlscyB8fCAoQ29tcGxldGlvbkl0ZW1MYWJlbERldGFpbHMgPSB7fSkpO1xuLyoqXG4gKiBUaGUgQ29tcGxldGlvbkl0ZW0gbmFtZXNwYWNlIHByb3ZpZGVzIGZ1bmN0aW9ucyB0byBkZWFsIHdpdGhcbiAqIGNvbXBsZXRpb24gaXRlbXMuXG4gKi9cbmV4cG9ydCB2YXIgQ29tcGxldGlvbkl0ZW07XG4oZnVuY3Rpb24gKENvbXBsZXRpb25JdGVtKSB7XG4gICAgLyoqXG4gICAgICogQ3JlYXRlIGEgY29tcGxldGlvbiBpdGVtIGFuZCBzZWVkIGl0IHdpdGggYSBsYWJlbC5cbiAgICAgKiBAcGFyYW0gbGFiZWwgVGhlIGNvbXBsZXRpb24gaXRlbSdzIGxhYmVsXG4gICAgICovXG4gICAgZnVuY3Rpb24gY3JlYXRlKGxhYmVsKSB7XG4gICAgICAgIHJldHVybiB7IGxhYmVsIH07XG4gICAgfVxuICAgIENvbXBsZXRpb25JdGVtLmNyZWF0ZSA9IGNyZWF0ZTtcbn0pKENvbXBsZXRpb25JdGVtIHx8IChDb21wbGV0aW9uSXRlbSA9IHt9KSk7XG4vKipcbiAqIFRoZSBDb21wbGV0aW9uTGlzdCBuYW1lc3BhY2UgcHJvdmlkZXMgZnVuY3Rpb25zIHRvIGRlYWwgd2l0aFxuICogY29tcGxldGlvbiBsaXN0cy5cbiAqL1xuZXhwb3J0IHZhciBDb21wbGV0aW9uTGlzdDtcbihmdW5jdGlvbiAoQ29tcGxldGlvbkxpc3QpIHtcbiAgICAvKipcbiAgICAgKiBDcmVhdGVzIGEgbmV3IGNvbXBsZXRpb24gbGlzdC5cbiAgICAgKlxuICAgICAqIEBwYXJhbSBpdGVtcyBUaGUgY29tcGxldGlvbiBpdGVtcy5cbiAgICAgKiBAcGFyYW0gaXNJbmNvbXBsZXRlIFRoZSBsaXN0IGlzIG5vdCBjb21wbGV0ZS5cbiAgICAgKi9cbiAgICBmdW5jdGlvbiBjcmVhdGUoaXRlbXMsIGlzSW5jb21wbGV0ZSkge1xuICAgICAgICByZXR1cm4geyBpdGVtczogaXRlbXMgPyBpdGVtcyA6IFtdLCBpc0luY29tcGxldGU6ICEhaXNJbmNvbXBsZXRlIH07XG4gICAgfVxuICAgIENvbXBsZXRpb25MaXN0LmNyZWF0ZSA9IGNyZWF0ZTtcbn0pKENvbXBsZXRpb25MaXN0IHx8IChDb21wbGV0aW9uTGlzdCA9IHt9KSk7XG5leHBvcnQgdmFyIE1hcmtlZFN0cmluZztcbihmdW5jdGlvbiAoTWFya2VkU3RyaW5nKSB7XG4gICAgLyoqXG4gICAgICogQ3JlYXRlcyBhIG1hcmtlZCBzdHJpbmcgZnJvbSBwbGFpbiB0ZXh0LlxuICAgICAqXG4gICAgICogQHBhcmFtIHBsYWluVGV4dCBUaGUgcGxhaW4gdGV4dC5cbiAgICAgKi9cbiAgICBmdW5jdGlvbiBmcm9tUGxhaW5UZXh0KHBsYWluVGV4dCkge1xuICAgICAgICByZXR1cm4gcGxhaW5UZXh0LnJlcGxhY2UoL1tcXFxcYCpfe31bXFxdKCkjK1xcLS4hXS9nLCAnXFxcXCQmJyk7IC8vIGVzY2FwZSBtYXJrZG93biBzeW50YXggdG9rZW5zOiBodHRwOi8vZGFyaW5nZmlyZWJhbGwubmV0L3Byb2plY3RzL21hcmtkb3duL3N5bnRheCNiYWNrc2xhc2hcbiAgICB9XG4gICAgTWFya2VkU3RyaW5nLmZyb21QbGFpblRleHQgPSBmcm9tUGxhaW5UZXh0O1xuICAgIC8qKlxuICAgICAqIENoZWNrcyB3aGV0aGVyIHRoZSBnaXZlbiB2YWx1ZSBjb25mb3JtcyB0byB0aGUge0BsaW5rIE1hcmtlZFN0cmluZ30gdHlwZS5cbiAgICAgKi9cbiAgICBmdW5jdGlvbiBpcyh2YWx1ZSkge1xuICAgICAgICBjb25zdCBjYW5kaWRhdGUgPSB2YWx1ZTtcbiAgICAgICAgcmV0dXJuIElzLnN0cmluZyhjYW5kaWRhdGUpIHx8IChJcy5vYmplY3RMaXRlcmFsKGNhbmRpZGF0ZSkgJiYgSXMuc3RyaW5nKGNhbmRpZGF0ZS5sYW5ndWFnZSkgJiYgSXMuc3RyaW5nKGNhbmRpZGF0ZS52YWx1ZSkpO1xuICAgIH1cbiAgICBNYXJrZWRTdHJpbmcuaXMgPSBpcztcbn0pKE1hcmtlZFN0cmluZyB8fCAoTWFya2VkU3RyaW5nID0ge30pKTtcbmV4cG9ydCB2YXIgSG92ZXI7XG4oZnVuY3Rpb24gKEhvdmVyKSB7XG4gICAgLyoqXG4gICAgICogQ2hlY2tzIHdoZXRoZXIgdGhlIGdpdmVuIHZhbHVlIGNvbmZvcm1zIHRvIHRoZSB7QGxpbmsgSG92ZXJ9IGludGVyZmFjZS5cbiAgICAgKi9cbiAgICBmdW5jdGlvbiBpcyh2YWx1ZSkge1xuICAgICAgICBjb25zdCBjYW5kaWRhdGUgPSB2YWx1ZTtcbiAgICAgICAgcmV0dXJuICEhY2FuZGlkYXRlICYmIElzLm9iamVjdExpdGVyYWwoY2FuZGlkYXRlKSAmJiAoTWFya3VwQ29udGVudC5pcyhjYW5kaWRhdGUuY29udGVudHMpIHx8XG4gICAgICAgICAgICBNYXJrZWRTdHJpbmcuaXMoY2FuZGlkYXRlLmNvbnRlbnRzKSB8fFxuICAgICAgICAgICAgSXMudHlwZWRBcnJheShjYW5kaWRhdGUuY29udGVudHMsIE1hcmtlZFN0cmluZy5pcykpICYmICh2YWx1ZS5yYW5nZSA9PT0gdW5kZWZpbmVkIHx8IFJhbmdlLmlzKHZhbHVlLnJhbmdlKSk7XG4gICAgfVxuICAgIEhvdmVyLmlzID0gaXM7XG59KShIb3ZlciB8fCAoSG92ZXIgPSB7fSkpO1xuLyoqXG4gKiBUaGUgUGFyYW1ldGVySW5mb3JtYXRpb24gbmFtZXNwYWNlIHByb3ZpZGVzIGhlbHBlciBmdW5jdGlvbnMgdG8gd29yayB3aXRoXG4gKiB7QGxpbmsgUGFyYW1ldGVySW5mb3JtYXRpb259IGxpdGVyYWxzLlxuICovXG5leHBvcnQgdmFyIFBhcmFtZXRlckluZm9ybWF0aW9uO1xuKGZ1bmN0aW9uIChQYXJhbWV0ZXJJbmZvcm1hdGlvbikge1xuICAgIC8qKlxuICAgICAqIENyZWF0ZXMgYSBuZXcgcGFyYW1ldGVyIGluZm9ybWF0aW9uIGxpdGVyYWwuXG4gICAgICpcbiAgICAgKiBAcGFyYW0gbGFiZWwgQSBsYWJlbCBzdHJpbmcuXG4gICAgICogQHBhcmFtIGRvY3VtZW50YXRpb24gQSBkb2Mgc3RyaW5nLlxuICAgICAqL1xuICAgIGZ1bmN0aW9uIGNyZWF0ZShsYWJlbCwgZG9jdW1lbnRhdGlvbikge1xuICAgICAgICByZXR1cm4gZG9jdW1lbnRhdGlvbiA/IHsgbGFiZWwsIGRvY3VtZW50YXRpb24gfSA6IHsgbGFiZWwgfTtcbiAgICB9XG4gICAgUGFyYW1ldGVySW5mb3JtYXRpb24uY3JlYXRlID0gY3JlYXRlO1xufSkoUGFyYW1ldGVySW5mb3JtYXRpb24gfHwgKFBhcmFtZXRlckluZm9ybWF0aW9uID0ge30pKTtcbi8qKlxuICogVGhlIFNpZ25hdHVyZUluZm9ybWF0aW9uIG5hbWVzcGFjZSBwcm92aWRlcyBoZWxwZXIgZnVuY3Rpb25zIHRvIHdvcmsgd2l0aFxuICoge0BsaW5rIFNpZ25hdHVyZUluZm9ybWF0aW9ufSBsaXRlcmFscy5cbiAqL1xuZXhwb3J0IHZhciBTaWduYXR1cmVJbmZvcm1hdGlvbjtcbihmdW5jdGlvbiAoU2lnbmF0dXJlSW5mb3JtYXRpb24pIHtcbiAgICBmdW5jdGlvbiBjcmVhdGUobGFiZWwsIGRvY3VtZW50YXRpb24sIC4uLnBhcmFtZXRlcnMpIHtcbiAgICAgICAgY29uc3QgcmVzdWx0ID0geyBsYWJlbCB9O1xuICAgICAgICBpZiAoSXMuZGVmaW5lZChkb2N1bWVudGF0aW9uKSkge1xuICAgICAgICAgICAgcmVzdWx0LmRvY3VtZW50YXRpb24gPSBkb2N1bWVudGF0aW9uO1xuICAgICAgICB9XG4gICAgICAgIGlmIChJcy5kZWZpbmVkKHBhcmFtZXRlcnMpKSB7XG4gICAgICAgICAgICByZXN1bHQucGFyYW1ldGVycyA9IHBhcmFtZXRlcnM7XG4gICAgICAgIH1cbiAgICAgICAgZWxzZSB7XG4gICAgICAgICAgICByZXN1bHQucGFyYW1ldGVycyA9IFtdO1xuICAgICAgICB9XG4gICAgICAgIHJldHVybiByZXN1bHQ7XG4gICAgfVxuICAgIFNpZ25hdHVyZUluZm9ybWF0aW9uLmNyZWF0ZSA9IGNyZWF0ZTtcbn0pKFNpZ25hdHVyZUluZm9ybWF0aW9uIHx8IChTaWduYXR1cmVJbmZvcm1hdGlvbiA9IHt9KSk7XG4vKipcbiAqIEEgZG9jdW1lbnQgaGlnaGxpZ2h0IGtpbmQuXG4gKi9cbmV4cG9ydCB2YXIgRG9jdW1lbnRIaWdobGlnaHRLaW5kO1xuKGZ1bmN0aW9uIChEb2N1bWVudEhpZ2hsaWdodEtpbmQpIHtcbiAgICAvKipcbiAgICAgKiBBIHRleHR1YWwgb2NjdXJyZW5jZS5cbiAgICAgKi9cbiAgICBEb2N1bWVudEhpZ2hsaWdodEtpbmQuVGV4dCA9IDE7XG4gICAgLyoqXG4gICAgICogUmVhZC1hY2Nlc3Mgb2YgYSBzeW1ib2wsIGxpa2UgcmVhZGluZyBhIHZhcmlhYmxlLlxuICAgICAqL1xuICAgIERvY3VtZW50SGlnaGxpZ2h0S2luZC5SZWFkID0gMjtcbiAgICAvKipcbiAgICAgKiBXcml0ZS1hY2Nlc3Mgb2YgYSBzeW1ib2wsIGxpa2Ugd3JpdGluZyB0byBhIHZhcmlhYmxlLlxuICAgICAqL1xuICAgIERvY3VtZW50SGlnaGxpZ2h0S2luZC5Xcml0ZSA9IDM7XG59KShEb2N1bWVudEhpZ2hsaWdodEtpbmQgfHwgKERvY3VtZW50SGlnaGxpZ2h0S2luZCA9IHt9KSk7XG4vKipcbiAqIERvY3VtZW50SGlnaGxpZ2h0IG5hbWVzcGFjZSB0byBwcm92aWRlIGhlbHBlciBmdW5jdGlvbnMgdG8gd29yayB3aXRoXG4gKiB7QGxpbmsgRG9jdW1lbnRIaWdobGlnaHR9IGxpdGVyYWxzLlxuICovXG5leHBvcnQgdmFyIERvY3VtZW50SGlnaGxpZ2h0O1xuKGZ1bmN0aW9uIChEb2N1bWVudEhpZ2hsaWdodCkge1xuICAgIC8qKlxuICAgICAqIENyZWF0ZSBhIERvY3VtZW50SGlnaGxpZ2h0IG9iamVjdC5cbiAgICAgKiBAcGFyYW0gcmFuZ2UgVGhlIHJhbmdlIHRoZSBoaWdobGlnaHQgYXBwbGllcyB0by5cbiAgICAgKiBAcGFyYW0ga2luZCBUaGUgaGlnaGxpZ2h0IGtpbmRcbiAgICAgKi9cbiAgICBmdW5jdGlvbiBjcmVhdGUocmFuZ2UsIGtpbmQpIHtcbiAgICAgICAgY29uc3QgcmVzdWx0ID0geyByYW5nZSB9O1xuICAgICAgICBpZiAoSXMubnVtYmVyKGtpbmQpKSB7XG4gICAgICAgICAgICByZXN1bHQua2luZCA9IGtpbmQ7XG4gICAgICAgIH1cbiAgICAgICAgcmV0dXJuIHJlc3VsdDtcbiAgICB9XG4gICAgRG9jdW1lbnRIaWdobGlnaHQuY3JlYXRlID0gY3JlYXRlO1xufSkoRG9jdW1lbnRIaWdobGlnaHQgfHwgKERvY3VtZW50SGlnaGxpZ2h0ID0ge30pKTtcbi8qKlxuICogQSBzeW1ib2wga2luZC5cbiAqL1xuZXhwb3J0IHZhciBTeW1ib2xLaW5kO1xuKGZ1bmN0aW9uIChTeW1ib2xLaW5kKSB7XG4gICAgU3ltYm9sS2luZC5GaWxlID0gMTtcbiAgICBTeW1ib2xLaW5kLk1vZHVsZSA9IDI7XG4gICAgU3ltYm9sS2luZC5OYW1lc3BhY2UgPSAzO1xuICAgIFN5bWJvbEtpbmQuUGFja2FnZSA9IDQ7XG4gICAgU3ltYm9sS2luZC5DbGFzcyA9IDU7XG4gICAgU3ltYm9sS2luZC5NZXRob2QgPSA2O1xuICAgIFN5bWJvbEtpbmQuUHJvcGVydHkgPSA3O1xuICAgIFN5bWJvbEtpbmQuRmllbGQgPSA4O1xuICAgIFN5bWJvbEtpbmQuQ29uc3RydWN0b3IgPSA5O1xuICAgIFN5bWJvbEtpbmQuRW51bSA9IDEwO1xuICAgIFN5bWJvbEtpbmQuSW50ZXJmYWNlID0gMTE7XG4gICAgU3ltYm9sS2luZC5GdW5jdGlvbiA9IDEyO1xuICAgIFN5bWJvbEtpbmQuVmFyaWFibGUgPSAxMztcbiAgICBTeW1ib2xLaW5kLkNvbnN0YW50ID0gMTQ7XG4gICAgU3ltYm9sS2luZC5TdHJpbmcgPSAxNTtcbiAgICBTeW1ib2xLaW5kLk51bWJlciA9IDE2O1xuICAgIFN5bWJvbEtpbmQuQm9vbGVhbiA9IDE3O1xuICAgIFN5bWJvbEtpbmQuQXJyYXkgPSAxODtcbiAgICBTeW1ib2xLaW5kLk9iamVjdCA9IDE5O1xuICAgIFN5bWJvbEtpbmQuS2V5ID0gMjA7XG4gICAgU3ltYm9sS2luZC5OdWxsID0gMjE7XG4gICAgU3ltYm9sS2luZC5FbnVtTWVtYmVyID0gMjI7XG4gICAgU3ltYm9sS2luZC5TdHJ1Y3QgPSAyMztcbiAgICBTeW1ib2xLaW5kLkV2ZW50ID0gMjQ7XG4gICAgU3ltYm9sS2luZC5PcGVyYXRvciA9IDI1O1xuICAgIFN5bWJvbEtpbmQuVHlwZVBhcmFtZXRlciA9IDI2O1xufSkoU3ltYm9sS2luZCB8fCAoU3ltYm9sS2luZCA9IHt9KSk7XG4vKipcbiAqIFN5bWJvbCB0YWdzIGFyZSBleHRyYSBhbm5vdGF0aW9ucyB0aGF0IHR3ZWFrIHRoZSByZW5kZXJpbmcgb2YgYSBzeW1ib2wuXG4gKlxuICogQHNpbmNlIDMuMTZcbiAqL1xuZXhwb3J0IHZhciBTeW1ib2xUYWc7XG4oZnVuY3Rpb24gKFN5bWJvbFRhZykge1xuICAgIC8qKlxuICAgICAqIFJlbmRlciBhIHN5bWJvbCBhcyBvYnNvbGV0ZSwgdXN1YWxseSB1c2luZyBhIHN0cmlrZS1vdXQuXG4gICAgICovXG4gICAgU3ltYm9sVGFnLkRlcHJlY2F0ZWQgPSAxO1xufSkoU3ltYm9sVGFnIHx8IChTeW1ib2xUYWcgPSB7fSkpO1xuZXhwb3J0IHZhciBTeW1ib2xJbmZvcm1hdGlvbjtcbihmdW5jdGlvbiAoU3ltYm9sSW5mb3JtYXRpb24pIHtcbiAgICAvKipcbiAgICAgKiBDcmVhdGVzIGEgbmV3IHN5bWJvbCBpbmZvcm1hdGlvbiBsaXRlcmFsLlxuICAgICAqXG4gICAgICogQHBhcmFtIG5hbWUgVGhlIG5hbWUgb2YgdGhlIHN5bWJvbC5cbiAgICAgKiBAcGFyYW0ga2luZCBUaGUga2luZCBvZiB0aGUgc3ltYm9sLlxuICAgICAqIEBwYXJhbSByYW5nZSBUaGUgcmFuZ2Ugb2YgdGhlIGxvY2F0aW9uIG9mIHRoZSBzeW1ib2wuXG4gICAgICogQHBhcmFtIHVyaSBUaGUgcmVzb3VyY2Ugb2YgdGhlIGxvY2F0aW9uIG9mIHN5bWJvbC5cbiAgICAgKiBAcGFyYW0gY29udGFpbmVyTmFtZSBUaGUgbmFtZSBvZiB0aGUgc3ltYm9sIGNvbnRhaW5pbmcgdGhlIHN5bWJvbC5cbiAgICAgKi9cbiAgICBmdW5jdGlvbiBjcmVhdGUobmFtZSwga2luZCwgcmFuZ2UsIHVyaSwgY29udGFpbmVyTmFtZSkge1xuICAgICAgICBjb25zdCByZXN1bHQgPSB7XG4gICAgICAgICAgICBuYW1lLFxuICAgICAgICAgICAga2luZCxcbiAgICAgICAgICAgIGxvY2F0aW9uOiB7IHVyaSwgcmFuZ2UgfVxuICAgICAgICB9O1xuICAgICAgICBpZiAoY29udGFpbmVyTmFtZSkge1xuICAgICAgICAgICAgcmVzdWx0LmNvbnRhaW5lck5hbWUgPSBjb250YWluZXJOYW1lO1xuICAgICAgICB9XG4gICAgICAgIHJldHVybiByZXN1bHQ7XG4gICAgfVxuICAgIFN5bWJvbEluZm9ybWF0aW9uLmNyZWF0ZSA9IGNyZWF0ZTtcbn0pKFN5bWJvbEluZm9ybWF0aW9uIHx8IChTeW1ib2xJbmZvcm1hdGlvbiA9IHt9KSk7XG5leHBvcnQgdmFyIFdvcmtzcGFjZVN5bWJvbDtcbihmdW5jdGlvbiAoV29ya3NwYWNlU3ltYm9sKSB7XG4gICAgLyoqXG4gICAgICogQ3JlYXRlIGEgbmV3IHdvcmtzcGFjZSBzeW1ib2wuXG4gICAgICpcbiAgICAgKiBAcGFyYW0gbmFtZSBUaGUgbmFtZSBvZiB0aGUgc3ltYm9sLlxuICAgICAqIEBwYXJhbSBraW5kIFRoZSBraW5kIG9mIHRoZSBzeW1ib2wuXG4gICAgICogQHBhcmFtIHVyaSBUaGUgcmVzb3VyY2Ugb2YgdGhlIGxvY2F0aW9uIG9mIHRoZSBzeW1ib2wuXG4gICAgICogQHBhcmFtIHJhbmdlIEFuIG9wdGlvbnMgcmFuZ2Ugb2YgdGhlIGxvY2F0aW9uLlxuICAgICAqIEByZXR1cm5zIEEgV29ya3NwYWNlU3ltYm9sLlxuICAgICAqL1xuICAgIGZ1bmN0aW9uIGNyZWF0ZShuYW1lLCBraW5kLCB1cmksIHJhbmdlKSB7XG4gICAgICAgIHJldHVybiByYW5nZSAhPT0gdW5kZWZpbmVkXG4gICAgICAgICAgICA/IHsgbmFtZSwga2luZCwgbG9jYXRpb246IHsgdXJpLCByYW5nZSB9IH1cbiAgICAgICAgICAgIDogeyBuYW1lLCBraW5kLCBsb2NhdGlvbjogeyB1cmkgfSB9O1xuICAgIH1cbiAgICBXb3Jrc3BhY2VTeW1ib2wuY3JlYXRlID0gY3JlYXRlO1xufSkoV29ya3NwYWNlU3ltYm9sIHx8IChXb3Jrc3BhY2VTeW1ib2wgPSB7fSkpO1xuZXhwb3J0IHZhciBEb2N1bWVudFN5bWJvbDtcbihmdW5jdGlvbiAoRG9jdW1lbnRTeW1ib2wpIHtcbiAgICAvKipcbiAgICAgKiBDcmVhdGVzIGEgbmV3IHN5bWJvbCBpbmZvcm1hdGlvbiBsaXRlcmFsLlxuICAgICAqXG4gICAgICogQHBhcmFtIG5hbWUgVGhlIG5hbWUgb2YgdGhlIHN5bWJvbC5cbiAgICAgKiBAcGFyYW0gZGV0YWlsIFRoZSBkZXRhaWwgb2YgdGhlIHN5bWJvbC5cbiAgICAgKiBAcGFyYW0ga2luZCBUaGUga2luZCBvZiB0aGUgc3ltYm9sLlxuICAgICAqIEBwYXJhbSByYW5nZSBUaGUgcmFuZ2Ugb2YgdGhlIHN5bWJvbC5cbiAgICAgKiBAcGFyYW0gc2VsZWN0aW9uUmFuZ2UgVGhlIHNlbGVjdGlvblJhbmdlIG9mIHRoZSBzeW1ib2wuXG4gICAgICogQHBhcmFtIGNoaWxkcmVuIENoaWxkcmVuIG9mIHRoZSBzeW1ib2wuXG4gICAgICovXG4gICAgZnVuY3Rpb24gY3JlYXRlKG5hbWUsIGRldGFpbCwga2luZCwgcmFuZ2UsIHNlbGVjdGlvblJhbmdlLCBjaGlsZHJlbikge1xuICAgICAgICBjb25zdCByZXN1bHQgPSB7XG4gICAgICAgICAgICBuYW1lLFxuICAgICAgICAgICAgZGV0YWlsLFxuICAgICAgICAgICAga2luZCxcbiAgICAgICAgICAgIHJhbmdlLFxuICAgICAgICAgICAgc2VsZWN0aW9uUmFuZ2VcbiAgICAgICAgfTtcbiAgICAgICAgaWYgKGNoaWxkcmVuICE9PSB1bmRlZmluZWQpIHtcbiAgICAgICAgICAgIHJlc3VsdC5jaGlsZHJlbiA9IGNoaWxkcmVuO1xuICAgICAgICB9XG4gICAgICAgIHJldHVybiByZXN1bHQ7XG4gICAgfVxuICAgIERvY3VtZW50U3ltYm9sLmNyZWF0ZSA9IGNyZWF0ZTtcbiAgICAvKipcbiAgICAgKiBDaGVja3Mgd2hldGhlciB0aGUgZ2l2ZW4gbGl0ZXJhbCBjb25mb3JtcyB0byB0aGUge0BsaW5rIERvY3VtZW50U3ltYm9sfSBpbnRlcmZhY2UuXG4gICAgICovXG4gICAgZnVuY3Rpb24gaXModmFsdWUpIHtcbiAgICAgICAgY29uc3QgY2FuZGlkYXRlID0gdmFsdWU7XG4gICAgICAgIHJldHVybiBjYW5kaWRhdGUgJiZcbiAgICAgICAgICAgIElzLnN0cmluZyhjYW5kaWRhdGUubmFtZSkgJiYgSXMubnVtYmVyKGNhbmRpZGF0ZS5raW5kKSAmJlxuICAgICAgICAgICAgUmFuZ2UuaXMoY2FuZGlkYXRlLnJhbmdlKSAmJiBSYW5nZS5pcyhjYW5kaWRhdGUuc2VsZWN0aW9uUmFuZ2UpICYmXG4gICAgICAgICAgICAoY2FuZGlkYXRlLmRldGFpbCA9PT0gdW5kZWZpbmVkIHx8IElzLnN0cmluZyhjYW5kaWRhdGUuZGV0YWlsKSkgJiZcbiAgICAgICAgICAgIChjYW5kaWRhdGUuZGVwcmVjYXRlZCA9PT0gdW5kZWZpbmVkIHx8IElzLmJvb2xlYW4oY2FuZGlkYXRlLmRlcHJlY2F0ZWQpKSAmJlxuICAgICAgICAgICAgKGNhbmRpZGF0ZS5jaGlsZHJlbiA9PT0gdW5kZWZpbmVkIHx8IEFycmF5LmlzQXJyYXkoY2FuZGlkYXRlLmNoaWxkcmVuKSkgJiZcbiAgICAgICAgICAgIChjYW5kaWRhdGUudGFncyA9PT0gdW5kZWZpbmVkIHx8IEFycmF5LmlzQXJyYXkoY2FuZGlkYXRlLnRhZ3MpKTtcbiAgICB9XG4gICAgRG9jdW1lbnRTeW1ib2wuaXMgPSBpcztcbn0pKERvY3VtZW50U3ltYm9sIHx8IChEb2N1bWVudFN5bWJvbCA9IHt9KSk7XG4vKipcbiAqIEEgc2V0IG9mIHByZWRlZmluZWQgY29kZSBhY3Rpb24ga2luZHNcbiAqL1xuZXhwb3J0IHZhciBDb2RlQWN0aW9uS2luZDtcbihmdW5jdGlvbiAoQ29kZUFjdGlvbktpbmQpIHtcbiAgICAvKipcbiAgICAgKiBFbXB0eSBraW5kLlxuICAgICAqL1xuICAgIENvZGVBY3Rpb25LaW5kLkVtcHR5ID0gJyc7XG4gICAgLyoqXG4gICAgICogQmFzZSBraW5kIGZvciBxdWlja2ZpeCBhY3Rpb25zOiAncXVpY2tmaXgnXG4gICAgICovXG4gICAgQ29kZUFjdGlvbktpbmQuUXVpY2tGaXggPSAncXVpY2tmaXgnO1xuICAgIC8qKlxuICAgICAqIEJhc2Uga2luZCBmb3IgcmVmYWN0b3JpbmcgYWN0aW9uczogJ3JlZmFjdG9yJ1xuICAgICAqL1xuICAgIENvZGVBY3Rpb25LaW5kLlJlZmFjdG9yID0gJ3JlZmFjdG9yJztcbiAgICAvKipcbiAgICAgKiBCYXNlIGtpbmQgZm9yIHJlZmFjdG9yaW5nIGV4dHJhY3Rpb24gYWN0aW9uczogJ3JlZmFjdG9yLmV4dHJhY3QnXG4gICAgICpcbiAgICAgKiBFeGFtcGxlIGV4dHJhY3QgYWN0aW9uczpcbiAgICAgKlxuICAgICAqIC0gRXh0cmFjdCBtZXRob2RcbiAgICAgKiAtIEV4dHJhY3QgZnVuY3Rpb25cbiAgICAgKiAtIEV4dHJhY3QgdmFyaWFibGVcbiAgICAgKiAtIEV4dHJhY3QgaW50ZXJmYWNlIGZyb20gY2xhc3NcbiAgICAgKiAtIC4uLlxuICAgICAqL1xuICAgIENvZGVBY3Rpb25LaW5kLlJlZmFjdG9yRXh0cmFjdCA9ICdyZWZhY3Rvci5leHRyYWN0JztcbiAgICAvKipcbiAgICAgKiBCYXNlIGtpbmQgZm9yIHJlZmFjdG9yaW5nIGlubGluZSBhY3Rpb25zOiAncmVmYWN0b3IuaW5saW5lJ1xuICAgICAqXG4gICAgICogRXhhbXBsZSBpbmxpbmUgYWN0aW9uczpcbiAgICAgKlxuICAgICAqIC0gSW5saW5lIGZ1bmN0aW9uXG4gICAgICogLSBJbmxpbmUgdmFyaWFibGVcbiAgICAgKiAtIElubGluZSBjb25zdGFudFxuICAgICAqIC0gLi4uXG4gICAgICovXG4gICAgQ29kZUFjdGlvbktpbmQuUmVmYWN0b3JJbmxpbmUgPSAncmVmYWN0b3IuaW5saW5lJztcbiAgICAvKipcbiAgICAgKiBCYXNlIGtpbmQgZm9yIHJlZmFjdG9yaW5nIG1vdmUgYWN0aW9uczogYHJlZmFjdG9yLm1vdmVgXG4gICAgICpcbiAgICAgKiBFeGFtcGxlIG1vdmUgYWN0aW9uczpcbiAgICAgKlxuICAgICAqIC0gTW92ZSBhIGZ1bmN0aW9uIHRvIGEgbmV3IGZpbGVcbiAgICAgKiAtIE1vdmUgYSBwcm9wZXJ0eSBiZXR3ZWVuIGNsYXNzZXNcbiAgICAgKiAtIE1vdmUgbWV0aG9kIHRvIGJhc2UgY2xhc3NcbiAgICAgKiAtIC4uLlxuICAgICAqXG4gICAgICogQHNpbmNlIDMuMTguMFxuICAgICAqL1xuICAgIENvZGVBY3Rpb25LaW5kLlJlZmFjdG9yTW92ZSA9ICdyZWZhY3Rvci5tb3ZlJztcbiAgICAvKipcbiAgICAgKiBCYXNlIGtpbmQgZm9yIHJlZmFjdG9yaW5nIHJld3JpdGUgYWN0aW9uczogJ3JlZmFjdG9yLnJld3JpdGUnXG4gICAgICpcbiAgICAgKiBFeGFtcGxlIHJld3JpdGUgYWN0aW9uczpcbiAgICAgKlxuICAgICAqIC0gQ29udmVydCBKYXZhU2NyaXB0IGZ1bmN0aW9uIHRvIGNsYXNzXG4gICAgICogLSBBZGQgb3IgcmVtb3ZlIHBhcmFtZXRlclxuICAgICAqIC0gRW5jYXBzdWxhdGUgZmllbGRcbiAgICAgKiAtIE1ha2UgbWV0aG9kIHN0YXRpY1xuICAgICAqIC0gTW92ZSBtZXRob2QgdG8gYmFzZSBjbGFzc1xuICAgICAqIC0gLi4uXG4gICAgICovXG4gICAgQ29kZUFjdGlvbktpbmQuUmVmYWN0b3JSZXdyaXRlID0gJ3JlZmFjdG9yLnJld3JpdGUnO1xuICAgIC8qKlxuICAgICAqIEJhc2Uga2luZCBmb3Igc291cmNlIGFjdGlvbnM6IGBzb3VyY2VgXG4gICAgICpcbiAgICAgKiBTb3VyY2UgY29kZSBhY3Rpb25zIGFwcGx5IHRvIHRoZSBlbnRpcmUgZmlsZS5cbiAgICAgKi9cbiAgICBDb2RlQWN0aW9uS2luZC5Tb3VyY2UgPSAnc291cmNlJztcbiAgICAvKipcbiAgICAgKiBCYXNlIGtpbmQgZm9yIGFuIG9yZ2FuaXplIGltcG9ydHMgc291cmNlIGFjdGlvbjogYHNvdXJjZS5vcmdhbml6ZUltcG9ydHNgXG4gICAgICovXG4gICAgQ29kZUFjdGlvbktpbmQuU291cmNlT3JnYW5pemVJbXBvcnRzID0gJ3NvdXJjZS5vcmdhbml6ZUltcG9ydHMnO1xuICAgIC8qKlxuICAgICAqIEJhc2Uga2luZCBmb3IgYXV0by1maXggc291cmNlIGFjdGlvbnM6IGBzb3VyY2UuZml4QWxsYC5cbiAgICAgKlxuICAgICAqIEZpeCBhbGwgYWN0aW9ucyBhdXRvbWF0aWNhbGx5IGZpeCBlcnJvcnMgdGhhdCBoYXZlIGEgY2xlYXIgZml4IHRoYXQgZG8gbm90IHJlcXVpcmUgdXNlciBpbnB1dC5cbiAgICAgKiBUaGV5IHNob3VsZCBub3Qgc3VwcHJlc3MgZXJyb3JzIG9yIHBlcmZvcm0gdW5zYWZlIGZpeGVzIHN1Y2ggYXMgZ2VuZXJhdGluZyBuZXcgdHlwZXMgb3IgY2xhc3Nlcy5cbiAgICAgKlxuICAgICAqIEBzaW5jZSAzLjE1LjBcbiAgICAgKi9cbiAgICBDb2RlQWN0aW9uS2luZC5Tb3VyY2VGaXhBbGwgPSAnc291cmNlLmZpeEFsbCc7XG4gICAgLyoqXG4gICAgICogQmFzZSBraW5kIGZvciBhbGwgY29kZSBhY3Rpb25zIGFwcGx5aW5nIHRvIHRoZSBlbnRpcmUgbm90ZWJvb2sncyBzY29wZS4gQ29kZUFjdGlvbktpbmRzIHVzaW5nXG4gICAgICogdGhpcyBzaG91bGQgYWx3YXlzIGJlZ2luIHdpdGggYG5vdGVib29rLmBcbiAgICAgKlxuICAgICAqIEBzaW5jZSAzLjE4LjBcbiAgICAgKi9cbiAgICBDb2RlQWN0aW9uS2luZC5Ob3RlYm9vayA9ICdub3RlYm9vayc7XG59KShDb2RlQWN0aW9uS2luZCB8fCAoQ29kZUFjdGlvbktpbmQgPSB7fSkpO1xuLyoqXG4gKiBUaGUgcmVhc29uIHdoeSBjb2RlIGFjdGlvbnMgd2VyZSByZXF1ZXN0ZWQuXG4gKlxuICogQHNpbmNlIDMuMTcuMFxuICovXG5leHBvcnQgdmFyIENvZGVBY3Rpb25UcmlnZ2VyS2luZDtcbihmdW5jdGlvbiAoQ29kZUFjdGlvblRyaWdnZXJLaW5kKSB7XG4gICAgLyoqXG4gICAgICogQ29kZSBhY3Rpb25zIHdlcmUgZXhwbGljaXRseSByZXF1ZXN0ZWQgYnkgdGhlIHVzZXIgb3IgYnkgYW4gZXh0ZW5zaW9uLlxuICAgICAqL1xuICAgIENvZGVBY3Rpb25UcmlnZ2VyS2luZC5JbnZva2VkID0gMTtcbiAgICAvKipcbiAgICAgKiBDb2RlIGFjdGlvbnMgd2VyZSByZXF1ZXN0ZWQgYXV0b21hdGljYWxseS5cbiAgICAgKlxuICAgICAqIFRoaXMgdHlwaWNhbGx5IGhhcHBlbnMgd2hlbiBjdXJyZW50IHNlbGVjdGlvbiBpbiBhIGZpbGUgY2hhbmdlcywgYnV0IGNhblxuICAgICAqIGFsc28gYmUgdHJpZ2dlcmVkIHdoZW4gZmlsZSBjb250ZW50IGNoYW5nZXMuXG4gICAgICovXG4gICAgQ29kZUFjdGlvblRyaWdnZXJLaW5kLkF1dG9tYXRpYyA9IDI7XG59KShDb2RlQWN0aW9uVHJpZ2dlcktpbmQgfHwgKENvZGVBY3Rpb25UcmlnZ2VyS2luZCA9IHt9KSk7XG4vKipcbiAqIFRoZSBDb2RlQWN0aW9uQ29udGV4dCBuYW1lc3BhY2UgcHJvdmlkZXMgaGVscGVyIGZ1bmN0aW9ucyB0byB3b3JrIHdpdGhcbiAqIHtAbGluayBDb2RlQWN0aW9uQ29udGV4dH0gbGl0ZXJhbHMuXG4gKi9cbmV4cG9ydCB2YXIgQ29kZUFjdGlvbkNvbnRleHQ7XG4oZnVuY3Rpb24gKENvZGVBY3Rpb25Db250ZXh0KSB7XG4gICAgLyoqXG4gICAgICogQ3JlYXRlcyBhIG5ldyBDb2RlQWN0aW9uQ29udGV4dCBsaXRlcmFsLlxuICAgICAqL1xuICAgIGZ1bmN0aW9uIGNyZWF0ZShkaWFnbm9zdGljcywgb25seSwgdHJpZ2dlcktpbmQpIHtcbiAgICAgICAgY29uc3QgcmVzdWx0ID0geyBkaWFnbm9zdGljcyB9O1xuICAgICAgICBpZiAob25seSAhPT0gdW5kZWZpbmVkICYmIG9ubHkgIT09IG51bGwpIHtcbiAgICAgICAgICAgIHJlc3VsdC5vbmx5ID0gb25seTtcbiAgICAgICAgfVxuICAgICAgICBpZiAodHJpZ2dlcktpbmQgIT09IHVuZGVmaW5lZCAmJiB0cmlnZ2VyS2luZCAhPT0gbnVsbCkge1xuICAgICAgICAgICAgcmVzdWx0LnRyaWdnZXJLaW5kID0gdHJpZ2dlcktpbmQ7XG4gICAgICAgIH1cbiAgICAgICAgcmV0dXJuIHJlc3VsdDtcbiAgICB9XG4gICAgQ29kZUFjdGlvbkNvbnRleHQuY3JlYXRlID0gY3JlYXRlO1xuICAgIC8qKlxuICAgICAqIENoZWNrcyB3aGV0aGVyIHRoZSBnaXZlbiBsaXRlcmFsIGNvbmZvcm1zIHRvIHRoZSB7QGxpbmsgQ29kZUFjdGlvbkNvbnRleHR9IGludGVyZmFjZS5cbiAgICAgKi9cbiAgICBmdW5jdGlvbiBpcyh2YWx1ZSkge1xuICAgICAgICBjb25zdCBjYW5kaWRhdGUgPSB2YWx1ZTtcbiAgICAgICAgcmV0dXJuIElzLmRlZmluZWQoY2FuZGlkYXRlKSAmJiBJcy50eXBlZEFycmF5KGNhbmRpZGF0ZS5kaWFnbm9zdGljcywgRGlhZ25vc3RpYy5pcylcbiAgICAgICAgICAgICYmIChjYW5kaWRhdGUub25seSA9PT0gdW5kZWZpbmVkIHx8IElzLnR5cGVkQXJyYXkoY2FuZGlkYXRlLm9ubHksIElzLnN0cmluZykpXG4gICAgICAgICAgICAmJiAoY2FuZGlkYXRlLnRyaWdnZXJLaW5kID09PSB1bmRlZmluZWQgfHwgY2FuZGlkYXRlLnRyaWdnZXJLaW5kID09PSBDb2RlQWN0aW9uVHJpZ2dlcktpbmQuSW52b2tlZCB8fCBjYW5kaWRhdGUudHJpZ2dlcktpbmQgPT09IENvZGVBY3Rpb25UcmlnZ2VyS2luZC5BdXRvbWF0aWMpO1xuICAgIH1cbiAgICBDb2RlQWN0aW9uQ29udGV4dC5pcyA9IGlzO1xufSkoQ29kZUFjdGlvbkNvbnRleHQgfHwgKENvZGVBY3Rpb25Db250ZXh0ID0ge30pKTtcbi8qKlxuICogQ29kZSBhY3Rpb24gdGFncyBhcmUgZXh0cmEgYW5ub3RhdGlvbnMgdGhhdCB0d2VhayB0aGUgYmVoYXZpb3Igb2YgYSBjb2RlIGFjdGlvbi5cbiAqXG4gKiBAc2luY2UgMy4xOC4wXG4gKi9cbmV4cG9ydCB2YXIgQ29kZUFjdGlvblRhZztcbihmdW5jdGlvbiAoQ29kZUFjdGlvblRhZykge1xuICAgIC8qKlxuICAgICAqIE1hcmtzIHRoZSBjb2RlIGFjdGlvbiBhcyBMTE0tZ2VuZXJhdGVkLlxuICAgICAqL1xuICAgIENvZGVBY3Rpb25UYWcuTExNR2VuZXJhdGVkID0gMTtcbiAgICAvKipcbiAgICAgKiBDaGVja3Mgd2hldGhlciB0aGUgZ2l2ZW4gbGl0ZXJhbCBjb25mb3JtcyB0byB0aGUge0BsaW5rIENvZGVBY3Rpb25UYWd9IGludGVyZmFjZS5cbiAgICAgKi9cbiAgICBmdW5jdGlvbiBpcyh2YWx1ZSkge1xuICAgICAgICByZXR1cm4gSXMuZGVmaW5lZCh2YWx1ZSkgJiYgdmFsdWUgPT09IENvZGVBY3Rpb25UYWcuTExNR2VuZXJhdGVkO1xuICAgIH1cbiAgICBDb2RlQWN0aW9uVGFnLmlzID0gaXM7XG59KShDb2RlQWN0aW9uVGFnIHx8IChDb2RlQWN0aW9uVGFnID0ge30pKTtcbmV4cG9ydCB2YXIgQ29kZUFjdGlvbjtcbihmdW5jdGlvbiAoQ29kZUFjdGlvbikge1xuICAgIGZ1bmN0aW9uIGNyZWF0ZSh0aXRsZSwga2luZE9yQ29tbWFuZE9yRWRpdCwga2luZCkge1xuICAgICAgICBjb25zdCByZXN1bHQgPSB7IHRpdGxlIH07XG4gICAgICAgIGxldCBjaGVja0tpbmQgPSB0cnVlO1xuICAgICAgICBpZiAodHlwZW9mIGtpbmRPckNvbW1hbmRPckVkaXQgPT09ICdzdHJpbmcnKSB7XG4gICAgICAgICAgICBjaGVja0tpbmQgPSBmYWxzZTtcbiAgICAgICAgICAgIHJlc3VsdC5raW5kID0ga2luZE9yQ29tbWFuZE9yRWRpdDtcbiAgICAgICAgfVxuICAgICAgICBlbHNlIGlmIChDb21tYW5kLmlzKGtpbmRPckNvbW1hbmRPckVkaXQpKSB7XG4gICAgICAgICAgICByZXN1bHQuY29tbWFuZCA9IGtpbmRPckNvbW1hbmRPckVkaXQ7XG4gICAgICAgIH1cbiAgICAgICAgZWxzZSB7XG4gICAgICAgICAgICByZXN1bHQuZWRpdCA9IGtpbmRPckNvbW1hbmRPckVkaXQ7XG4gICAgICAgIH1cbiAgICAgICAgaWYgKGNoZWNrS2luZCAmJiBraW5kICE9PSB1bmRlZmluZWQpIHtcbiAgICAgICAgICAgIHJlc3VsdC5raW5kID0ga2luZDtcbiAgICAgICAgfVxuICAgICAgICByZXR1cm4gcmVzdWx0O1xuICAgIH1cbiAgICBDb2RlQWN0aW9uLmNyZWF0ZSA9IGNyZWF0ZTtcbiAgICBmdW5jdGlvbiBpcyh2YWx1ZSkge1xuICAgICAgICBjb25zdCBjYW5kaWRhdGUgPSB2YWx1ZTtcbiAgICAgICAgcmV0dXJuIGNhbmRpZGF0ZSAmJiBJcy5zdHJpbmcoY2FuZGlkYXRlLnRpdGxlKSAmJlxuICAgICAgICAgICAgKGNhbmRpZGF0ZS5kaWFnbm9zdGljcyA9PT0gdW5kZWZpbmVkIHx8IElzLnR5cGVkQXJyYXkoY2FuZGlkYXRlLmRpYWdub3N0aWNzLCBEaWFnbm9zdGljLmlzKSkgJiZcbiAgICAgICAgICAgIChjYW5kaWRhdGUua2luZCA9PT0gdW5kZWZpbmVkIHx8IElzLnN0cmluZyhjYW5kaWRhdGUua2luZCkpICYmXG4gICAgICAgICAgICAoY2FuZGlkYXRlLmVkaXQgIT09IHVuZGVmaW5lZCB8fCBjYW5kaWRhdGUuY29tbWFuZCAhPT0gdW5kZWZpbmVkKSAmJlxuICAgICAgICAgICAgKGNhbmRpZGF0ZS5jb21tYW5kID09PSB1bmRlZmluZWQgfHwgQ29tbWFuZC5pcyhjYW5kaWRhdGUuY29tbWFuZCkpICYmXG4gICAgICAgICAgICAoY2FuZGlkYXRlLmlzUHJlZmVycmVkID09PSB1bmRlZmluZWQgfHwgSXMuYm9vbGVhbihjYW5kaWRhdGUuaXNQcmVmZXJyZWQpKSAmJlxuICAgICAgICAgICAgKGNhbmRpZGF0ZS5lZGl0ID09PSB1bmRlZmluZWQgfHwgV29ya3NwYWNlRWRpdC5pcyhjYW5kaWRhdGUuZWRpdCkpICYmXG4gICAgICAgICAgICAoY2FuZGlkYXRlLnRhZ3MgPT09IHVuZGVmaW5lZCB8fCBJcy50eXBlZEFycmF5KGNhbmRpZGF0ZS50YWdzLCBDb2RlQWN0aW9uVGFnLmlzKSk7XG4gICAgfVxuICAgIENvZGVBY3Rpb24uaXMgPSBpcztcbn0pKENvZGVBY3Rpb24gfHwgKENvZGVBY3Rpb24gPSB7fSkpO1xuLyoqXG4gKiBUaGUgQ29kZUxlbnMgbmFtZXNwYWNlIHByb3ZpZGVzIGhlbHBlciBmdW5jdGlvbnMgdG8gd29yayB3aXRoXG4gKiB7QGxpbmsgQ29kZUxlbnN9IGxpdGVyYWxzLlxuICovXG5leHBvcnQgdmFyIENvZGVMZW5zO1xuKGZ1bmN0aW9uIChDb2RlTGVucykge1xuICAgIC8qKlxuICAgICAqIENyZWF0ZXMgYSBuZXcgQ29kZUxlbnMgbGl0ZXJhbC5cbiAgICAgKi9cbiAgICBmdW5jdGlvbiBjcmVhdGUocmFuZ2UsIGRhdGEpIHtcbiAgICAgICAgY29uc3QgcmVzdWx0ID0geyByYW5nZSB9O1xuICAgICAgICBpZiAoSXMuZGVmaW5lZChkYXRhKSkge1xuICAgICAgICAgICAgcmVzdWx0LmRhdGEgPSBkYXRhO1xuICAgICAgICB9XG4gICAgICAgIHJldHVybiByZXN1bHQ7XG4gICAgfVxuICAgIENvZGVMZW5zLmNyZWF0ZSA9IGNyZWF0ZTtcbiAgICAvKipcbiAgICAgKiBDaGVja3Mgd2hldGhlciB0aGUgZ2l2ZW4gbGl0ZXJhbCBjb25mb3JtcyB0byB0aGUge0BsaW5rIENvZGVMZW5zfSBpbnRlcmZhY2UuXG4gICAgICovXG4gICAgZnVuY3Rpb24gaXModmFsdWUpIHtcbiAgICAgICAgY29uc3QgY2FuZGlkYXRlID0gdmFsdWU7XG4gICAgICAgIHJldHVybiBJcy5kZWZpbmVkKGNhbmRpZGF0ZSkgJiYgUmFuZ2UuaXMoY2FuZGlkYXRlLnJhbmdlKSAmJiAoSXMudW5kZWZpbmVkKGNhbmRpZGF0ZS5jb21tYW5kKSB8fCBDb21tYW5kLmlzKGNhbmRpZGF0ZS5jb21tYW5kKSk7XG4gICAgfVxuICAgIENvZGVMZW5zLmlzID0gaXM7XG59KShDb2RlTGVucyB8fCAoQ29kZUxlbnMgPSB7fSkpO1xuLyoqXG4gKiBUaGUgRm9ybWF0dGluZ09wdGlvbnMgbmFtZXNwYWNlIHByb3ZpZGVzIGhlbHBlciBmdW5jdGlvbnMgdG8gd29yayB3aXRoXG4gKiB7QGxpbmsgRm9ybWF0dGluZ09wdGlvbnN9IGxpdGVyYWxzLlxuICovXG5leHBvcnQgdmFyIEZvcm1hdHRpbmdPcHRpb25zO1xuKGZ1bmN0aW9uIChGb3JtYXR0aW5nT3B0aW9ucykge1xuICAgIC8qKlxuICAgICAqIENyZWF0ZXMgYSBuZXcgRm9ybWF0dGluZ09wdGlvbnMgbGl0ZXJhbC5cbiAgICAgKi9cbiAgICBmdW5jdGlvbiBjcmVhdGUodGFiU2l6ZSwgaW5zZXJ0U3BhY2VzKSB7XG4gICAgICAgIHJldHVybiB7IHRhYlNpemUsIGluc2VydFNwYWNlcyB9O1xuICAgIH1cbiAgICBGb3JtYXR0aW5nT3B0aW9ucy5jcmVhdGUgPSBjcmVhdGU7XG4gICAgLyoqXG4gICAgICogQ2hlY2tzIHdoZXRoZXIgdGhlIGdpdmVuIGxpdGVyYWwgY29uZm9ybXMgdG8gdGhlIHtAbGluayBGb3JtYXR0aW5nT3B0aW9uc30gaW50ZXJmYWNlLlxuICAgICAqL1xuICAgIGZ1bmN0aW9uIGlzKHZhbHVlKSB7XG4gICAgICAgIGNvbnN0IGNhbmRpZGF0ZSA9IHZhbHVlO1xuICAgICAgICByZXR1cm4gSXMuZGVmaW5lZChjYW5kaWRhdGUpICYmIElzLnVpbnRlZ2VyKGNhbmRpZGF0ZS50YWJTaXplKSAmJiBJcy5ib29sZWFuKGNhbmRpZGF0ZS5pbnNlcnRTcGFjZXMpO1xuICAgIH1cbiAgICBGb3JtYXR0aW5nT3B0aW9ucy5pcyA9IGlzO1xufSkoRm9ybWF0dGluZ09wdGlvbnMgfHwgKEZvcm1hdHRpbmdPcHRpb25zID0ge30pKTtcbi8qKlxuICogVGhlIERvY3VtZW50TGluayBuYW1lc3BhY2UgcHJvdmlkZXMgaGVscGVyIGZ1bmN0aW9ucyB0byB3b3JrIHdpdGhcbiAqIHtAbGluayBEb2N1bWVudExpbmt9IGxpdGVyYWxzLlxuICovXG5leHBvcnQgdmFyIERvY3VtZW50TGluaztcbihmdW5jdGlvbiAoRG9jdW1lbnRMaW5rKSB7XG4gICAgLyoqXG4gICAgICogQ3JlYXRlcyBhIG5ldyBEb2N1bWVudExpbmsgbGl0ZXJhbC5cbiAgICAgKi9cbiAgICBmdW5jdGlvbiBjcmVhdGUocmFuZ2UsIHRhcmdldCwgZGF0YSkge1xuICAgICAgICByZXR1cm4geyByYW5nZSwgdGFyZ2V0LCBkYXRhIH07XG4gICAgfVxuICAgIERvY3VtZW50TGluay5jcmVhdGUgPSBjcmVhdGU7XG4gICAgLyoqXG4gICAgICogQ2hlY2tzIHdoZXRoZXIgdGhlIGdpdmVuIGxpdGVyYWwgY29uZm9ybXMgdG8gdGhlIHtAbGluayBEb2N1bWVudExpbmt9IGludGVyZmFjZS5cbiAgICAgKi9cbiAgICBmdW5jdGlvbiBpcyh2YWx1ZSkge1xuICAgICAgICBjb25zdCBjYW5kaWRhdGUgPSB2YWx1ZTtcbiAgICAgICAgcmV0dXJuIElzLmRlZmluZWQoY2FuZGlkYXRlKSAmJiBSYW5nZS5pcyhjYW5kaWRhdGUucmFuZ2UpICYmIChJcy51bmRlZmluZWQoY2FuZGlkYXRlLnRhcmdldCkgfHwgSXMuc3RyaW5nKGNhbmRpZGF0ZS50YXJnZXQpKTtcbiAgICB9XG4gICAgRG9jdW1lbnRMaW5rLmlzID0gaXM7XG59KShEb2N1bWVudExpbmsgfHwgKERvY3VtZW50TGluayA9IHt9KSk7XG4vKipcbiAqIFRoZSBTZWxlY3Rpb25SYW5nZSBuYW1lc3BhY2UgcHJvdmlkZXMgaGVscGVyIGZ1bmN0aW9uIHRvIHdvcmsgd2l0aFxuICogU2VsZWN0aW9uUmFuZ2UgbGl0ZXJhbHMuXG4gKi9cbmV4cG9ydCB2YXIgU2VsZWN0aW9uUmFuZ2U7XG4oZnVuY3Rpb24gKFNlbGVjdGlvblJhbmdlKSB7XG4gICAgLyoqXG4gICAgICogQ3JlYXRlcyBhIG5ldyBTZWxlY3Rpb25SYW5nZVxuICAgICAqIEBwYXJhbSByYW5nZSB0aGUgcmFuZ2UuXG4gICAgICogQHBhcmFtIHBhcmVudCBhbiBvcHRpb25hbCBwYXJlbnQuXG4gICAgICovXG4gICAgZnVuY3Rpb24gY3JlYXRlKHJhbmdlLCBwYXJlbnQpIHtcbiAgICAgICAgcmV0dXJuIHsgcmFuZ2UsIHBhcmVudCB9O1xuICAgIH1cbiAgICBTZWxlY3Rpb25SYW5nZS5jcmVhdGUgPSBjcmVhdGU7XG4gICAgZnVuY3Rpb24gaXModmFsdWUpIHtcbiAgICAgICAgY29uc3QgY2FuZGlkYXRlID0gdmFsdWU7XG4gICAgICAgIHJldHVybiBJcy5vYmplY3RMaXRlcmFsKGNhbmRpZGF0ZSkgJiYgUmFuZ2UuaXMoY2FuZGlkYXRlLnJhbmdlKSAmJiAoY2FuZGlkYXRlLnBhcmVudCA9PT0gdW5kZWZpbmVkIHx8IFNlbGVjdGlvblJhbmdlLmlzKGNhbmRpZGF0ZS5wYXJlbnQpKTtcbiAgICB9XG4gICAgU2VsZWN0aW9uUmFuZ2UuaXMgPSBpcztcbn0pKFNlbGVjdGlvblJhbmdlIHx8IChTZWxlY3Rpb25SYW5nZSA9IHt9KSk7XG4vKipcbiAqIEEgc2V0IG9mIHByZWRlZmluZWQgdG9rZW4gdHlwZXMuIFRoaXMgc2V0IGlzIG5vdCBmaXhlZFxuICogYW4gY2xpZW50cyBjYW4gc3BlY2lmeSBhZGRpdGlvbmFsIHRva2VuIHR5cGVzIHZpYSB0aGVcbiAqIGNvcnJlc3BvbmRpbmcgY2xpZW50IGNhcGFiaWxpdGllcy5cbiAqXG4gKiBAc2luY2UgMy4xNi4wXG4gKi9cbmV4cG9ydCB2YXIgU2VtYW50aWNUb2tlblR5cGVzO1xuKGZ1bmN0aW9uIChTZW1hbnRpY1Rva2VuVHlwZXMpIHtcbiAgICBTZW1hbnRpY1Rva2VuVHlwZXNbXCJuYW1lc3BhY2VcIl0gPSBcIm5hbWVzcGFjZVwiO1xuICAgIC8qKlxuICAgICAqIFJlcHJlc2VudHMgYSBnZW5lcmljIHR5cGUuIEFjdHMgYXMgYSBmYWxsYmFjayBmb3IgdHlwZXMgd2hpY2ggY2FuJ3QgYmUgbWFwcGVkIHRvXG4gICAgICogYSBzcGVjaWZpYyB0eXBlIGxpa2UgY2xhc3Mgb3IgZW51bS5cbiAgICAgKi9cbiAgICBTZW1hbnRpY1Rva2VuVHlwZXNbXCJ0eXBlXCJdID0gXCJ0eXBlXCI7XG4gICAgU2VtYW50aWNUb2tlblR5cGVzW1wiY2xhc3NcIl0gPSBcImNsYXNzXCI7XG4gICAgU2VtYW50aWNUb2tlblR5cGVzW1wiZW51bVwiXSA9IFwiZW51bVwiO1xuICAgIFNlbWFudGljVG9rZW5UeXBlc1tcImludGVyZmFjZVwiXSA9IFwiaW50ZXJmYWNlXCI7XG4gICAgU2VtYW50aWNUb2tlblR5cGVzW1wic3RydWN0XCJdID0gXCJzdHJ1Y3RcIjtcbiAgICBTZW1hbnRpY1Rva2VuVHlwZXNbXCJ0eXBlUGFyYW1ldGVyXCJdID0gXCJ0eXBlUGFyYW1ldGVyXCI7XG4gICAgU2VtYW50aWNUb2tlblR5cGVzW1wicGFyYW1ldGVyXCJdID0gXCJwYXJhbWV0ZXJcIjtcbiAgICBTZW1hbnRpY1Rva2VuVHlwZXNbXCJ2YXJpYWJsZVwiXSA9IFwidmFyaWFibGVcIjtcbiAgICBTZW1hbnRpY1Rva2VuVHlwZXNbXCJwcm9wZXJ0eVwiXSA9IFwicHJvcGVydHlcIjtcbiAgICBTZW1hbnRpY1Rva2VuVHlwZXNbXCJlbnVtTWVtYmVyXCJdID0gXCJlbnVtTWVtYmVyXCI7XG4gICAgU2VtYW50aWNUb2tlblR5cGVzW1wiZXZlbnRcIl0gPSBcImV2ZW50XCI7XG4gICAgU2VtYW50aWNUb2tlblR5cGVzW1wiZnVuY3Rpb25cIl0gPSBcImZ1bmN0aW9uXCI7XG4gICAgU2VtYW50aWNUb2tlblR5cGVzW1wibWV0aG9kXCJdID0gXCJtZXRob2RcIjtcbiAgICBTZW1hbnRpY1Rva2VuVHlwZXNbXCJtYWNyb1wiXSA9IFwibWFjcm9cIjtcbiAgICBTZW1hbnRpY1Rva2VuVHlwZXNbXCJrZXl3b3JkXCJdID0gXCJrZXl3b3JkXCI7XG4gICAgU2VtYW50aWNUb2tlblR5cGVzW1wibW9kaWZpZXJcIl0gPSBcIm1vZGlmaWVyXCI7XG4gICAgU2VtYW50aWNUb2tlblR5cGVzW1wiY29tbWVudFwiXSA9IFwiY29tbWVudFwiO1xuICAgIFNlbWFudGljVG9rZW5UeXBlc1tcInN0cmluZ1wiXSA9IFwic3RyaW5nXCI7XG4gICAgU2VtYW50aWNUb2tlblR5cGVzW1wibnVtYmVyXCJdID0gXCJudW1iZXJcIjtcbiAgICBTZW1hbnRpY1Rva2VuVHlwZXNbXCJyZWdleHBcIl0gPSBcInJlZ2V4cFwiO1xuICAgIFNlbWFudGljVG9rZW5UeXBlc1tcIm9wZXJhdG9yXCJdID0gXCJvcGVyYXRvclwiO1xuICAgIC8qKlxuICAgICAqIEBzaW5jZSAzLjE3LjBcbiAgICAgKi9cbiAgICBTZW1hbnRpY1Rva2VuVHlwZXNbXCJkZWNvcmF0b3JcIl0gPSBcImRlY29yYXRvclwiO1xuICAgIC8qKlxuICAgICAqIEBzaW5jZSAzLjE4LjBcbiAgICAgKi9cbiAgICBTZW1hbnRpY1Rva2VuVHlwZXNbXCJsYWJlbFwiXSA9IFwibGFiZWxcIjtcbn0pKFNlbWFudGljVG9rZW5UeXBlcyB8fCAoU2VtYW50aWNUb2tlblR5cGVzID0ge30pKTtcbi8qKlxuICogQSBzZXQgb2YgcHJlZGVmaW5lZCB0b2tlbiBtb2RpZmllcnMuIFRoaXMgc2V0IGlzIG5vdCBmaXhlZFxuICogYW4gY2xpZW50cyBjYW4gc3BlY2lmeSBhZGRpdGlvbmFsIHRva2VuIHR5cGVzIHZpYSB0aGVcbiAqIGNvcnJlc3BvbmRpbmcgY2xpZW50IGNhcGFiaWxpdGllcy5cbiAqXG4gKiBAc2luY2UgMy4xNi4wXG4gKi9cbmV4cG9ydCB2YXIgU2VtYW50aWNUb2tlbk1vZGlmaWVycztcbihmdW5jdGlvbiAoU2VtYW50aWNUb2tlbk1vZGlmaWVycykge1xuICAgIFNlbWFudGljVG9rZW5Nb2RpZmllcnNbXCJkZWNsYXJhdGlvblwiXSA9IFwiZGVjbGFyYXRpb25cIjtcbiAgICBTZW1hbnRpY1Rva2VuTW9kaWZpZXJzW1wiZGVmaW5pdGlvblwiXSA9IFwiZGVmaW5pdGlvblwiO1xuICAgIFNlbWFudGljVG9rZW5Nb2RpZmllcnNbXCJyZWFkb25seVwiXSA9IFwicmVhZG9ubHlcIjtcbiAgICBTZW1hbnRpY1Rva2VuTW9kaWZpZXJzW1wic3RhdGljXCJdID0gXCJzdGF0aWNcIjtcbiAgICBTZW1hbnRpY1Rva2VuTW9kaWZpZXJzW1wiZGVwcmVjYXRlZFwiXSA9IFwiZGVwcmVjYXRlZFwiO1xuICAgIFNlbWFudGljVG9rZW5Nb2RpZmllcnNbXCJhYnN0cmFjdFwiXSA9IFwiYWJzdHJhY3RcIjtcbiAgICBTZW1hbnRpY1Rva2VuTW9kaWZpZXJzW1wiYXN5bmNcIl0gPSBcImFzeW5jXCI7XG4gICAgU2VtYW50aWNUb2tlbk1vZGlmaWVyc1tcIm1vZGlmaWNhdGlvblwiXSA9IFwibW9kaWZpY2F0aW9uXCI7XG4gICAgU2VtYW50aWNUb2tlbk1vZGlmaWVyc1tcImRvY3VtZW50YXRpb25cIl0gPSBcImRvY3VtZW50YXRpb25cIjtcbiAgICBTZW1hbnRpY1Rva2VuTW9kaWZpZXJzW1wiZGVmYXVsdExpYnJhcnlcIl0gPSBcImRlZmF1bHRMaWJyYXJ5XCI7XG59KShTZW1hbnRpY1Rva2VuTW9kaWZpZXJzIHx8IChTZW1hbnRpY1Rva2VuTW9kaWZpZXJzID0ge30pKTtcbi8qKlxuICogQHNpbmNlIDMuMTYuMFxuICovXG5leHBvcnQgdmFyIFNlbWFudGljVG9rZW5zO1xuKGZ1bmN0aW9uIChTZW1hbnRpY1Rva2Vucykge1xuICAgIGZ1bmN0aW9uIGlzKHZhbHVlKSB7XG4gICAgICAgIGNvbnN0IGNhbmRpZGF0ZSA9IHZhbHVlO1xuICAgICAgICByZXR1cm4gSXMub2JqZWN0TGl0ZXJhbChjYW5kaWRhdGUpICYmIChjYW5kaWRhdGUucmVzdWx0SWQgPT09IHVuZGVmaW5lZCB8fCB0eXBlb2YgY2FuZGlkYXRlLnJlc3VsdElkID09PSAnc3RyaW5nJykgJiZcbiAgICAgICAgICAgIEFycmF5LmlzQXJyYXkoY2FuZGlkYXRlLmRhdGEpICYmIChjYW5kaWRhdGUuZGF0YS5sZW5ndGggPT09IDAgfHwgdHlwZW9mIGNhbmRpZGF0ZS5kYXRhWzBdID09PSAnbnVtYmVyJyk7XG4gICAgfVxuICAgIFNlbWFudGljVG9rZW5zLmlzID0gaXM7XG59KShTZW1hbnRpY1Rva2VucyB8fCAoU2VtYW50aWNUb2tlbnMgPSB7fSkpO1xuLyoqXG4gKiBUaGUgSW5saW5lVmFsdWVUZXh0IG5hbWVzcGFjZSBwcm92aWRlcyBmdW5jdGlvbnMgdG8gZGVhbCB3aXRoIElubGluZVZhbHVlVGV4dHMuXG4gKlxuICogQHNpbmNlIDMuMTcuMFxuICovXG5leHBvcnQgdmFyIElubGluZVZhbHVlVGV4dDtcbihmdW5jdGlvbiAoSW5saW5lVmFsdWVUZXh0KSB7XG4gICAgLyoqXG4gICAgICogQ3JlYXRlcyBhIG5ldyBJbmxpbmVWYWx1ZVRleHQgbGl0ZXJhbC5cbiAgICAgKi9cbiAgICBmdW5jdGlvbiBjcmVhdGUocmFuZ2UsIHRleHQpIHtcbiAgICAgICAgcmV0dXJuIHsgcmFuZ2UsIHRleHQgfTtcbiAgICB9XG4gICAgSW5saW5lVmFsdWVUZXh0LmNyZWF0ZSA9IGNyZWF0ZTtcbiAgICBmdW5jdGlvbiBpcyh2YWx1ZSkge1xuICAgICAgICBjb25zdCBjYW5kaWRhdGUgPSB2YWx1ZTtcbiAgICAgICAgcmV0dXJuIGNhbmRpZGF0ZSAhPT0gdW5kZWZpbmVkICYmIGNhbmRpZGF0ZSAhPT0gbnVsbCAmJiBSYW5nZS5pcyhjYW5kaWRhdGUucmFuZ2UpICYmIElzLnN0cmluZyhjYW5kaWRhdGUudGV4dCk7XG4gICAgfVxuICAgIElubGluZVZhbHVlVGV4dC5pcyA9IGlzO1xufSkoSW5saW5lVmFsdWVUZXh0IHx8IChJbmxpbmVWYWx1ZVRleHQgPSB7fSkpO1xuLyoqXG4gKiBUaGUgSW5saW5lVmFsdWVWYXJpYWJsZUxvb2t1cCBuYW1lc3BhY2UgcHJvdmlkZXMgZnVuY3Rpb25zIHRvXG4gKiBkZWFsIHdpdGggSW5saW5lVmFsdWVWYXJpYWJsZUxvb2t1cHMuXG4gKlxuICogQHNpbmNlIDMuMTcuMFxuICovXG5leHBvcnQgdmFyIElubGluZVZhbHVlVmFyaWFibGVMb29rdXA7XG4oZnVuY3Rpb24gKElubGluZVZhbHVlVmFyaWFibGVMb29rdXApIHtcbiAgICAvKipcbiAgICAgKiBDcmVhdGVzIGEgbmV3IElubGluZVZhbHVlVGV4dCBsaXRlcmFsLlxuICAgICAqL1xuICAgIGZ1bmN0aW9uIGNyZWF0ZShyYW5nZSwgdmFyaWFibGVOYW1lLCBjYXNlU2Vuc2l0aXZlTG9va3VwKSB7XG4gICAgICAgIHJldHVybiB7IHJhbmdlLCB2YXJpYWJsZU5hbWUsIGNhc2VTZW5zaXRpdmVMb29rdXAgfTtcbiAgICB9XG4gICAgSW5saW5lVmFsdWVWYXJpYWJsZUxvb2t1cC5jcmVhdGUgPSBjcmVhdGU7XG4gICAgZnVuY3Rpb24gaXModmFsdWUpIHtcbiAgICAgICAgY29uc3QgY2FuZGlkYXRlID0gdmFsdWU7XG4gICAgICAgIHJldHVybiBjYW5kaWRhdGUgIT09IHVuZGVmaW5lZCAmJiBjYW5kaWRhdGUgIT09IG51bGwgJiYgUmFuZ2UuaXMoY2FuZGlkYXRlLnJhbmdlKSAmJiBJcy5ib29sZWFuKGNhbmRpZGF0ZS5jYXNlU2Vuc2l0aXZlTG9va3VwKVxuICAgICAgICAgICAgJiYgKElzLnN0cmluZyhjYW5kaWRhdGUudmFyaWFibGVOYW1lKSB8fCBjYW5kaWRhdGUudmFyaWFibGVOYW1lID09PSB1bmRlZmluZWQpO1xuICAgIH1cbiAgICBJbmxpbmVWYWx1ZVZhcmlhYmxlTG9va3VwLmlzID0gaXM7XG59KShJbmxpbmVWYWx1ZVZhcmlhYmxlTG9va3VwIHx8IChJbmxpbmVWYWx1ZVZhcmlhYmxlTG9va3VwID0ge30pKTtcbi8qKlxuICogVGhlIElubGluZVZhbHVlRXZhbHVhdGFibGVFeHByZXNzaW9uIG5hbWVzcGFjZSBwcm92aWRlcyBmdW5jdGlvbnMgdG8gZGVhbCB3aXRoIElubGluZVZhbHVlRXZhbHVhdGFibGVFeHByZXNzaW9uLlxuICpcbiAqIEBzaW5jZSAzLjE3LjBcbiAqL1xuZXhwb3J0IHZhciBJbmxpbmVWYWx1ZUV2YWx1YXRhYmxlRXhwcmVzc2lvbjtcbihmdW5jdGlvbiAoSW5saW5lVmFsdWVFdmFsdWF0YWJsZUV4cHJlc3Npb24pIHtcbiAgICAvKipcbiAgICAgKiBDcmVhdGVzIGEgbmV3IElubGluZVZhbHVlRXZhbHVhdGFibGVFeHByZXNzaW9uIGxpdGVyYWwuXG4gICAgICovXG4gICAgZnVuY3Rpb24gY3JlYXRlKHJhbmdlLCBleHByZXNzaW9uKSB7XG4gICAgICAgIHJldHVybiB7IHJhbmdlLCBleHByZXNzaW9uIH07XG4gICAgfVxuICAgIElubGluZVZhbHVlRXZhbHVhdGFibGVFeHByZXNzaW9uLmNyZWF0ZSA9IGNyZWF0ZTtcbiAgICBmdW5jdGlvbiBpcyh2YWx1ZSkge1xuICAgICAgICBjb25zdCBjYW5kaWRhdGUgPSB2YWx1ZTtcbiAgICAgICAgcmV0dXJuIGNhbmRpZGF0ZSAhPT0gdW5kZWZpbmVkICYmIGNhbmRpZGF0ZSAhPT0gbnVsbCAmJiBSYW5nZS5pcyhjYW5kaWRhdGUucmFuZ2UpXG4gICAgICAgICAgICAmJiAoSXMuc3RyaW5nKGNhbmRpZGF0ZS5leHByZXNzaW9uKSB8fCBjYW5kaWRhdGUuZXhwcmVzc2lvbiA9PT0gdW5kZWZpbmVkKTtcbiAgICB9XG4gICAgSW5saW5lVmFsdWVFdmFsdWF0YWJsZUV4cHJlc3Npb24uaXMgPSBpcztcbn0pKElubGluZVZhbHVlRXZhbHVhdGFibGVFeHByZXNzaW9uIHx8IChJbmxpbmVWYWx1ZUV2YWx1YXRhYmxlRXhwcmVzc2lvbiA9IHt9KSk7XG4vKipcbiAqIFRoZSBJbmxpbmVWYWx1ZUNvbnRleHQgbmFtZXNwYWNlIHByb3ZpZGVzIGhlbHBlciBmdW5jdGlvbnMgdG8gd29yayB3aXRoXG4gKiB7QGxpbmsgSW5saW5lVmFsdWVDb250ZXh0fSBsaXRlcmFscy5cbiAqXG4gKiBAc2luY2UgMy4xNy4wXG4gKi9cbmV4cG9ydCB2YXIgSW5saW5lVmFsdWVDb250ZXh0O1xuKGZ1bmN0aW9uIChJbmxpbmVWYWx1ZUNvbnRleHQpIHtcbiAgICAvKipcbiAgICAgKiBDcmVhdGVzIGEgbmV3IElubGluZVZhbHVlQ29udGV4dCBsaXRlcmFsLlxuICAgICAqL1xuICAgIGZ1bmN0aW9uIGNyZWF0ZShmcmFtZUlkLCBzdG9wcGVkTG9jYXRpb24pIHtcbiAgICAgICAgcmV0dXJuIHsgZnJhbWVJZCwgc3RvcHBlZExvY2F0aW9uIH07XG4gICAgfVxuICAgIElubGluZVZhbHVlQ29udGV4dC5jcmVhdGUgPSBjcmVhdGU7XG4gICAgLyoqXG4gICAgICogQ2hlY2tzIHdoZXRoZXIgdGhlIGdpdmVuIGxpdGVyYWwgY29uZm9ybXMgdG8gdGhlIHtAbGluayBJbmxpbmVWYWx1ZUNvbnRleHR9IGludGVyZmFjZS5cbiAgICAgKi9cbiAgICBmdW5jdGlvbiBpcyh2YWx1ZSkge1xuICAgICAgICBjb25zdCBjYW5kaWRhdGUgPSB2YWx1ZTtcbiAgICAgICAgcmV0dXJuIElzLmRlZmluZWQoY2FuZGlkYXRlKSAmJiBSYW5nZS5pcyh2YWx1ZS5zdG9wcGVkTG9jYXRpb24pO1xuICAgIH1cbiAgICBJbmxpbmVWYWx1ZUNvbnRleHQuaXMgPSBpcztcbn0pKElubGluZVZhbHVlQ29udGV4dCB8fCAoSW5saW5lVmFsdWVDb250ZXh0ID0ge30pKTtcbi8qKlxuICogSW5sYXkgaGludCBraW5kcy5cbiAqXG4gKiBAc2luY2UgMy4xNy4wXG4gKi9cbmV4cG9ydCB2YXIgSW5sYXlIaW50S2luZDtcbihmdW5jdGlvbiAoSW5sYXlIaW50S2luZCkge1xuICAgIC8qKlxuICAgICAqIEFuIGlubGF5IGhpbnQgdGhhdCBmb3IgYSB0eXBlIGFubm90YXRpb24uXG4gICAgICovXG4gICAgSW5sYXlIaW50S2luZC5UeXBlID0gMTtcbiAgICAvKipcbiAgICAgKiBBbiBpbmxheSBoaW50IHRoYXQgaXMgZm9yIGEgcGFyYW1ldGVyLlxuICAgICAqL1xuICAgIElubGF5SGludEtpbmQuUGFyYW1ldGVyID0gMjtcbiAgICBmdW5jdGlvbiBpcyh2YWx1ZSkge1xuICAgICAgICByZXR1cm4gdmFsdWUgPT09IDEgfHwgdmFsdWUgPT09IDI7XG4gICAgfVxuICAgIElubGF5SGludEtpbmQuaXMgPSBpcztcbn0pKElubGF5SGludEtpbmQgfHwgKElubGF5SGludEtpbmQgPSB7fSkpO1xuZXhwb3J0IHZhciBJbmxheUhpbnRMYWJlbFBhcnQ7XG4oZnVuY3Rpb24gKElubGF5SGludExhYmVsUGFydCkge1xuICAgIGZ1bmN0aW9uIGNyZWF0ZSh2YWx1ZSkge1xuICAgICAgICByZXR1cm4geyB2YWx1ZSB9O1xuICAgIH1cbiAgICBJbmxheUhpbnRMYWJlbFBhcnQuY3JlYXRlID0gY3JlYXRlO1xuICAgIGZ1bmN0aW9uIGlzKHZhbHVlKSB7XG4gICAgICAgIGNvbnN0IGNhbmRpZGF0ZSA9IHZhbHVlO1xuICAgICAgICByZXR1cm4gSXMub2JqZWN0TGl0ZXJhbChjYW5kaWRhdGUpXG4gICAgICAgICAgICAmJiAoY2FuZGlkYXRlLnRvb2x0aXAgPT09IHVuZGVmaW5lZCB8fCBJcy5zdHJpbmcoY2FuZGlkYXRlLnRvb2x0aXApIHx8IE1hcmt1cENvbnRlbnQuaXMoY2FuZGlkYXRlLnRvb2x0aXApKVxuICAgICAgICAgICAgJiYgKGNhbmRpZGF0ZS5sb2NhdGlvbiA9PT0gdW5kZWZpbmVkIHx8IExvY2F0aW9uLmlzKGNhbmRpZGF0ZS5sb2NhdGlvbikpXG4gICAgICAgICAgICAmJiAoY2FuZGlkYXRlLmNvbW1hbmQgPT09IHVuZGVmaW5lZCB8fCBDb21tYW5kLmlzKGNhbmRpZGF0ZS5jb21tYW5kKSk7XG4gICAgfVxuICAgIElubGF5SGludExhYmVsUGFydC5pcyA9IGlzO1xufSkoSW5sYXlIaW50TGFiZWxQYXJ0IHx8IChJbmxheUhpbnRMYWJlbFBhcnQgPSB7fSkpO1xuZXhwb3J0IHZhciBJbmxheUhpbnQ7XG4oZnVuY3Rpb24gKElubGF5SGludCkge1xuICAgIGZ1bmN0aW9uIGNyZWF0ZShwb3NpdGlvbiwgbGFiZWwsIGtpbmQpIHtcbiAgICAgICAgY29uc3QgcmVzdWx0ID0geyBwb3NpdGlvbiwgbGFiZWwgfTtcbiAgICAgICAgaWYgKGtpbmQgIT09IHVuZGVmaW5lZCkge1xuICAgICAgICAgICAgcmVzdWx0LmtpbmQgPSBraW5kO1xuICAgICAgICB9XG4gICAgICAgIHJldHVybiByZXN1bHQ7XG4gICAgfVxuICAgIElubGF5SGludC5jcmVhdGUgPSBjcmVhdGU7XG4gICAgZnVuY3Rpb24gaXModmFsdWUpIHtcbiAgICAgICAgY29uc3QgY2FuZGlkYXRlID0gdmFsdWU7XG4gICAgICAgIHJldHVybiBJcy5vYmplY3RMaXRlcmFsKGNhbmRpZGF0ZSkgJiYgUG9zaXRpb24uaXMoY2FuZGlkYXRlLnBvc2l0aW9uKVxuICAgICAgICAgICAgJiYgKElzLnN0cmluZyhjYW5kaWRhdGUubGFiZWwpIHx8IElzLnR5cGVkQXJyYXkoY2FuZGlkYXRlLmxhYmVsLCBJbmxheUhpbnRMYWJlbFBhcnQuaXMpKVxuICAgICAgICAgICAgJiYgKGNhbmRpZGF0ZS5raW5kID09PSB1bmRlZmluZWQgfHwgSW5sYXlIaW50S2luZC5pcyhjYW5kaWRhdGUua2luZCkpXG4gICAgICAgICAgICAmJiAoY2FuZGlkYXRlLnRleHRFZGl0cyA9PT0gdW5kZWZpbmVkKSB8fCBJcy50eXBlZEFycmF5KGNhbmRpZGF0ZS50ZXh0RWRpdHMsIFRleHRFZGl0LmlzKVxuICAgICAgICAgICAgJiYgKGNhbmRpZGF0ZS50b29sdGlwID09PSB1bmRlZmluZWQgfHwgSXMuc3RyaW5nKGNhbmRpZGF0ZS50b29sdGlwKSB8fCBNYXJrdXBDb250ZW50LmlzKGNhbmRpZGF0ZS50b29sdGlwKSlcbiAgICAgICAgICAgICYmIChjYW5kaWRhdGUucGFkZGluZ0xlZnQgPT09IHVuZGVmaW5lZCB8fCBJcy5ib29sZWFuKGNhbmRpZGF0ZS5wYWRkaW5nTGVmdCkpXG4gICAgICAgICAgICAmJiAoY2FuZGlkYXRlLnBhZGRpbmdSaWdodCA9PT0gdW5kZWZpbmVkIHx8IElzLmJvb2xlYW4oY2FuZGlkYXRlLnBhZGRpbmdSaWdodCkpO1xuICAgIH1cbiAgICBJbmxheUhpbnQuaXMgPSBpcztcbn0pKElubGF5SGludCB8fCAoSW5sYXlIaW50ID0ge30pKTtcbmV4cG9ydCB2YXIgU3RyaW5nVmFsdWU7XG4oZnVuY3Rpb24gKFN0cmluZ1ZhbHVlKSB7XG4gICAgZnVuY3Rpb24gY3JlYXRlU25pcHBldCh2YWx1ZSkge1xuICAgICAgICByZXR1cm4geyBraW5kOiAnc25pcHBldCcsIHZhbHVlIH07XG4gICAgfVxuICAgIFN0cmluZ1ZhbHVlLmNyZWF0ZVNuaXBwZXQgPSBjcmVhdGVTbmlwcGV0O1xuICAgIGZ1bmN0aW9uIGlzU25pcHBldCh2YWx1ZSkge1xuICAgICAgICBjb25zdCBjYW5kaWRhdGUgPSB2YWx1ZTtcbiAgICAgICAgcmV0dXJuIElzLm9iamVjdExpdGVyYWwoY2FuZGlkYXRlKVxuICAgICAgICAgICAgJiYgY2FuZGlkYXRlLmtpbmQgPT09ICdzbmlwcGV0J1xuICAgICAgICAgICAgJiYgSXMuc3RyaW5nKGNhbmRpZGF0ZS52YWx1ZSk7XG4gICAgfVxuICAgIFN0cmluZ1ZhbHVlLmlzU25pcHBldCA9IGlzU25pcHBldDtcbn0pKFN0cmluZ1ZhbHVlIHx8IChTdHJpbmdWYWx1ZSA9IHt9KSk7XG5leHBvcnQgdmFyIElubGluZUNvbXBsZXRpb25JdGVtO1xuKGZ1bmN0aW9uIChJbmxpbmVDb21wbGV0aW9uSXRlbSkge1xuICAgIGZ1bmN0aW9uIGNyZWF0ZShpbnNlcnRUZXh0LCBmaWx0ZXJUZXh0LCByYW5nZSwgY29tbWFuZCkge1xuICAgICAgICByZXR1cm4geyBpbnNlcnRUZXh0LCBmaWx0ZXJUZXh0LCByYW5nZSwgY29tbWFuZCB9O1xuICAgIH1cbiAgICBJbmxpbmVDb21wbGV0aW9uSXRlbS5jcmVhdGUgPSBjcmVhdGU7XG59KShJbmxpbmVDb21wbGV0aW9uSXRlbSB8fCAoSW5saW5lQ29tcGxldGlvbkl0ZW0gPSB7fSkpO1xuZXhwb3J0IHZhciBJbmxpbmVDb21wbGV0aW9uTGlzdDtcbihmdW5jdGlvbiAoSW5saW5lQ29tcGxldGlvbkxpc3QpIHtcbiAgICBmdW5jdGlvbiBjcmVhdGUoaXRlbXMpIHtcbiAgICAgICAgcmV0dXJuIHsgaXRlbXMgfTtcbiAgICB9XG4gICAgSW5saW5lQ29tcGxldGlvbkxpc3QuY3JlYXRlID0gY3JlYXRlO1xufSkoSW5saW5lQ29tcGxldGlvbkxpc3QgfHwgKElubGluZUNvbXBsZXRpb25MaXN0ID0ge30pKTtcbi8qKlxuICogRGVzY3JpYmVzIGhvdyBhbiB7QGxpbmsgSW5saW5lQ29tcGxldGlvbkl0ZW1Qcm92aWRlciBpbmxpbmUgY29tcGxldGlvbiBwcm92aWRlcn0gd2FzIHRyaWdnZXJlZC5cbiAqXG4gKiBAc2luY2UgMy4xOC4wXG4gKi9cbmV4cG9ydCB2YXIgSW5saW5lQ29tcGxldGlvblRyaWdnZXJLaW5kO1xuKGZ1bmN0aW9uIChJbmxpbmVDb21wbGV0aW9uVHJpZ2dlcktpbmQpIHtcbiAgICAvKipcbiAgICAgKiBDb21wbGV0aW9uIHdhcyB0cmlnZ2VyZWQgZXhwbGljaXRseSBieSBhIHVzZXIgZ2VzdHVyZS5cbiAgICAgKi9cbiAgICBJbmxpbmVDb21wbGV0aW9uVHJpZ2dlcktpbmQuSW52b2tlZCA9IDE7XG4gICAgLyoqXG4gICAgICogQ29tcGxldGlvbiB3YXMgdHJpZ2dlcmVkIGF1dG9tYXRpY2FsbHkgd2hpbGUgZWRpdGluZy5cbiAgICAgKi9cbiAgICBJbmxpbmVDb21wbGV0aW9uVHJpZ2dlcktpbmQuQXV0b21hdGljID0gMjtcbn0pKElubGluZUNvbXBsZXRpb25UcmlnZ2VyS2luZCB8fCAoSW5saW5lQ29tcGxldGlvblRyaWdnZXJLaW5kID0ge30pKTtcbmV4cG9ydCB2YXIgU2VsZWN0ZWRDb21wbGV0aW9uSW5mbztcbihmdW5jdGlvbiAoU2VsZWN0ZWRDb21wbGV0aW9uSW5mbykge1xuICAgIGZ1bmN0aW9uIGNyZWF0ZShyYW5nZSwgdGV4dCkge1xuICAgICAgICByZXR1cm4geyByYW5nZSwgdGV4dCB9O1xuICAgIH1cbiAgICBTZWxlY3RlZENvbXBsZXRpb25JbmZvLmNyZWF0ZSA9IGNyZWF0ZTtcbn0pKFNlbGVjdGVkQ29tcGxldGlvbkluZm8gfHwgKFNlbGVjdGVkQ29tcGxldGlvbkluZm8gPSB7fSkpO1xuZXhwb3J0IHZhciBJbmxpbmVDb21wbGV0aW9uQ29udGV4dDtcbihmdW5jdGlvbiAoSW5saW5lQ29tcGxldGlvbkNvbnRleHQpIHtcbiAgICBmdW5jdGlvbiBjcmVhdGUodHJpZ2dlcktpbmQsIHNlbGVjdGVkQ29tcGxldGlvbkluZm8pIHtcbiAgICAgICAgcmV0dXJuIHsgdHJpZ2dlcktpbmQsIHNlbGVjdGVkQ29tcGxldGlvbkluZm8gfTtcbiAgICB9XG4gICAgSW5saW5lQ29tcGxldGlvbkNvbnRleHQuY3JlYXRlID0gY3JlYXRlO1xufSkoSW5saW5lQ29tcGxldGlvbkNvbnRleHQgfHwgKElubGluZUNvbXBsZXRpb25Db250ZXh0ID0ge30pKTtcbmV4cG9ydCB2YXIgV29ya3NwYWNlRm9sZGVyO1xuKGZ1bmN0aW9uIChXb3Jrc3BhY2VGb2xkZXIpIHtcbiAgICBmdW5jdGlvbiBpcyh2YWx1ZSkge1xuICAgICAgICBjb25zdCBjYW5kaWRhdGUgPSB2YWx1ZTtcbiAgICAgICAgcmV0dXJuIElzLm9iamVjdExpdGVyYWwoY2FuZGlkYXRlKSAmJiBVUkkuaXMoY2FuZGlkYXRlLnVyaSkgJiYgSXMuc3RyaW5nKGNhbmRpZGF0ZS5uYW1lKTtcbiAgICB9XG4gICAgV29ya3NwYWNlRm9sZGVyLmlzID0gaXM7XG59KShXb3Jrc3BhY2VGb2xkZXIgfHwgKFdvcmtzcGFjZUZvbGRlciA9IHt9KSk7XG5leHBvcnQgY29uc3QgRU9MID0gWydcXG4nLCAnXFxyXFxuJywgJ1xcciddO1xuLyoqXG4gKiBAZGVwcmVjYXRlZCBVc2UgdGhlIHRleHQgZG9jdW1lbnQgZnJvbSB0aGUgbmV3IHZzY29kZS1sYW5ndWFnZXNlcnZlci10ZXh0ZG9jdW1lbnQgcGFja2FnZS5cbiAqL1xuZXhwb3J0IHZhciBUZXh0RG9jdW1lbnQ7XG4oZnVuY3Rpb24gKFRleHREb2N1bWVudCkge1xuICAgIC8qKlxuICAgICAqIENyZWF0ZXMgYSBuZXcgSVRleHREb2N1bWVudCBsaXRlcmFsIGZyb20gdGhlIGdpdmVuIHVyaSBhbmQgY29udGVudC5cbiAgICAgKiBAcGFyYW0gdXJpIFRoZSBkb2N1bWVudCdzIHVyaS5cbiAgICAgKiBAcGFyYW0gbGFuZ3VhZ2VJZCBUaGUgZG9jdW1lbnQncyBsYW5ndWFnZSBJZC5cbiAgICAgKiBAcGFyYW0gdmVyc2lvbiBUaGUgZG9jdW1lbnQncyB2ZXJzaW9uLlxuICAgICAqIEBwYXJhbSBjb250ZW50IFRoZSBkb2N1bWVudCdzIGNvbnRlbnQuXG4gICAgICovXG4gICAgZnVuY3Rpb24gY3JlYXRlKHVyaSwgbGFuZ3VhZ2VJZCwgdmVyc2lvbiwgY29udGVudCkge1xuICAgICAgICByZXR1cm4gbmV3IEZ1bGxUZXh0RG9jdW1lbnQodXJpLCBsYW5ndWFnZUlkLCB2ZXJzaW9uLCBjb250ZW50KTtcbiAgICB9XG4gICAgVGV4dERvY3VtZW50LmNyZWF0ZSA9IGNyZWF0ZTtcbiAgICAvKipcbiAgICAgKiBDaGVja3Mgd2hldGhlciB0aGUgZ2l2ZW4gbGl0ZXJhbCBjb25mb3JtcyB0byB0aGUge0BsaW5rIElUZXh0RG9jdW1lbnR9IGludGVyZmFjZS5cbiAgICAgKi9cbiAgICBmdW5jdGlvbiBpcyh2YWx1ZSkge1xuICAgICAgICBjb25zdCBjYW5kaWRhdGUgPSB2YWx1ZTtcbiAgICAgICAgcmV0dXJuIElzLmRlZmluZWQoY2FuZGlkYXRlKSAmJiBJcy5zdHJpbmcoY2FuZGlkYXRlLnVyaSkgJiYgKElzLnVuZGVmaW5lZChjYW5kaWRhdGUubGFuZ3VhZ2VJZCkgfHwgSXMuc3RyaW5nKGNhbmRpZGF0ZS5sYW5ndWFnZUlkKSkgJiYgSXMudWludGVnZXIoY2FuZGlkYXRlLmxpbmVDb3VudClcbiAgICAgICAgICAgICYmIElzLmZ1bmMoY2FuZGlkYXRlLmdldFRleHQpICYmIElzLmZ1bmMoY2FuZGlkYXRlLnBvc2l0aW9uQXQpICYmIElzLmZ1bmMoY2FuZGlkYXRlLm9mZnNldEF0KSA/IHRydWUgOiBmYWxzZTtcbiAgICB9XG4gICAgVGV4dERvY3VtZW50LmlzID0gaXM7XG4gICAgZnVuY3Rpb24gYXBwbHlFZGl0cyhkb2N1bWVudCwgZWRpdHMpIHtcbiAgICAgICAgbGV0IHRleHQgPSBkb2N1bWVudC5nZXRUZXh0KCk7XG4gICAgICAgIGNvbnN0IHNvcnRlZEVkaXRzID0gbWVyZ2VTb3J0KGVkaXRzLCAoYSwgYikgPT4ge1xuICAgICAgICAgICAgY29uc3QgZGlmZiA9IGEucmFuZ2Uuc3RhcnQubGluZSAtIGIucmFuZ2Uuc3RhcnQubGluZTtcbiAgICAgICAgICAgIGlmIChkaWZmID09PSAwKSB7XG4gICAgICAgICAgICAgICAgcmV0dXJuIGEucmFuZ2Uuc3RhcnQuY2hhcmFjdGVyIC0gYi5yYW5nZS5zdGFydC5jaGFyYWN0ZXI7XG4gICAgICAgICAgICB9XG4gICAgICAgICAgICByZXR1cm4gZGlmZjtcbiAgICAgICAgfSk7XG4gICAgICAgIGxldCBsYXN0TW9kaWZpZWRPZmZzZXQgPSB0ZXh0Lmxlbmd0aDtcbiAgICAgICAgZm9yIChsZXQgaSA9IHNvcnRlZEVkaXRzLmxlbmd0aCAtIDE7IGkgPj0gMDsgaS0tKSB7XG4gICAgICAgICAgICBjb25zdCBlID0gc29ydGVkRWRpdHNbaV07XG4gICAgICAgICAgICBjb25zdCBzdGFydE9mZnNldCA9IGRvY3VtZW50Lm9mZnNldEF0KGUucmFuZ2Uuc3RhcnQpO1xuICAgICAgICAgICAgY29uc3QgZW5kT2Zmc2V0ID0gZG9jdW1lbnQub2Zmc2V0QXQoZS5yYW5nZS5lbmQpO1xuICAgICAgICAgICAgaWYgKGVuZE9mZnNldCA8PSBsYXN0TW9kaWZpZWRPZmZzZXQpIHtcbiAgICAgICAgICAgICAgICB0ZXh0ID0gdGV4dC5zdWJzdHJpbmcoMCwgc3RhcnRPZmZzZXQpICsgZS5uZXdUZXh0ICsgdGV4dC5zdWJzdHJpbmcoZW5kT2Zmc2V0LCB0ZXh0Lmxlbmd0aCk7XG4gICAgICAgICAgICB9XG4gICAgICAgICAgICBlbHNlIHtcbiAgICAgICAgICAgICAgICB0aHJvdyBuZXcgRXJyb3IoJ092ZXJsYXBwaW5nIGVkaXQnKTtcbiAgICAgICAgICAgIH1cbiAgICAgICAgICAgIGxhc3RNb2RpZmllZE9mZnNldCA9IHN0YXJ0T2Zmc2V0O1xuICAgICAgICB9XG4gICAgICAgIHJldHVybiB0ZXh0O1xuICAgIH1cbiAgICBUZXh0RG9jdW1lbnQuYXBwbHlFZGl0cyA9IGFwcGx5RWRpdHM7XG4gICAgZnVuY3Rpb24gbWVyZ2VTb3J0KGRhdGEsIGNvbXBhcmUpIHtcbiAgICAgICAgaWYgKGRhdGEubGVuZ3RoIDw9IDEpIHtcbiAgICAgICAgICAgIC8vIHNvcnRlZFxuICAgICAgICAgICAgcmV0dXJuIGRhdGE7XG4gICAgICAgIH1cbiAgICAgICAgY29uc3QgcCA9IChkYXRhLmxlbmd0aCAvIDIpIHwgMDtcbiAgICAgICAgY29uc3QgbGVmdCA9IGRhdGEuc2xpY2UoMCwgcCk7XG4gICAgICAgIGNvbnN0IHJpZ2h0ID0gZGF0YS5zbGljZShwKTtcbiAgICAgICAgbWVyZ2VTb3J0KGxlZnQsIGNvbXBhcmUpO1xuICAgICAgICBtZXJnZVNvcnQocmlnaHQsIGNvbXBhcmUpO1xuICAgICAgICBsZXQgbGVmdElkeCA9IDA7XG4gICAgICAgIGxldCByaWdodElkeCA9IDA7XG4gICAgICAgIGxldCBpID0gMDtcbiAgICAgICAgd2hpbGUgKGxlZnRJZHggPCBsZWZ0Lmxlbmd0aCAmJiByaWdodElkeCA8IHJpZ2h0Lmxlbmd0aCkge1xuICAgICAgICAgICAgY29uc3QgcmV0ID0gY29tcGFyZShsZWZ0W2xlZnRJZHhdLCByaWdodFtyaWdodElkeF0pO1xuICAgICAgICAgICAgaWYgKHJldCA8PSAwKSB7XG4gICAgICAgICAgICAgICAgLy8gc21hbGxlcl9lcXVhbCAtPiB0YWtlIGxlZnQgdG8gcHJlc2VydmUgb3JkZXJcbiAgICAgICAgICAgICAgICBkYXRhW2krK10gPSBsZWZ0W2xlZnRJZHgrK107XG4gICAgICAgICAgICB9XG4gICAgICAgICAgICBlbHNlIHtcbiAgICAgICAgICAgICAgICAvLyBncmVhdGVyIC0+IHRha2UgcmlnaHRcbiAgICAgICAgICAgICAgICBkYXRhW2krK10gPSByaWdodFtyaWdodElkeCsrXTtcbiAgICAgICAgICAgIH1cbiAgICAgICAgfVxuICAgICAgICB3aGlsZSAobGVmdElkeCA8IGxlZnQubGVuZ3RoKSB7XG4gICAgICAgICAgICBkYXRhW2krK10gPSBsZWZ0W2xlZnRJZHgrK107XG4gICAgICAgIH1cbiAgICAgICAgd2hpbGUgKHJpZ2h0SWR4IDwgcmlnaHQubGVuZ3RoKSB7XG4gICAgICAgICAgICBkYXRhW2krK10gPSByaWdodFtyaWdodElkeCsrXTtcbiAgICAgICAgfVxuICAgICAgICByZXR1cm4gZGF0YTtcbiAgICB9XG59KShUZXh0RG9jdW1lbnQgfHwgKFRleHREb2N1bWVudCA9IHt9KSk7XG4vKipcbiAqIEBkZXByZWNhdGVkIFVzZSB0aGUgdGV4dCBkb2N1bWVudCBmcm9tIHRoZSBuZXcgdnNjb2RlLWxhbmd1YWdlc2VydmVyLXRleHRkb2N1bWVudCBwYWNrYWdlLlxuICovXG5jbGFzcyBGdWxsVGV4dERvY3VtZW50IHtcbiAgICBjb25zdHJ1Y3Rvcih1cmksIGxhbmd1YWdlSWQsIHZlcnNpb24sIGNvbnRlbnQpIHtcbiAgICAgICAgdGhpcy5fdXJpID0gdXJpO1xuICAgICAgICB0aGlzLl9sYW5ndWFnZUlkID0gbGFuZ3VhZ2VJZDtcbiAgICAgICAgdGhpcy5fdmVyc2lvbiA9IHZlcnNpb247XG4gICAgICAgIHRoaXMuX2NvbnRlbnQgPSBjb250ZW50O1xuICAgICAgICB0aGlzLl9saW5lT2Zmc2V0cyA9IHVuZGVmaW5lZDtcbiAgICB9XG4gICAgZ2V0IHVyaSgpIHtcbiAgICAgICAgcmV0dXJuIHRoaXMuX3VyaTtcbiAgICB9XG4gICAgZ2V0IGxhbmd1YWdlSWQoKSB7XG4gICAgICAgIHJldHVybiB0aGlzLl9sYW5ndWFnZUlkO1xuICAgIH1cbiAgICBnZXQgdmVyc2lvbigpIHtcbiAgICAgICAgcmV0dXJuIHRoaXMuX3ZlcnNpb247XG4gICAgfVxuICAgIGdldFRleHQocmFuZ2UpIHtcbiAgICAgICAgaWYgKHJhbmdlKSB7XG4gICAgICAgICAgICBjb25zdCBzdGFydCA9IHRoaXMub2Zmc2V0QXQocmFuZ2Uuc3RhcnQpO1xuICAgICAgICAgICAgY29uc3QgZW5kID0gdGhpcy5vZmZzZXRBdChyYW5nZS5lbmQpO1xuICAgICAgICAgICAgcmV0dXJuIHRoaXMuX2NvbnRlbnQuc3Vic3RyaW5nKHN0YXJ0LCBlbmQpO1xuICAgICAgICB9XG4gICAgICAgIHJldHVybiB0aGlzLl9jb250ZW50O1xuICAgIH1cbiAgICB1cGRhdGUoZXZlbnQsIHZlcnNpb24pIHtcbiAgICAgICAgdGhpcy5fY29udGVudCA9IGV2ZW50LnRleHQ7XG4gICAgICAgIHRoaXMuX3ZlcnNpb24gPSB2ZXJzaW9uO1xuICAgICAgICB0aGlzLl9saW5lT2Zmc2V0cyA9IHVuZGVmaW5lZDtcbiAgICB9XG4gICAgZ2V0TGluZU9mZnNldHMoKSB7XG4gICAgICAgIGlmICh0aGlzLl9saW5lT2Zmc2V0cyA9PT0gdW5kZWZpbmVkKSB7XG4gICAgICAgICAgICBjb25zdCBsaW5lT2Zmc2V0cyA9IFtdO1xuICAgICAgICAgICAgY29uc3QgdGV4dCA9IHRoaXMuX2NvbnRlbnQ7XG4gICAgICAgICAgICBsZXQgaXNMaW5lU3RhcnQgPSB0cnVlO1xuICAgICAgICAgICAgZm9yIChsZXQgaSA9IDA7IGkgPCB0ZXh0Lmxlbmd0aDsgaSsrKSB7XG4gICAgICAgICAgICAgICAgaWYgKGlzTGluZVN0YXJ0KSB7XG4gICAgICAgICAgICAgICAgICAgIGxpbmVPZmZzZXRzLnB1c2goaSk7XG4gICAgICAgICAgICAgICAgICAgIGlzTGluZVN0YXJ0ID0gZmFsc2U7XG4gICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgIGNvbnN0IGNoID0gdGV4dC5jaGFyQXQoaSk7XG4gICAgICAgICAgICAgICAgaXNMaW5lU3RhcnQgPSAoY2ggPT09ICdcXHInIHx8IGNoID09PSAnXFxuJyk7XG4gICAgICAgICAgICAgICAgaWYgKGNoID09PSAnXFxyJyAmJiBpICsgMSA8IHRleHQubGVuZ3RoICYmIHRleHQuY2hhckF0KGkgKyAxKSA9PT0gJ1xcbicpIHtcbiAgICAgICAgICAgICAgICAgICAgaSsrO1xuICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgIH1cbiAgICAgICAgICAgIGlmIChpc0xpbmVTdGFydCAmJiB0ZXh0Lmxlbmd0aCA+IDApIHtcbiAgICAgICAgICAgICAgICBsaW5lT2Zmc2V0cy5wdXNoKHRleHQubGVuZ3RoKTtcbiAgICAgICAgICAgIH1cbiAgICAgICAgICAgIHRoaXMuX2xpbmVPZmZzZXRzID0gbGluZU9mZnNldHM7XG4gICAgICAgIH1cbiAgICAgICAgcmV0dXJuIHRoaXMuX2xpbmVPZmZzZXRzO1xuICAgIH1cbiAgICBwb3NpdGlvbkF0KG9mZnNldCkge1xuICAgICAgICBvZmZzZXQgPSBNYXRoLm1heChNYXRoLm1pbihvZmZzZXQsIHRoaXMuX2NvbnRlbnQubGVuZ3RoKSwgMCk7XG4gICAgICAgIGNvbnN0IGxpbmVPZmZzZXRzID0gdGhpcy5nZXRMaW5lT2Zmc2V0cygpO1xuICAgICAgICBsZXQgbG93ID0gMCwgaGlnaCA9IGxpbmVPZmZzZXRzLmxlbmd0aDtcbiAgICAgICAgaWYgKGhpZ2ggPT09IDApIHtcbiAgICAgICAgICAgIHJldHVybiBQb3NpdGlvbi5jcmVhdGUoMCwgb2Zmc2V0KTtcbiAgICAgICAgfVxuICAgICAgICB3aGlsZSAobG93IDwgaGlnaCkge1xuICAgICAgICAgICAgY29uc3QgbWlkID0gTWF0aC5mbG9vcigobG93ICsgaGlnaCkgLyAyKTtcbiAgICAgICAgICAgIGlmIChsaW5lT2Zmc2V0c1ttaWRdID4gb2Zmc2V0KSB7XG4gICAgICAgICAgICAgICAgaGlnaCA9IG1pZDtcbiAgICAgICAgICAgIH1cbiAgICAgICAgICAgIGVsc2Uge1xuICAgICAgICAgICAgICAgIGxvdyA9IG1pZCArIDE7XG4gICAgICAgICAgICB9XG4gICAgICAgIH1cbiAgICAgICAgLy8gbG93IGlzIHRoZSBsZWFzdCB4IGZvciB3aGljaCB0aGUgbGluZSBvZmZzZXQgaXMgbGFyZ2VyIHRoYW4gdGhlIGN1cnJlbnQgb2Zmc2V0XG4gICAgICAgIC8vIG9yIGFycmF5Lmxlbmd0aCBpZiBubyBsaW5lIG9mZnNldCBpcyBsYXJnZXIgdGhhbiB0aGUgY3VycmVudCBvZmZzZXRcbiAgICAgICAgY29uc3QgbGluZSA9IGxvdyAtIDE7XG4gICAgICAgIHJldHVybiBQb3NpdGlvbi5jcmVhdGUobGluZSwgb2Zmc2V0IC0gbGluZU9mZnNldHNbbGluZV0pO1xuICAgIH1cbiAgICBvZmZzZXRBdChwb3NpdGlvbikge1xuICAgICAgICBjb25zdCBsaW5lT2Zmc2V0cyA9IHRoaXMuZ2V0TGluZU9mZnNldHMoKTtcbiAgICAgICAgaWYgKHBvc2l0aW9uLmxpbmUgPj0gbGluZU9mZnNldHMubGVuZ3RoKSB7XG4gICAgICAgICAgICByZXR1cm4gdGhpcy5fY29udGVudC5sZW5ndGg7XG4gICAgICAgIH1cbiAgICAgICAgZWxzZSBpZiAocG9zaXRpb24ubGluZSA8IDApIHtcbiAgICAgICAgICAgIHJldHVybiAwO1xuICAgICAgICB9XG4gICAgICAgIGNvbnN0IGxpbmVPZmZzZXQgPSBsaW5lT2Zmc2V0c1twb3NpdGlvbi5saW5lXTtcbiAgICAgICAgY29uc3QgbmV4dExpbmVPZmZzZXQgPSAocG9zaXRpb24ubGluZSArIDEgPCBsaW5lT2Zmc2V0cy5sZW5ndGgpID8gbGluZU9mZnNldHNbcG9zaXRpb24ubGluZSArIDFdIDogdGhpcy5fY29udGVudC5sZW5ndGg7XG4gICAgICAgIHJldHVybiBNYXRoLm1heChNYXRoLm1pbihsaW5lT2Zmc2V0ICsgcG9zaXRpb24uY2hhcmFjdGVyLCBuZXh0TGluZU9mZnNldCksIGxpbmVPZmZzZXQpO1xuICAgIH1cbiAgICBnZXQgbGluZUNvdW50KCkge1xuICAgICAgICByZXR1cm4gdGhpcy5nZXRMaW5lT2Zmc2V0cygpLmxlbmd0aDtcbiAgICB9XG59XG52YXIgSXM7XG4oZnVuY3Rpb24gKElzKSB7XG4gICAgY29uc3QgdG9TdHJpbmcgPSBPYmplY3QucHJvdG90eXBlLnRvU3RyaW5nO1xuICAgIGZ1bmN0aW9uIGRlZmluZWQodmFsdWUpIHtcbiAgICAgICAgcmV0dXJuIHR5cGVvZiB2YWx1ZSAhPT0gJ3VuZGVmaW5lZCc7XG4gICAgfVxuICAgIElzLmRlZmluZWQgPSBkZWZpbmVkO1xuICAgIGZ1bmN0aW9uIHVuZGVmaW5lZCh2YWx1ZSkge1xuICAgICAgICByZXR1cm4gdHlwZW9mIHZhbHVlID09PSAndW5kZWZpbmVkJztcbiAgICB9XG4gICAgSXMudW5kZWZpbmVkID0gdW5kZWZpbmVkO1xuICAgIGZ1bmN0aW9uIGJvb2xlYW4odmFsdWUpIHtcbiAgICAgICAgcmV0dXJuIHZhbHVlID09PSB0cnVlIHx8IHZhbHVlID09PSBmYWxzZTtcbiAgICB9XG4gICAgSXMuYm9vbGVhbiA9IGJvb2xlYW47XG4gICAgZnVuY3Rpb24gc3RyaW5nKHZhbHVlKSB7XG4gICAgICAgIHJldHVybiB0b1N0cmluZy5jYWxsKHZhbHVlKSA9PT0gJ1tvYmplY3QgU3RyaW5nXSc7XG4gICAgfVxuICAgIElzLnN0cmluZyA9IHN0cmluZztcbiAgICBmdW5jdGlvbiBudW1iZXIodmFsdWUpIHtcbiAgICAgICAgcmV0dXJuIHRvU3RyaW5nLmNhbGwodmFsdWUpID09PSAnW29iamVjdCBOdW1iZXJdJztcbiAgICB9XG4gICAgSXMubnVtYmVyID0gbnVtYmVyO1xuICAgIGZ1bmN0aW9uIG51bWJlclJhbmdlKHZhbHVlLCBtaW4sIG1heCkge1xuICAgICAgICByZXR1cm4gdG9TdHJpbmcuY2FsbCh2YWx1ZSkgPT09ICdbb2JqZWN0IE51bWJlcl0nICYmIG1pbiA8PSB2YWx1ZSAmJiB2YWx1ZSA8PSBtYXg7XG4gICAgfVxuICAgIElzLm51bWJlclJhbmdlID0gbnVtYmVyUmFuZ2U7XG4gICAgZnVuY3Rpb24gaW50ZWdlcih2YWx1ZSkge1xuICAgICAgICByZXR1cm4gdG9TdHJpbmcuY2FsbCh2YWx1ZSkgPT09ICdbb2JqZWN0IE51bWJlcl0nICYmIC0yMTQ3NDgzNjQ4IDw9IHZhbHVlICYmIHZhbHVlIDw9IDIxNDc0ODM2NDc7XG4gICAgfVxuICAgIElzLmludGVnZXIgPSBpbnRlZ2VyO1xuICAgIGZ1bmN0aW9uIHVpbnRlZ2VyKHZhbHVlKSB7XG4gICAgICAgIHJldHVybiB0b1N0cmluZy5jYWxsKHZhbHVlKSA9PT0gJ1tvYmplY3QgTnVtYmVyXScgJiYgMCA8PSB2YWx1ZSAmJiB2YWx1ZSA8PSAyMTQ3NDgzNjQ3O1xuICAgIH1cbiAgICBJcy51aW50ZWdlciA9IHVpbnRlZ2VyO1xuICAgIGZ1bmN0aW9uIGZ1bmModmFsdWUpIHtcbiAgICAgICAgcmV0dXJuIHRvU3RyaW5nLmNhbGwodmFsdWUpID09PSAnW29iamVjdCBGdW5jdGlvbl0nO1xuICAgIH1cbiAgICBJcy5mdW5jID0gZnVuYztcbiAgICBmdW5jdGlvbiBvYmplY3RMaXRlcmFsKHZhbHVlKSB7XG4gICAgICAgIC8vIFN0cmljdGx5IHNwZWFraW5nIGNsYXNzIGluc3RhbmNlcyBwYXNzIHRoaXMgY2hlY2sgYXMgd2VsbC4gU2luY2UgdGhlIExTUFxuICAgICAgICAvLyBkb2Vzbid0IHVzZSBjbGFzc2VzIHdlIGlnbm9yZSB0aGlzIGZvciBub3cuIElmIHdlIGRvIHdlIG5lZWQgdG8gYWRkIHNvbWV0aGluZ1xuICAgICAgICAvLyBsaWtlIHRoaXM6IGBPYmplY3QuZ2V0UHJvdG90eXBlT2YoT2JqZWN0LmdldFByb3RvdHlwZU9mKHgpKSA9PT0gbnVsbGBcbiAgICAgICAgcmV0dXJuIHZhbHVlICE9PSBudWxsICYmIHR5cGVvZiB2YWx1ZSA9PT0gJ29iamVjdCc7XG4gICAgfVxuICAgIElzLm9iamVjdExpdGVyYWwgPSBvYmplY3RMaXRlcmFsO1xuICAgIGZ1bmN0aW9uIHR5cGVkQXJyYXkodmFsdWUsIGNoZWNrKSB7XG4gICAgICAgIHJldHVybiBBcnJheS5pc0FycmF5KHZhbHVlKSAmJiB2YWx1ZS5ldmVyeShjaGVjayk7XG4gICAgfVxuICAgIElzLnR5cGVkQXJyYXkgPSB0eXBlZEFycmF5O1xufSkoSXMgfHwgKElzID0ge30pKTtcbiJdLAogICJtYXBwaW5ncyI6ICI7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7OztBQUFBO0FBQUE7QUFBQTtBQUtBLGFBQU8sZUFBZSxTQUFTLGNBQWMsRUFBRSxPQUFPLEtBQUssQ0FBQztBQUM1RCxjQUFRLGNBQWMsUUFBUSxRQUFRLFFBQVEsT0FBTyxRQUFRLFFBQVEsUUFBUSxTQUFTLFFBQVEsU0FBUyxRQUFRLFVBQVU7QUFDekgsZUFBUyxRQUFRLE9BQU87QUFDcEIsZUFBTyxVQUFVLFFBQVEsVUFBVTtBQUFBLE1BQ3ZDO0FBQ0EsY0FBUSxVQUFVO0FBQ2xCLGVBQVMsT0FBTyxPQUFPO0FBQ25CLGVBQU8sT0FBTyxVQUFVLFlBQVksaUJBQWlCO0FBQUEsTUFDekQ7QUFDQSxjQUFRLFNBQVM7QUFDakIsZUFBUyxPQUFPLE9BQU87QUFDbkIsZUFBTyxPQUFPLFVBQVUsWUFBWSxpQkFBaUI7QUFBQSxNQUN6RDtBQUNBLGNBQVEsU0FBUztBQUNqQixlQUFTLE1BQU0sT0FBTztBQUNsQixlQUFPLGlCQUFpQjtBQUFBLE1BQzVCO0FBQ0EsY0FBUSxRQUFRO0FBQ2hCLGVBQVMsS0FBSyxPQUFPO0FBQ2pCLGVBQU8sT0FBTyxVQUFVO0FBQUEsTUFDNUI7QUFDQSxjQUFRLE9BQU87QUFDZixlQUFTLE1BQU0sT0FBTztBQUNsQixlQUFPLE1BQU0sUUFBUSxLQUFLO0FBQUEsTUFDOUI7QUFDQSxjQUFRLFFBQVE7QUFDaEIsZUFBUyxZQUFZLE9BQU87QUFDeEIsZUFBTyxNQUFNLEtBQUssS0FBSyxNQUFNLE1BQU0sVUFBUSxPQUFPLElBQUksQ0FBQztBQUFBLE1BQzNEO0FBQ0EsY0FBUSxjQUFjO0FBQUE7QUFBQTs7O0FDbEN0QjtBQUFBO0FBQUE7QUFLQSxhQUFPLGVBQWUsU0FBUyxjQUFjLEVBQUUsT0FBTyxLQUFLLENBQUM7QUFDNUQsY0FBUSxVQUFVLFFBQVEs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UUFDQSxXQUFXLEVBQUUsU0FBUyxLQUFLO0FBQUEsUUFDM0IsWUFBWTtBQUFBLFVBQ1IsV0FBc0I7QUFBQSxVQUN0QixzQkFBc0I7QUFBQSxVQUN0QixTQUFzQixFQUFFLFNBQVMsS0FBSztBQUFBLFVBQ3RDLGVBQWU7QUFBQSxZQUNYO0FBQUEsWUFBYTtBQUFBLFlBQVM7QUFBQSxZQUN0QjtBQUFBLFlBQ0E7QUFBQSxZQUNBO0FBQUEsVUFDSjtBQUFBLFVBQ0EsYUFBYSxDQUFDLFFBQVEsU0FBUyxPQUFPLE9BQU8sRUFBRTtBQUFBLFFBQ25EO0FBQUEsUUFDQSxlQUFnQixFQUFFLFNBQVMsS0FBSztBQUFBLFFBQ2hDLFFBQWdCLEVBQUUsU0FBUyxLQUFLO0FBQUEsUUFDaEMsYUFBZ0IsRUFBRSxpQkFBaUIsTUFBTTtBQUFBLFFBQ3pDLFlBQWdCLEVBQUUsZ0JBQWdCLEVBQUUsU0FBUyxNQUFNLEVBQUU7QUFBQTtBQUFBO0FBQUEsUUFHckQsb0JBQXdCLEVBQUUsU0FBUyxNQUFNO0FBQUEsUUFDekMseUJBQXlCLEVBQUUsU0FBUyxNQUFNO0FBQUEsTUFDOUM7QUFBQSxJQUNKO0FBQUEsRUFDSjsiLAogICJuYW1lcyI6IFsiRXJyb3JDb2RlcyIsICJNZXNzYWdlIiwgIlRvdWNoIiwgIkRpc3Bvc2FibGUiLCAiUkFMIiwgIkV2ZW50IiwgIklzIiwgIkNhbmNlbGxhdGlvblRva2VuIiwgIkNhbmNlbGxhdGlvblN0YXRlIiwgIl9jb25uIiwgIklzIiwgIk1lc3NhZ2VSZWFkZXIiLCAiQWJzdHJhY3RNZXNzYWdlUmVhZGVyIiwgIlJlc29sdmVkTWVzc2FnZVJlYWRlck9wdGlvbnMiLCAiSXMiLCAiTWVzc2FnZVdyaXRlciIsICJBYnN0cmFjdE1lc3NhZ2VXcml0ZXIiLCAiUmVzb2x2ZWRNZXNzYWdlV3JpdGVyT3B0aW9ucyIsICJyZXN1bHQiLCAiSXMiLCAiQ2FuY2VsTm90aWZpY2F0aW9uIiwgIlByb2dyZXNzVG9rZW4iLCAiUHJvZ3Jlc3NOb3RpZmljYXRpb24iLCAiU3RhclJlcXVlc3RIYW5kbGVyIiwgIlRyYWNlIiwgIlRyYWNlVmFsdWVzIiwgIlRyYWNlRm9ybWF0IiwgIlNldFRyYWNlTm90aWZpY2F0aW9uIiwgIkxvZ1RyYWNlTm90aWZpY2F0aW9uIiwgIkNvbm5lY3Rpb25FcnJvcnMiLCAiQ29ubmVjdGlvblN0cmF0ZWd5IiwgIklkQ2FuY2VsbGF0aW9uUmVjZWl2ZXJTdHJhdGVneSIsICJSZXF1ZXN0Q2FuY2VsbGF0aW9uUmVjZWl2ZXJTdHJhdGVneSIsICJDYW5jZWxsYXRpb25SZWNlaXZlclN0cmF0ZWd5IiwgIkNhbmNlbGxhdGlvblNlbmRlclN0cmF0ZWd5IiwgIkNhbmNlbGxhdGlvblN0cmF0ZWd5IiwgIk1lc3NhZ2VTdHJhdGVneSIsICJDb25uZWN0aW9uT3B0aW9ucyIsICJDb25uZWN0aW9uU3RhdGUiLCAiY3JlYXRlTWVzc2FnZUNvbm5lY3Rpb24iLCAic3RhcnRUaW1lIiwgIlJJTCIsICJtIiwgImV4cG9ydHMiLCAiY3JlYXRlTWVzc2FnZUNvbm5lY3Rpb24iLCAiaW1wb3J0X3ZzY29kZV9qc29ucnBjIiwgImltcG9ydF92c2NvZGVfanNvbnJwYyIsICJpbXBvcnRfdnNjb2RlX2pzb25ycGMiLCAiaW1wb3J0X3ZzY29kZV9qc29ucnBjIiwgIkRvY3VtZW50VXJpIiwgIlVSSSIsICJpbnRlZ2VyIiwgInVpbnRlZ2VyIiwgIlBvc2l0aW9uIiwgIlJhbmdlIiwgIkxvY2F0aW9uIiwgIkxvY2F0aW9uTGluayIsICJDb2xvciIsICJDb2xvckluZm9ybWF0aW9uIiwgIkNvbG9yUHJlc2VudGF0aW9uIiwgIkZvbGRpbmdSYW5nZUtpbmQiLCAiRm9sZGluZ1JhbmdlIiwgIkRpYWdub3N0aWNSZWxhdGVkSW5mb3JtYXRpb24iLCAibG9jYXRpb24iLCAiRGlhZ25vc3RpY1NldmVyaXR5IiwgIkRpYWdub3N0aWNUYWciLCAiQ29kZURlc2NyaXB0aW9uIiwgIkRpYWdub3N0aWMiLCAiQ29tbWFuZCIsICJUZXh0RWRpdCIsICJDaGFuZ2VBbm5vdGF0aW9uIiwgIkNoYW5nZUFubm90YXRpb25JZGVudGlmaWVyIiwgIkFubm90YXRlZFRleHRFZGl0IiwgIlRleHREb2N1bWVudEVkaXQiLCAiQ3JlYXRlRmlsZSIsICJSZW5hbWVGaWxlIiwgIkRlbGV0ZUZpbGUiLCAiV29ya3NwYWNlRWRpdCIsICJTbmlwcGV0VGV4dEVkaXQiLCAiUmFuZ2UiLCAiVGV4dERvY3VtZW50SWRlbnRpZmllciIsICJWZXJzaW9uZWRUZXh0RG9jdW1lbnRJZGVudGlmaWVyIiwgIk9wdGlvbmFsVmVyc2lvbmVkVGV4dERvY3VtZW50SWRlbnRpZmllciIsICJMYW5ndWFnZUtpbmQiLCAiVGV4dERvY3VtZW50SXRlbSIsICJNYXJrdXBLaW5kIiwgIk1hcmt1cENvbnRlbnQiLCAiQ29tcGxldGlvbkl0ZW1LaW5kIiwgIkluc2VydFRleHRGb3JtYXQiLCAiQ29tcGxldGlvbkl0ZW1UYWciLCAiSW5zZXJ0UmVwbGFjZUVkaXQiLCAiUmFuZ2UiLCAiSW5zZXJ0VGV4dE1vZGUiLCAiQXBwbHlLaW5kIiwgIkNvbXBsZXRpb25JdGVtTGFiZWxEZXRhaWxzIiwgIkNvbXBsZXRpb25JdGVtIiwgIkNvbXBsZXRpb25MaXN0IiwgIk1hcmtlZFN0cmluZyIsICJIb3ZlciIsICJQYXJhbWV0ZXJJbmZvcm1hdGlvbiIsICJTaWduYXR1cmVJbmZvcm1hdGlvbiIsICJEb2N1bWVudEhpZ2hsaWdodEtpbmQiLCAiRG9jdW1lbnRIaWdobGlnaHQiLCAiU3ltYm9sS2luZCIsICJTeW1ib2xUYWciLCAiU3ltYm9sSW5mb3JtYXRpb24iLCAiV29ya3NwYWNlU3ltYm9sIiwgIkRvY3VtZW50U3ltYm9sIiwgIkNvZGVBY3Rpb25LaW5kIiwgIkNvZGVBY3Rpb25UcmlnZ2VyS2luZCIsICJDb2RlQWN0aW9uQ29udGV4dCIsICJDb2RlQWN0aW9uVGFnIiwgIkNvZGVBY3Rpb24iLCAiQ29kZUxlbnMiLCAiRm9ybWF0dGluZ09wdGlvbnMiLCAiRG9jdW1lbnRMaW5rIiwgIlNlbGVjdGlvblJhbmdlIiwgIlNlbWFudGljVG9rZW5UeXBlcyIsICJTZW1hbnRpY1Rva2VuTW9kaWZpZXJzIiwgIlNlbWFudGljVG9rZW5zIiwgIklubGluZVZhbHVlVGV4dCIsICJJbmxpbmVWYWx1ZVZhcmlhYmxlTG9va3VwIiwgIklubGluZVZhbHVlRXZhbHVhdGFibGVFeHByZXNzaW9uIiwgIklubGluZVZhbHVlQ29udGV4dCIsICJJbmxheUhpbnRLaW5kIiwgIklubGF5SGludExhYmVsUGFydCIsICJJbmxheUhpbnQiLCAiUG9zaXRpb24iLCAiU3RyaW5nVmFsdWUiLCAiSW5saW5lQ29tcGxldGlvbkl0ZW0iLCAiSW5saW5lQ29tcGxldGlvbkxpc3QiLCAiSW5saW5lQ29tcGxldGlvblRyaWdnZXJLaW5kIiwgIlNlbGVjdGVkQ29tcGxldGlvbkluZm8iLCAiSW5saW5lQ29tcGxldGlvbkNvbnRleHQiLCAiV29ya3NwYWNlRm9sZGVyIiwgIlRleHREb2N1bWVudCIsICJQb3NpdGlvbiIsICJJcyIsICJ1bmRlZmluZWQiLCAiaW50ZWdlciIsICJ1aW50ZWdlciIsICJtIl0KfQo=
