"use strict";(self.webpackChunk_N_E=self.webpackChunk_N_E||[]).push([[50],{4050:function(e,n,t){t.r(n),n.default=async(e={})=>new Promise((n=>{const t=document.createElement("input");t.type="file";const s=[...e.mimeTypes?e.mimeTypes:[],e.extensions?e.extensions.map((e=>"."+e)):[]].join();t.multiple=e.multiple||!1,t.accept=s||"*/*",t.addEventListener("change",(()=>(t.remove(),n(t.multiple?t.files:t.files[0])))),t.click()}))}}]);