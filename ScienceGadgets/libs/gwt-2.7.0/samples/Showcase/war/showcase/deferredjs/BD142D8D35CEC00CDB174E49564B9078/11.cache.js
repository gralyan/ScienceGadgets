$wnd.showcase.runAsyncCallback11("function BSb(){}\nfunction DSb(){}\nfunction vSb(a,b){a.b=b}\nfunction wSb(a){if(a==lSb){return true}vB();return a==oSb}\nfunction xSb(a){if(a==kSb){return true}vB();return a==jSb}\nfunction CSb(a){this.b=(fUb(),aUb).a;this.e=(kUb(),jUb).a;this.a=a}\nfunction tSb(a,b){var c;c=XO(a.fb,159);c.b=b.a;!!c.d&&bNb(c.d,b)}\nfunction uSb(a,b){var c;c=XO(a.fb,159);c.e=b.a;!!c.d&&dNb(c.d,b)}\nfunction pSb(){pSb=u8;iSb=new BSb;lSb=new BSb;kSb=new BSb;jSb=new BSb;mSb=new BSb;nSb=new BSb;oSb=new BSb}\nfunction ySb(){pSb();fNb.call(this);this.b=(fUb(),aUb);this.c=(kUb(),jUb);wp((OJb(),this.e),Hoc,0);wp(this.e,Ioc,0)}\nfunction qSb(a,b,c){var d;if(c==iSb){if(b==a.a){return}else if(a.a){throw new r9b('Only one CENTER widget may be added')}}Rh(b);C2b(a.j,b);c==iSb&&(a.a=b);d=new CSb(c);b.fb=d;tSb(b,a.b);uSb(b,a.c);sSb(a);Th(b,a)}\nfunction rSb(a,b){var c,d,e,f,g,h,i;i2b((OJb(),a.hb),'',b);h=new Ygc;i=new M2b(a.j);while(i.b<i.c.c){c=K2b(i);g=XO(c.fb,159).a;e=XO(h.Uh(g),86);d=!e?1:e.a;f=g==mSb?'north'+d:g==nSb?'south'+d:g==oSb?'west'+d:g==jSb?'east'+d:g==lSb?'linestart'+d:g==kSb?'lineend'+d:xoc;i2b(Qp(c.hb),b,f);h.Vh(g,F9b(d+1))}}\nfunction sSb(a){var b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r;b=(OJb(),a.d);while(sLb(b)>0){cp(b,rLb(b,0))}o=1;e=1;for(i=new M2b(a.j);i.b<i.c.c;){d=K2b(i);f=XO(d.fb,159).a;f==mSb||f==nSb?++o:(f==jSb||f==oSb||f==lSb||f==kSb)&&++e}p=PO(M2,skc,266,o,0,1);for(g=0;g<o;++g){p[g]=new DSb;p[g].b=$doc.createElement(Foc);$o(b,VJb(p[g].b))}k=0;l=e-1;m=0;q=o-1;c=null;for(h=new M2b(a.j);h.b<h.c.c;){d=K2b(h);j=XO(d.fb,159);r=$doc.createElement(Goc);j.d=r;xp(j.d,roc,j.b);Eq(j.d.style,soc,j.e);xp(j.d,Jkc,j.f);xp(j.d,Ikc,j.c);if(j.a==mSb){RJb(p[m].b,r,p[m].a);$o(r,VJb(d.hb));wp(r,lpc,l-k+1);++m}else if(j.a==nSb){RJb(p[q].b,r,p[q].a);$o(r,VJb(d.hb));wp(r,lpc,l-k+1);--q}else if(j.a==iSb){c=r}else if(wSb(j.a)){n=p[m];RJb(n.b,r,n.a++);$o(r,VJb(d.hb));wp(r,bqc,q-m+1);++k}else if(xSb(j.a)){n=p[m];RJb(n.b,r,n.a);$o(r,VJb(d.hb));wp(r,bqc,q-m+1);--l}}if(a.a){n=p[m];RJb(n.b,c,n.a);$o(c,VJb(dh(a.a)))}}\nt8(412,1,cnc);_.xc=function gsb(){var a,b,c;Ibb(this.a,(a=new ySb,tp((OJb(),a.hb),'cw-DockPanel'),wp(a.e,Hoc,4),vSb(a,(fUb(),_Tb)),qSb(a,new WQb(Xpc),(pSb(),mSb)),qSb(a,new WQb(Ypc),nSb),qSb(a,new WQb(Zpc),jSb),qSb(a,new WQb($pc),oSb),qSb(a,new WQb(_pc),mSb),qSb(a,new WQb(aqc),nSb),b=new WQb(\"Voici un <code>panneau de d\\xE9filement<\\/code> situ\\xE9 au centre d'un <code>panneau d'ancrage<\\/code>. Si des contenus relativement volumineux sont ins\\xE9r\\xE9s au milieu de ce panneau \\xE0 d\\xE9filement et si sa taille est d\\xE9finie, il prend la forme d'une zone dot\\xE9e d'une fonction de d\\xE9filement \\xE0 l'int\\xE9rieur de la page, sans l'utilisation d'un IFRAME.<br><br>Voici un texte encore plus obscur qui va surtout servir \\xE0 faire d\\xE9filer cet \\xE9l\\xE9ment jusqu'en bas de sa zone visible. Sinon, il vous faudra r\\xE9duire ce panneau \\xE0 une taille minuscule pour pouvoir afficher ces formidables barres de d\\xE9filement!\"),c=new iOb(b),c.hb.style[Jkc]='400px',c.hb.style[Ikc]='100px',qSb(a,c,iSb),rSb(a,'cwDockPanel'),a))};t8(875,259,Okc,ySb);_.Mb=function zSb(a){rSb(this,a)};_.dc=function ASb(a){var b;b=_Lb(this,a);if(b){a==this.a&&(this.a=null);sSb(this)}return b};var iSb,jSb,kSb,lSb,mSb,nSb,oSb;var N2=Z8b(Mkc,'DockPanel',875);t8(158,1,{},BSb);var K2=Z8b(Mkc,'DockPanel/DockLayoutConstant',158);t8(159,1,{159:1},CSb);_.c='';_.f='';var L2=Z8b(Mkc,'DockPanel/LayoutData',159);t8(266,1,{266:1},DSb);_.a=0;var M2=Z8b(Mkc,'DockPanel/TmpRow',266);Zjc(Jl)(11);\n//# sourceURL=showcase-11.js\n")