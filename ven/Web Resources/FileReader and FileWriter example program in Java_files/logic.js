var helpers = createHelpers();
var creative = {
  selectors: [
    '#main-container',
    '#bg-exit',
    '.jellybean',
    '.all-butterfly',
    '.all-cloud-1',
    '.all-cloud-2',
    '.all-lines-cloud-1',
    '.all-lines-cloud-2',
    '.cloud-lines',
    '.all-flowers',
    '.all-road-1',
    '.all-road-2',
    '.all-star-1',
    '.all-star-2',
    '.all-star-3',
    '.all-star-4',
    '.all-sun',
    '.all-tree',
    '.all-trunk',
    '.tree',
    '.all-butterfly',
    '.f4-lines-1',
    '.starts',
    '.copies',
    '.clouds',
    '.f5-bg-1',
    '.f5-copy-1',
    '.f5-copy-2',
    '.f5-logo',
    '.main-cta',
    '.reload-btn',
    '.f5-button-legal',
    '.legal-close-btn',
    '.show-btns'
  ],
  config: {
    timelines: {
      bannerMaster: { delay: 0.3 }
    },
    secondaryPayload: [
      helpers.createSecondaryPayload([
        'https://s0.2mdn.net/ads/studio/cached_libs/tweenmax_1.18.0_499ba64a23378545748ff12d372e59e9_min.js'
      ], 'js'),
      helpers.createSecondaryPayload([
        'sprites.png',
        'jellybean.png'
      ], 'img')
    ],
    isExternalLoaded: false,
    isVisible: false
  },
  dom: {},
  timelines: {},
  rules: helpers.createStyleSheet()
};

function createHelpers() {
  return {
    /**
     * Creates an object to track the secondary payload by type
     * @param {array} srcs  with a list of sources
     * @param {string} type with a string representing the load type
     * @return {object}     with the secondary payload object to be tracked
     */
    createSecondaryPayload: function(srcs, type) {
      return {
        flag: false,
        type: type,
        loaded: 0,
        srcs: srcs
      }
    },
    /**
     * Converts a dashed string into a camel cased one
     * @param {string} sentenceCase with the dashed word to convert
     * @return {string}             with the camel cased string
     */
    toCamelCase: function (sentenceCase) {
      var out = '';
      if (sentenceCase.indexOf('-') < 0) {
        return sentenceCase.replace(/#|\./g, '');
      }
      sentenceCase
        .replace(/#|\./g, '')
        .split('-').forEach(function (el, idx) {
          var add = el.toLowerCase();
          out += (idx === 0 ? add : add[0].toUpperCase() + add.slice(1));
        });

      return out;
    },
    /**
     * Identifies a way to load a source depending on the type
     * @param {object} payload with the payload configuration
     * @param {string} src     with the source
     * @return {void}
     */
    externalLoader: {
      img: function(payload, src) {
        var image = new Image(),
          imageUrl = Enabler.getUrl(src);
        image.onload = image.onerror = image.onabort = validateLoad.bind({}, payload, src);
        image.src = imageUrl;
      },
      js: function(payload, src) {
        Enabler.loadScript(src, validateLoad.bind({}, payload));
      }
    },
    /**
     * Creates a stylesheet to store all the javascript rules
     * @return {object} with a styleSheet object
     */
    createStyleSheet: function () {
      var style = document.createElement('style');

      style.appendChild(document.createTextNode(''));
      document.head.appendChild(style);

      return style.sheet;
    },
    /**
     * Identifies a way to create a CSS rule depending on the type
     * @param {string} ruleName with the css rule name
     * @param {string} src      with the source
     * @return {void}
     */
    cssRuleMaker: {
      img: function(ruleName, src) {
        creative.rules.insertRule('.' + ruleName + '{position:absolute;background-image:url(' + src + ');}', creative.rules.length);
      }
    },
    /**
     * Creates an event listener the same way to have consistency
     * @param {object} config with all the listeners to create
     * @return {void}
     */
    addListeners: function(config) {
      config.forEach(function(listener) {
        listener.obj.addEventListener(listener.type, listener.callback, false);
      });
    },
    getChromeVersion: function() {
      var raw = navigator.userAgent.match(/Chrom(e|ium)\/([0-9]+)\./);
      return raw ? parseInt(raw[2], 10) : false;
    }
  };
}

function loadExternal() {
  creative.config.secondaryPayload.forEach(function(payload) {
    payload.srcs.forEach(function(src) {
      helpers.externalLoader[payload.type](payload, src);
    });
  });
}

function validateLoad(payload, src) {
  payload.loaded += 1;

  // Adds the css rule in case the payload requires it
  if (helpers.cssRuleMaker.hasOwnProperty(payload.type)) {
    helpers.cssRuleMaker[payload.type](src.split('/').pop().split('.')[0], src);
  }
  // Adds the payload flag to true in case all sources are loaded
  if (payload.srcs.length === payload.loaded) {
    payload.flag = true;
  }
  // Adds the external payload flag to true when all secondary payloads are done
  creative.config.isExternalLoaded = creative.config.secondaryPayload.every(function(pl) {
    return pl.flag;
  });

  politeInit();
}

/**
 * Initializes the ad components
 */
function setup() {
  // DOM
  creative.selectors.forEach(function (el, idx) {
    creative.dom[helpers.toCamelCase(el)] = (el[0] === '.') ? document.querySelectorAll(el) : document.querySelector(el);
  });
}

function setupTimelines() {
  // Animations
  creative.timelines.master = new TimelineMax();
  for (tl in creative.config.timelines) {
    creative.timelines[tl] = new TimelineMax(creative.config.timelines[tl]);
    creative.timelines.master.add(creative.timelines[tl]);
  }
}

/**
 * Window onload handler.
 */
function preInit() {
  setup();

  if (Enabler.isInitialized()) {
    init();
  } else {
    Enabler.addEventListener(
      studio.events.StudioEvent.INIT,
      init
    );
  }
}

/**
 * Ad initialisation.
 */
function init() {
  var isVisibleHandler = function() {
    creative.config.isVisible = true;
    politeInit();
  }

  // Add tracking listeners
  addListeners();
  loadExternal();
  // Polite loading
  if (Enabler.isVisible()) {
    isVisibleHandler();
  } else {
    Enabler.addEventListener(
      studio.events.StudioEvent.VISIBLE,
      isVisibleHandler
    );
  }
}

function politeInit() {
  if (creative.config.isExternalLoaded && creative.config.isVisible) {
    setupTimelines();
    show();
    starJsAnimations();
    starCssAnimations();
  }
}

/**
 * Adds appropriate listeners at initialization time
 */
function addListeners() {
  helpers.addListeners([
    { obj: creative.dom.bgExit, type: 'mouseover', callback: handlers.onOver },
    { obj: creative.dom.bgExit, type: 'mouseout', callback: handlers.onOut },
    { obj: creative.dom.bgExit, type: 'mousedown', callback: handlers.onExit },
    { obj: creative.dom.mainCta[0], type: 'mouseover', callback: handlers.onCtaOver },
    { obj: creative.dom.mainCta[0], type: 'mouseout', callback: handlers.onCtaOut },
    { obj: creative.dom.mainCta[0], type: 'mousedown', callback: handlers.onCtaExit },
    { obj: creative.dom.f5ButtonLegal[0], type: 'mousedown', callback: handlers.onBtnLegalOpen },
    { obj: creative.dom.legalCloseBtn[0], type: 'mousedown', callback: handlers.onBtnLegalClose },
    { obj: creative.dom.reloadBtn[0], type: 'mousedown', callback: handlers.onReload }
  ]);
}

/**
 *  Shows the ad.
 */
function show() {
  creative.dom.mainContainer.style.display = 'block';
}

/**
 *  Stars the ad animations by css.
 */
function starCssAnimations() {}

/**
 *  Stars the ad animations by gasp.
 */
function starJsAnimations() {
  var CHROME_VERSION = 49;

  if(helpers.getChromeVersion() >= CHROME_VERSION) {
    CSSPlugin.defaultForce3D = false;
  }

  function frame1() {
    var timeline1 = new TimelineMax(),
      bannerWidth = creative.dom.mainContainer.offsetWidth,
      bannerHeight = creative.dom.mainContainer.offsetHeight;

    function drawStroke(SVGpathElement) {
      var pathlength = SVGpathElement[0].getTotalLength();

      SVGpathElement[0].style.strokeDasharray = pathlength + ' ' + pathlength;
      SVGpathElement[0].style.strokeDashoffset = pathlength;

      return { strokeDashoffset: 0, ease: Power0.easeInOut }
    }

    timeline1
      // Street Objects
      .to(creative.dom.allRoad2, 1, drawStroke(creative.dom.allRoad2), 'frameOne')
      .to(creative.dom.allRoad1, 1, drawStroke(creative.dom.allRoad1), 'frameOne')

      .from(creative.dom.jellybean, 8, { scale: .5, x: '40%', y: '-35%', ease: Power0.easeOut, rotationZ: 0.01 }, 'frameOne')
      .to(creative.dom.allRoad2, 8, { y: '+=33', ease: Power0.easeNone }, 'frameOne+=.2')
      .to(creative.dom.allRoad1, 8, { y: '+=60', ease: Power0.easeNone }, 'frameOne+=.2')

      // Ground Objects
      .fromTo(creative.dom.allFlowers, 3, { top: bannerHeight + 10, left: -34 }, { y: '-=57', x: bannerWidth - 130, ease: Power0.easeNone },'frameOne')
      .to(creative.dom.allFlowers, 1, {opacity: 0},'frameOne+=1.4')
      .to(creative.dom.allTree, 2 , drawStroke(creative.dom.tree), 'frameOne')
      .fromTo(creative.dom.allTree, 7, { x: '-=90', y: -bannerHeight/2 + 110, scale: 1.4 }, { y: '-=60', x: bannerWidth + 35, scale: 1, ease: Power0.easeNone, rotationZ: 0.01 }, 'frameOne')
      .fromTo(creative.dom.allTrunk, 7, {  x: '-=70', top: bannerHeight/2 - 3, scale: 1.1 }, { y: '-=100',x: bannerWidth + 45, scale: 0.9, ease: Power0.easeNone, rotationZ: 0.01 }, 'frameOne+=.1')
      // Sky animation
      .to(creative.dom.allCloud1, .6, drawStroke(creative.dom.allCloud1), 'frameOne')
      .to(creative.dom.allCloud2, .6, drawStroke(creative.dom.allCloud2), 'frameOne')
      .fromTo(creative.dom.allSun, 1, { y: -bannerHeight/2 }, { y: -20, ease: Elastic.easeOut.config(1, 1.2) }, 'frameOne+=.7')

      .to(creative.dom.allSun, 4, { left: '+=30', ease: Power0.easeNone, repeat: 1, yoyo:true }, 'frameOne+=1')
      .to(creative.dom.cloudLines, 4, { left: '+=30', ease: Power0.easeNone, repeat: 1, yoyo:true }, 'frameOne+=1')

      .fromTo(creative.dom.cloudLines, 1, { y: -76 }, { y: -20, ease: Elastic.easeOut.config(1, 1.2) }, 'frameOne+=1')

      .to(creative.dom.allCloud1, 4, { x: '+=30', ease: Power0.easeNone, repeat: 1, yoyo:true }, 'frameOne+=1')
      .to(creative.dom.allCloud2, 4, { x: '+=30', ease: Power0.easeNone, repeat: 1, yoyo:true }, 'frameOne+=1')

      .from(creative.dom.allButterfly, 8, { y: '+=70', x: '-=100' }, 'frameOne')
      .staggerFrom(creative.dom.starts, .2, { scale: 0, opacity: 0, rotationZ: 0.01 }, .2, 'frameOne+=5')

      // Jelly effect
      .to(creative.dom.allButterfly, .5, { scaleX: 1.2,  scaleY: .8, repeat: 11, yoyo: true, rotationZ: 0.01 }, 'frameOne')
      .staggerFromTo(creative.dom.starts, .5, { scale: 1 }, { scaleX: 1.2,  scaleY: .8, repeat: 7, yoyo: true, rotationZ: 0.01 }, .2, 'frameOne+=5.2')
      .staggerFromTo(creative.dom.cloudLines, .5, { scale: 1 }, { scaleX: 1.05,  scaleY: .95, repeat: 15, yoyo: true, rotationZ: 0.01 }, .2, 'frameOne')
      .staggerFromTo(creative.dom.clouds, .5, { scale: 1 }, { scaleX: 1.05,  scaleY: .95, repeat: 15, yoyo: true, rotationZ: 0.01 }, .2, 'frameOne')
      .to(creative.dom.allSun, .5, { scaleX: 1.05,  scaleY: .95, repeat: 17, yoyo: true, rotationZ: 0.01 }, .2, 'frameOne')

      // Texts
      .staggerFrom(creative.dom.copies, .2, { x: '-=20', opacity: 0, ease: Back.easeOut.config(.5), repeatDelay: 1.5, yoyo: true, repeat: 1 }, 1.9, 'frameOne')

      // Wheels Animation
      .to(creative.dom.jellybean, .2, { backgroundPosition: '0 -320px', ease:SteppedEase.config(1), repeat: 40 }, 'frameOne')

      // Animation out
      .to(creative.dom.cloudLines, .3, { y: -bannerHeight/2, ease: Elastic.easeIn.config(1, .5)}, '-=1.5')
      .to(creative.dom.allCloud1, .2, { strokeDashoffset: creative.dom.allCloud1[0].getTotalLength() }, '-=1.2')
      .to(creative.dom.allCloud2, .2, { strokeDashoffset: creative.dom.allCloud2[0].getTotalLength() }, '-=1.2')
      .to(creative.dom.allSun, .1, { y: -bannerHeight/2, ease: Elastic.easeIn.config(1, .5)}, '-=1.2')
      .to(creative.dom.allRoad2, .2, { strokeDashoffset: creative.dom.allRoad2[0].getTotalLength() }, '-=1.2')
      .to(creative.dom.allRoad1, .2, { strokeDashoffset: creative.dom.allRoad1[0].getTotalLength() }, '-=1.2')
      .staggerTo(creative.dom.starts, .2, { scale: 0, opacity: 0, rotationZ: 0.01 }, .1, '-=1.3')
      .to(creative.dom.allButterfly, .2, { scale: 0, opacity: 0, rotationZ: 0.01 }, '-=1.2')
    ;

    return timeline1;
  }

  function frame2() {
    var timeline1 = new TimelineMax();

    timeline1
      .from(creative.dom.f5Bg1, 1, { opacity: 0 }, 'frameTwo')
      .from(creative.dom.f5Copy1, .6, { x: '-=80', opacity: 0, ease: Back.easeOut.config(1.7) }, 'frameTwo+=.6')
      .from(creative.dom.f5Copy2, .6, { x: '+=80', opacity: 0, ease: Back.easeOut.config(1.7) }, 'frameTwo+=.6')
      .from(creative.dom.f5Logo, .3, { opacity: 0 }, 'frameTwo+=.6')
      .from(creative.dom.mainCta, .5, { opacity: 0 }, 'frameTwo+=.8')
      .from(creative.dom.showBtns, .3, { opacity: 0 }, .3, 'frameTwo+=.6')
      .to(creative.dom.reloadBtn, .3, { display: 'block' }, .3, 'frameTwo+=.6')
      .to(creative.dom.f5ButtonLegal, .3, { display: 'block' }, .3, 'frameTwo+=.6')
    ;

    return timeline1;
  }

  creative.timelines.bannerMaster.add(frame1());
  creative.timelines.bannerMaster.add(frame2(), '-=.8');
}

// ---------------------------------------------------------------------------------
// MAIN
// ---------------------------------------------------------------------------------

// Event listeners handlers
var handlers = {
  onOver: function (e) {
    var code = 'vlp_background-rollover';
    console.log(code);
    Enabler.counter(code);
  },
  onOut: function (e) {
    var code = 'vlp_background-rollout';
    console.log(code)
    Enabler.counter(code);
  },
  onExit: function (e) {
    var code = 'vlp_background';
    console.log(code);
    Enabler.counter(code);
    Enabler.exit(code);
  },
  onCtaOver: function (e) {
    var code = 'vlp_cta_vehicle-details-rollover';
    console.log(code);
    Enabler.counter(code);
  },
  onCtaOut: function (e) {
    var code = 'vlp_cta_vehicle-details-rollout';
    console.log(code);
    Enabler.counter(code);
  },
  onCtaExit: function (e) {
    var code = 'vlp_cta_vehicle-details';
    console.log(code);
    Enabler.counter(code);
    Enabler.exit(code);
  },
  onBtnLegalOpen: function (e) {
    var code = 'legal-open';
    console.log(code);
    Enabler.startTimer(code);
    Enabler.counter(code);
  },
  onBtnLegalClose: function (e) {
    var code = 'legal-close';
    console.log(code);
    Enabler.stopTimer('legal-open');
    Enabler.counter(code);
  },
  onReload: function (e) {
    var code = 'reload';
    console.log(code);
    Enabler.counter(code);
    creative.timelines.master.restart()
  }
}

/**
 *  Main onload handler
 */
window.addEventListener('load', preInit);
