// ///////////////////
// OPEN AJAX HUB
// ///////////////////

D2OpenAjaxHub = function() {

	this.hubClient = null;
	
	if (typeof(console) == 'undefined')
	    console = {};

	if (typeof(console.log) == 'undefined')
	    console.log = function(){};

	if (typeof(console.debug) == 'undefined')
	    console.debug = console.log;

	if (typeof(console.error) == 'undefined')
	    console.error = console.log;
	

	// ///////////////////
	// CONTEXT / WIDGET INFO
	// ///////////////////

	this.sContextUid = null;
	this.sWidgetType = null;
	this.sWidgetId = null;
	this.bActive = true;

	// ///////////////////
	// EVENTS
	// ///////////////////
	this.registeredChannelIds = new Array();
	this.registeredChannels = new Array();

	this.sStoredChannel = null;
	this.sStoredMessage = null;
	this.fStoredCallback = null;
	this.bStoredMessageConsumed = false;

}

function D2OpenAjaxHub() {
}

D2OpenAjaxHub.prototype.connectHub = function(fOnConnectCompleted, fOnInit, fOnActive) {

	console.log("Connect hub");

	var refD2OAH = this;

	function clientSecurityAlertHandler(source, alertType) {
		console.log(" security alert  - source :" + source + " ---- type:" + alertType);
	}

	this.hubClient = new OpenAjax.hub.IframeHubClient({
		HubClient : {
			onSecurityAlert : clientSecurityAlertHandler
		}
	});

	/* Callback that is invoked upon successful connection to the Managed Hub */
	function clientConnectCompleted(hubClient, success, error) {
		console.log(" connect ---  hubclient:" + hubClient + " ---- success:" + success + "  ---- error:" + error);

		refD2OAH.parseClientId();

		// connecting to specific channels
		// /////////

		// active widget channel
		function onActive(channel, message) {

			var openAjaxMessage = new OpenAjaxMessage();
			openAjaxMessage.parse(message);
			openAjaxMessage.setVolatile(true);

			if (openAjaxMessage.getContainerUid() == refD2OAH.getContextUid() && openAjaxMessage.getTargetId() == refD2OAH.getWidgetId()) {
				var bVal = openAjaxMessage.getValue() == "true";
				refD2OAH.setActive(bVal);
				if (fOnActive != null)
					fOnActive.call(window, bVal);
			}

		}
		;
		hubClient.subscribe("D2_EVENT_IFRAME_ACTIVE", onActive);

		// init widget channel
		function onInit(channel, message) {

			var openAjaxMessage = new OpenAjaxMessage();
			openAjaxMessage.parse(message);
			openAjaxMessage.setVolatile(true);

			if (openAjaxMessage.getContainerUid() == refD2OAH.getContextUid() && openAjaxMessage.getTargetId() == refD2OAH.getWidgetId()) {

				if ("int" == openAjaxMessage.get("source")) {
					if (fOnInit != null)
						fOnInit.call(window, openAjaxMessage);
				}
			}

		}
		;
		hubClient.subscribe("D2_EVENT_IFRAME_INIT", onInit);

		// calling user callback
		fOnConnectCompleted.call(window, hubClient, success, error);

		// telling the portal that the widget is init'ed to gather extra info
		var oamInit = new OpenAjaxMessage();
		oamInit.put("source", "ext");
		oamInit.setTargetId(refD2OAH.getWidgetId());
		refD2OAH.sendMessage("D2_EVENT_IFRAME_INIT", oamInit);
	}

	// Connect to the ManagedHub
	this.hubClient.connect(clientConnectCompleted);
}

D2OpenAjaxHub.prototype.disconnectHub = function() {
	console.log(" disconnect ---  hubclient:" + this.hubClient);

	this.unsubscribeFromAllChannels();

	this.hubClient.disconnect();

}

// ///////////////////
// CONTEXT / WIDGET INFO
// ///////////////////

// Returns the context uid for the widget
// This uid has to be sent to the D2 portal as it is mandatory for many
// operations
D2OpenAjaxHub.prototype.getContextUid = function() {
	if (this.sContextUid == null && this.hubClient != null) {
		this.parseClientId();
	}
	return this.sContextUid;
}

// Returns the type of the widget
D2OpenAjaxHub.prototype.getWidgetType = function() {
	if (this.sWidgetType == null && this.hubClient != null) {
		this.parseClientId();
	}
	return this.sWidgetType;
}

// Returns the id of the widget
D2OpenAjaxHub.prototype.getWidgetId = function() {
	if (this.sWidgetId == null && this.hubClient != null) {
		this.parseClientId();
	}
	return this.sWidgetId;
}

D2OpenAjaxHub.prototype.parseClientId = function() {
	if (this.hubClient != null) {

		// the hubCLientId contains parameters and is encoded like an openAjax
		// message
		var oamId = new OpenAjaxMessage()
		oamId.parse(this.hubClient.getClientID());

		this.sContextUid = oamId.getContainerUid();
		console.log("setting context uid to :" + this.sContextUid);

		this.sWidgetType = oamId.getTargetType();
		console.log("setting widget type  to :" + this.sWidgetType);

		this.sWidgetId = oamId.getId();
		console.log("setting widget id  to :" + this.sWidgetId);
	}
}

D2OpenAjaxHub.prototype.setActive = function(bActiveFlag, scope) {
	console.log("setting iframe widget to  name to " + (bActiveFlag ? "active" : "inactive"));

	var refD2OAH = scope != null ? scope : this;
	refD2OAH.bActive = bActiveFlag;

	if (refD2OAH.isActive())
		refD2OAH.replayMessage();

}

D2OpenAjaxHub.prototype.isActive = function() {
	return this.bActive;
}

// ///////////////////
// EVENTS
// ///////////////////

D2OpenAjaxHub.prototype.sendMessage = function(sChannel, oMessage) {

	oMessage.setContainerUid(this.getContextUid());
	oMessage.setSender(this.getWidgetId());

	this.sendMessageString(sChannel, oMessage.toString());
}

D2OpenAjaxHub.prototype.sendMessages = function(arrayStringChannel, oMessage) {

	this.sendMessageStrings(arrayStringChannel, oMessage.toString());
}

D2OpenAjaxHub.prototype.sendMessageString = function(sChannel, sMessage) {
	this.hubClient.publish(sChannel, sMessage);
}

D2OpenAjaxHub.prototype.sendMessageStrings = function(arrayStringChannel, sMessage) {
	for ( var i = 0; i < arrayStringChannel.length; i++) {
		this.hubClient.publish(arrayStringChannel[i], sMessage);
	}
}

D2OpenAjaxHub.prototype.subscribeToChannel = function(sChannel, fCallbackDataReceived, bHandleMessageIfInactive) {
	// checking if channel is not already registered
	var index = -1;
	for ( var i = 0; i < this.registeredChannels.length; i++) {
		if (this.registeredChannels[i] == sChannel) {
			index = i;
			break;
		}
	}

	if (index == -1 && typeof (fCallbackDataReceived) != "undefined" && fCallbackDataReceived != null) {

		var refD2OAH = this;

		// subscribing and adding filter channel function

		var oaCallback;
		oaCallback = function openAjaxCallback(sChannel, sMessage) {

			var _bHandleMessageIfInactive = typeof (bHandleMessageIfInactive) != 'undefined' ? bHandleMessageIfInactive : false;
			var openAjaxMessage = new OpenAjaxMessage();
			openAjaxMessage.parse(sMessage);

			// checking if message is ok for the widget's container
			if (openAjaxMessage.isGlobal() || openAjaxMessage.getContainerUid() == refD2OAH.getContextUid()) {

				// excluding by type if set
				var sExcludedType = openAjaxMessage.getExcludedType();
				if (sExcludedType == null || sExcludedType != widget.getWidgetType()) {

					// excluding by id if set
					var sExcludedId = openAjaxMessage.getExcludedId();
					if (sExcludedId == null || sExcludedId != widget.getWidgetId()) {

						// including by type if set
						var sTargetType = openAjaxMessage.getTargetType();
						if (sTargetType == null || sTargetType == widget.getWidgetType()) {

							// including by id if set
							var sTargetId = openAjaxMessage.getTargetId();
							if (sTargetId == null || targetId == widget.getWidgetId()) {

								// if the widget is active, the callback is
								// executed
								if (refD2OAH.isActive() || _bHandleMessageIfInactive)
									fCallbackDataReceived.call(window, sChannel, openAjaxMessage);

								// stores the message to replay it if necessary
								if (!openAjaxMessage.isVolatile())
									refD2OAH.storeMessage(sChannel, sMessage, oaCallback, _bHandleMessageIfInactive);
							}
						}
					}
				}
			}
		}

		var id = this.hubClient.subscribe(sChannel, oaCallback, this);
		this.registeredChannels.push(sChannel);
		this.registeredChannelIds.push(id);

	} else {
		console.log("subscribeToChannel : Channel " + sChannel + " is already registered");
	}

	this.logRegisteredChannels();
}

D2OpenAjaxHub.prototype.subscribeToChannels = function(arrayStringChannel, fCallbackDataReceived, bHandleMessageIfInactive) {
	for ( var i = 0; i < arrayStringChannel.length; i++) {
		this.subscribeToChannel(arrayStringChannel[i], fCallbackDataReceived, bHandleMessageIfInactive);
	}
}

D2OpenAjaxHub.prototype.unsubscribeFromChannel = function(sChannel, bLog) {
	// checking if channel is not already registered
	var index = -1;
	for ( var i = 0; i < this.registeredChannels.length; i++) {
		if (this.registeredChannels[i] == sChannel) {
			index = i;
			break;
		}
	}
	if (index != -1) {
		this.hubClient.unsubscribe(this.registeredChannelIds[index]);
		this.registeredChannels.splice(index, 1);
		this.registeredChannelIds.splice(index, 1);

	} else {
		console.log("unsubscribeFromChannel : Channel " + sChannel + " is not registered");
	}

	this.logRegisteredChannels();
}

D2OpenAjaxHub.prototype.unsubscribeFromChannels = function(arrayStringChannel, bLog) {
	for ( var i = 0; i < arrayStringChannel.length; i++) {
		this.hubClient.unsubscribeFromChannel(arrayStringChannel[i], bLog);
	}
}

D2OpenAjaxHub.prototype.unsubscribeFromAllChannels = function() {
	for ( var i = 0; i < this.registeredChannelIds.length; i++) {
		this.hubClient.unsubscribe(this.registeredChannelIds[i]);

	}
	this.registeredChannels.length = 0;
	this.registeredChannelIds.length = 0;
	this.logRegisteredChannels();
}

D2OpenAjaxHub.prototype.logRegisteredChannels = function() {
	if (this.registeredChannels.length > 0) {
		console.log("Registered channels :");
		for ( var i = 0; i < this.registeredChannels.length; i++) {
			console.log(this.registeredChannels[i] + " ( " + this.registeredChannelIds[i] + ")");
		}
	} else {
		console.log("No registered channels :");
	}
}

D2OpenAjaxHub.prototype.storeMessage = function(sChannel, sMessage, fCallback, bHandleMessageIfInactive) {
	console.log("storing message :" + sChannel + " : " + sMessage);

	this.sStoredChannel = sChannel;
	this.fStoredCallback = fCallback;
	this.sStoredMessage = sMessage;
	this.bStoredMessageConsumed = this.isActive() || bHandleMessageIfInactive;
}

D2OpenAjaxHub.prototype.replayMessage = function(bForceReplay) {
	if ((bForceReplay || !this.bStoredMessageConsumed) && this.sStoredChannel != null && this.fStoredCallback != null && this.sStoredMessage != null) {
		console.log("Replaying message : " + this.sStoredMessage + " - client : " + this.getContextUid());
		this.fStoredCallback.call(window, this.sStoredChannel, this.sStoredMessage);
		this.bStoredMessageConsumed = true;
	}
}

// /////////////////////
// OPEN AJAX MESSAGE
// /////////////////////

OpenAjaxMessage = function() {

	// constants
	this.PARAM_EQ = "==";
	this.PARAM_SEPARATOR = "!!";
	this.PARAM_ID = "oam_id";
	this.PARAM_CONTAINER_UID = "oam_cuid";
	this.PARAM_GLOBAL = "oam_global";
	this.PARAM_SENDER = "oam_sender";
	this.PARAM_VALUE = "oam_value";
	this.PARAM_TARGET_TYPE = "oam_target_type";
	this.PARAM_TARGET_ID = "oam_target_id";
	this.PARAM_EXCLUDED_TYPE = "oam_excluded_type";
	this.PARAM_EXCLUDED_ID = "oam_excluded_id";
	this.PARAM_VOLATILE = "oam_volatile";
	this.PARAM_SPLIT_SEPARATOR = "¬";

	// variables
	this.current = undefined;
	this.size = 0;
	this.isLinked = true;

}

function OpenAjaxMessage() {
	this.setContainerUid(this.getContextUid());
	this.setSender(hubClient.getClientID());

}

// ////////////////////////////////
// message base map-like functions
// ////////////////////////////////

OpenAjaxMessage.from = function(obj, foreignKeys) {
	var message = new OpenAjaxMessage();

	for ( var prop in obj) {
		if (foreignKeys || obj.hasOwnProperty(prop))
			message.put(prop, obj[prop]);
	}

	return message;
};

OpenAjaxMessage.noop = function() {
	return this;
};

OpenAjaxMessage.illegal = function() {
	throw new Error('can\'t do this with unlinked messages');
};

OpenAjaxMessage.prototype.disableLinking = function() {
	this.isLinked = false;
	this.link = OpenAjaxMessage.noop;
	this.unlink = OpenAjaxMessage.noop;
	this.disableLinking = OpenAjaxMessage.noop;
	this.next = OpenAjaxMessage.illegal;
	this.key = OpenAjaxMessage.illegal;
	this.value = OpenAjaxMessage.illegal;
	this.removeAll = OpenAjaxMessage.illegal;
	this.each = OpenAjaxMessage.illegal;
	this.flip = OpenAjaxMessage.illegal;
	this.drop = OpenAjaxMessage.illegal;
	this.listKeys = OpenAjaxMessage.illegal;
	this.listValues = OpenAjaxMessage.illegal;

	return this;
};

OpenAjaxMessage.prototype.hash = function(value) {
	return value instanceof Object ? (value.__hash || (value.__hash = 'object ' + ++arguments.callee.current)) : (typeof value) + ' ' + String(value);
};

OpenAjaxMessage.prototype.hash.current = 0;

OpenAjaxMessage.prototype.link = function(entry) {
	if (this.size === 0) {
		entry.prev = entry;
		entry.next = entry;
		this.current = entry;
	} else {
		entry.prev = this.current.prev;
		entry.prev.next = entry;
		entry.next = this.current;
		this.current.prev = entry;
	}
};

OpenAjaxMessage.prototype.unlink = function(entry) {
	if (this.size === 0)
		this.current = undefined;
	else {
		entry.prev.next = entry.next;
		entry.next.prev = entry.prev;
		if (entry === this.current)
			this.current = entry.next;
	}
};

OpenAjaxMessage.prototype.get = function(key) {
	var entry = this[this.hash(key)];
	return typeof entry === 'undefined' ? undefined : entry.value;
};

OpenAjaxMessage.prototype.put = function(key, value) {
	var hash = this.hash(key);
	if (this.hasOwnProperty(hash))
		this[hash].value = value;
	else {
		var entry = {
			key : key,
			value : value
		};
		this[hash] = entry;

		this.link(entry);
		++this.size;
	}

	return this;
};

OpenAjaxMessage.prototype.remove = function(key) {
	var hash = this.hash(key);

	if (this.hasOwnProperty(hash)) {
		--this.size;
		this.unlink(this[hash]);

		delete this[hash];
	}

	return this;
};

OpenAjaxMessage.prototype.removeAll = function() {
	while (this.size)
		this.remove(this.key());

	return this;
};

OpenAjaxMessage.prototype.contains = function(key) {
	return this.hasOwnProperty(this.hash(key));
};

OpenAjaxMessage.prototype.isUndefined = function(key) {
	var hash = this.hash(key);
	return this.hasOwnProperty(hash) ? typeof this[hash] === 'undefined' : false;
};

OpenAjaxMessage.prototype.next = function() {
	this.current = this.current.next;
};

OpenAjaxMessage.prototype.key = function() {
	return this.current.key;
};

OpenAjaxMessage.prototype.value = function() {
	return this.current.value;
};

OpenAjaxMessage.prototype.each = function(func, thisArg) {
	if (typeof thisArg === 'undefined')
		thisArg = this;

	for ( var i = this.size; i--; this.next()) {
		var n = func.call(thisArg, this.key(), this.value(), i > 0);
		if (typeof n === 'number')
			i += n; // allows to add/remove entries in func
	}

	return this;
};

OpenAjaxMessage.prototype.flip = function(linkEntries) {
	var message = new OpenAjaxMessage(linkEntries);

	for ( var i = this.size; i--; this.next()) {
		var value = this.value(), list = message.get(value);

		if (list)
			list.push(this.key());
		else
			message.put(value, [ this.key() ]);
	}

	return message;
};

OpenAjaxMessage.prototype.drop = function(func, thisArg) {
	if (typeof thisArg === 'undefined')
		thisArg = this;

	for ( var i = this.size; i--;) {
		if (func.call(thisArg, this.key(), this.value())) {
			this.remove(this.key());
			--i;
		} else
			this.next();
	}

	return this;
};

OpenAjaxMessage.prototype.listValues = function() {
	var list = [];

	for ( var i = this.size; i--; this.next())
		list.push(this.value());

	return list;
}

OpenAjaxMessage.prototype.listKeys = function() {
	var list = [];

	for ( var i = this.size; i--; this.next())
		list.push(this.key());

	return list;
}

OpenAjaxMessage.reverseIndexTableFrom = function(array, linkEntries) {
	var message = new OpenAjaxMessage(linkEntries);

	for ( var i = 0, len = array.length; i < len; ++i) {
		var entry = array[i], list = message.get(entry);

		if (list)
			list.push(i);
		else
			message.put(entry, [ i ]);
	}

	return message;
};

OpenAjaxMessage.cross = function(message1, message2, func, thisArg) {
	var linkedMessage, otherMessage;

	if (message1.isLinked) {
		linkedMessage = message1;
		otherMessage = message2;
	} else if (message2.isLinked) {
		linkedMessage = message2;
		otherMessage = message1;
	} else
		OpenAjaxMessage.illegal();

	for ( var i = linkedMessage.size; i--; linkedMessage.next()) {
		var key = linkedMessage.key();
		if (otherMessage.contains(key))
			func.call(thisArg, key, message1.get(key), message2.get(key));
	}

	return thisArg;
};

OpenAjaxMessage.uniqueArray = function(array) {
	var message = new OpenAjaxMessage;

	for ( var i = 0, len = array.length; i < len; ++i)
		message.put(array[i]);

	return message.listKeys();
};

// //////////////////////////////////////
// message getters / setters / helpers
// //////////////////////////////////////

/**
 * @return true if the message is broadcasted to all of the portal widgets,
 *         false if the message is broadcasted to the widgets in the current
 *         tab/container
 */
OpenAjaxMessage.prototype.isGlobal = function() {
	return "true" == this.get(this.PARAM_GLOBAL);
}

/**
 * @param global :
 *            true if the message should be broadcasted to all of the portal
 *            widgets, false if the message should be broadcasted to the widgets
 *            in the current tab/container
 * @return global
 */
OpenAjaxMessage.prototype.setGlobal = function(bGlobal) {
	this.put(this.PARAM_GLOBAL, bGlobal?"true":"false");
	return bGlobal;
}

/**
 * 
 * @param raw :
 *            if true, the id param will be sent as it is stored, even if
 *            containing multiple ids
 * 
 * @return the id information sent with the message ( it can be the id of the
 *         document to fetch, for example ) - if the message contains multiple
 *         ids, it'll only send the first one if raw set to false
 */
OpenAjaxMessage.prototype.getId = function(bRaw) {
	var sId = null;
	if (this.isMultipleIds() && !bRaw) {
		var aIds = this.getIds();
		if (aIds != null && aIds.length > 0)
			sId = aIds[0];
	} else
		sId = this.get(this.PARAM_ID);

	return sId;
}

/**
 * @return the ids information sent with the message in array
 */
OpenAjaxMessage.prototype.getIds = function() {
	var aIds = this.get(this.PARAM_ID);
	if (aIds != null) {
		return aIds.split(this.PARAM_SPLIT_SEPARATOR);
	} else
		return null;
}

/**
 * @param id :
 *            id information sent with the message ( it can be the id of the
 *            document to fetch, for example )
 * @return id
 */
OpenAjaxMessage.prototype.setId = function(sId) {
	this.put(this.PARAM_ID, sId);
	return id;
}

/**
 * @param ids :
 *            ids information sent with the message ( it can be the id of the
 *            document to fetch, for example )
 * @return ids concatenated
 */
OpenAjaxMessage.prototype.setIds = function(aIds) {
	var sIds = aIds.join(ids, this.SEPARATOR_VALUE);
	this.put(this.PARAM_ID, sIds);
	return sIds;
}

/**
 * @return true if this message contains multiple ids
 */
OpenAjaxMessage.prototype.isMultipleIds = function() {
	var sId = this.get(this.PARAM_ID);
	return sId != null && sId.indexOf(this.SEPARATOR_VALUE) != -1;
}

/**
 * @return the target widget types information sent with the message
 */
OpenAjaxMessage.prototype.getTargetType = function() {
	return this.get(this.PARAM_TARGET_TYPE);
}

/**
 * @param target :
 *            target widget type information sent with the message
 * @return target
 */
OpenAjaxMessage.prototype.setTargetType = function(sTarget) {
	this.put(this.PARAM_TARGET_TYPE, sTarget);
	return sTarget;
}

/**
 * @return the target widget id information sent with the message
 */
OpenAjaxMessage.prototype.getTargetId = function() {
	return this.get(this.PARAM_TARGET_ID);
}

/**
 * @param target :
 *            target widget id information sent with the message
 * @return target
 */
OpenAjaxMessage.prototype.setTargetId = function(sTarget) {
	this.put(this.PARAM_TARGET_ID, sTarget);
	return sTarget;
}

/**
 * @return the excluded widget type information sent with the message
 */
OpenAjaxMessage.prototype.getExcludedType = function() {
	return this.get(this.PARAM_EXCLUDED_TYPE);
}

/**
 * @param excluded :
 *            excluded widget type information sent with the message
 * @return target
 */
OpenAjaxMessage.prototype.setExcludedType = function(sExcluded) {
	this.put(this.PARAM_EXCLUDED_TYPE, sExcluded);
	return sExcluded;
}

/**
 * @return the excluded widget id information sent with the message
 */
OpenAjaxMessage.prototype.getExcludedId = function() {
	return this.get(this.PARAM_EXCLUDED_ID);
}

/**
 * @param excluded :
 *            excluded widget id information sent with the message
 * @return target
 */
OpenAjaxMessage.prototype.setExcludedId = function(sExcluded) {
	this.put(this.PARAM_EXCLUDED_ID, sExcluded);
	return sExcluded;
}

/**
 * @return the container UID from where the sender widget sent this message
 */
OpenAjaxMessage.prototype.getContainerUid = function() {
	return this.get(this.PARAM_CONTAINER_UID);
}

/**
 * You should not use this method as the container uid is already set by the
 * constructors
 * 
 * @param containerUid
 * @return container Uid
 */
OpenAjaxMessage.prototype.setContainerUid = function(sContainerUid) {
	this.put(this.PARAM_CONTAINER_UID, sContainerUid);
	return sContainerUid;
}

/**
 * @return the volatile state of the message
 */
OpenAjaxMessage.prototype.isVolatile = function() {
	return this.get(this.PARAM_VOLATILE);
}

/**
 * You should use this method only if you want your message not to be stored for a
 * replay on inactive widget
 * 
 * @param volatileEvent :
 *            true to avoid storing for replay
 * @return volatileEvent
 */
OpenAjaxMessage.prototype.setVolatile = function(bVolatileEvent) {
	this.put(this.PARAM_VOLATILE, bVolatileEvent);
	return bVolatileEvent;
}

/**
 * @return the id of the sender widget for this message
 */
OpenAjaxMessage.prototype.getSender = function() {
	return this.get(this.PARAM_SENDER);
}

/**
 * You should not use this method as the sender is already set by the
 * constructors
 * 
 * @param sender :
 *            the id of the sender widget for this message
 * @return sender
 */
OpenAjaxMessage.prototype.setSender = function(sSender) {
	this.put(this.PARAM_SENDER, sSender);
	return sSender;
}

/**
 * @return the value information sent with the message
 */
OpenAjaxMessage.prototype.getValue = function() {
	return this.get(this.PARAM_VALUE);
}

/**
 * @param value :
 *            the value information sent with the message
 * @return value
 */
OpenAjaxMessage.prototype.setValue = function(sValue) {
	this.put(this.PARAM_VALUE, sValue);
	return sValue;
}

// //////////////////////////////////////
// message parsing / concat
// //////////////////////////////////////

OpenAjaxMessage.prototype.toString = function() {
	var string = '';

	function addEntry(key, value, hasNext) {
		string += key + this.PARAM_EQ + value;
		if (hasNext)
			string += this.PARAM_SEPARATOR;
	}

	if (this.isLinked && this.size) {
		this.each(addEntry);
	}

	return string;
};

OpenAjaxMessage.prototype.parse = function(sMessage) {
	try {
		var aString = sMessage.split(this.PARAM_SEPARATOR);

		for ( var i = 0; i < aString.length; i++) {
			var aDuo = aString[i].split(this.PARAM_EQ);
			this.put(aDuo[0], aDuo[1]);
		}
	} catch (exception) {
		console.log("error while parsing message : " + sMessage + " - error:" + exception.message);
	}

};
