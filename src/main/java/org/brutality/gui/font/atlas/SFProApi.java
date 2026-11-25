package org.brutality.gui.font.atlas;

import org.brutality.gui.font.api.BrutalityFont;
import org.brutality.gui.font.api.GlyphFonts;
import org.brutality.gui.font.api.NameApi;
import org.brutality.gui.font.impl.SFProComponent;

public interface SFProApi {
    GlyphFonts glyphFonts = SFProApi.Entry.glyphFonts;
    NameApi nameApi = glyphFonts.computeNameApi("SF-Pro");
    NameApi nameApi2 = glyphFonts.computeNameApi("stylesicons");

    public static final class Entry {
        public static final GlyphFonts glyphFonts = SFProComponent.computeGlyphFonts();

        private Entry() {
        }
    }

    public interface Listener {
        NameApi nameApi = SFProApi.glyphFonts.computeNameApi("stylesicons");

        public static final class Entry {
            public static final BrutalityFont apiApi = SFProApi.Listener.nameApi.computeApiApi(16.0F);

            private Entry() {
            }
        }

        public static final class Entry2 {
            public static final BrutalityFont apiApi = SFProApi.Listener.nameApi.computeApiApi(24.0F);

            private Entry2() {
            }
        }

        public static final class Entry3 {
            public static final BrutalityFont apiApi = SFProApi.Listener.nameApi.computeApiApi(35.0F);

            private Entry3() {
            }
        }

        public static final class Entry4 {
            public static final BrutalityFont apiApi = SFProApi.Listener.nameApi.computeApiApi(50.0F);

            private Entry4() {
            }
        }
    }

    public interface Listener2 {
        NameApi nameApi = SFProApi.glyphFonts.computeNameApi("JetBrainsMono");

        public static final class Entry {
            public static final BrutalityFont apiApi = SFProApi.Listener2.nameApi.computeApiApi(12.0F);

            private Entry() {
            }
        }

        public static final class Entry10 {
            public static final BrutalityFont apiApi = SFProApi.Listener2.nameApi.computeApiApi(35.0F);

            private Entry10() {
            }
        }

        public static final class Entry2 {
            public static final BrutalityFont apiApi = SFProApi.Listener2.nameApi.computeApiApi(14.0F);

            private Entry2() {
            }
        }

        public static final class Entry3 {
            public static final BrutalityFont apiApi = SFProApi.Listener2.nameApi.computeApiApi(16.0F);

            private Entry3() {
            }
        }

        public static final class Entry4 {
            public static final BrutalityFont apiApi = SFProApi.Listener2.nameApi.computeApiApi(17.0F);

            private Entry4() {
            }
        }

        public static final class Entry5 {
            public static final BrutalityFont apiApi = SFProApi.Listener2.nameApi.computeApiApi(18.0F);

            private Entry5() {
            }
        }

        public static final class Entry6 {
            public static final BrutalityFont apiApi = SFProApi.Listener2.nameApi.computeApiApi(20.0F);

            private Entry6() {
            }
        }

        public static final class Entry7 {
            public static final BrutalityFont apiApi = SFProApi.Listener2.nameApi.computeApiApi(21.0F);

            private Entry7() {
            }
        }

        public static final class Entry8 {
            public static final BrutalityFont apiApi = SFProApi.Listener2.nameApi.computeApiApi(24.0F);

            private Entry8() {
            }
        }

        public static final class Entry9 {
            public static final BrutalityFont apiApi = SFProApi.Listener2.nameApi.computeApiApi(28.0F);

            private Entry9() {
            }
        }
    }

    public interface Listener3 {
        NameApi nameApi = SFProApi.glyphFonts.computeNameApi("JetBrainsMono-Bold");

        public static final class Entry {
            public static final BrutalityFont apiApi = SFProApi.Listener3.nameApi.computeApiApi(16.0F);

            private Entry() {
            }
        }

        public static final class Entry2 {
            public static final BrutalityFont apiApi = SFProApi.Listener3.nameApi.computeApiApi(18.0F);

            private Entry2() {
            }
        }

        public static final class Entry3 {
            public static final BrutalityFont apiApi = SFProApi.Listener3.nameApi.computeApiApi(20.0F);

            private Entry3() {
            }
        }

        public static final class Entry4 {
            public static final BrutalityFont apiApi = SFProApi.Listener3.nameApi.computeApiApi(24.0F);

            private Entry4() {
            }
        }

        public static final class Entry5 {
            public static final BrutalityFont apiApi = SFProApi.Listener3.nameApi.computeApiApi(28.0F);

            private Entry5() {
            }
        }
    }

    public interface Listener4 {
        NameApi nameApi = SFProApi.glyphFonts.computeNameApi("oxide");

        public static final class Entry {
            public static final BrutalityFont apiApi = SFProApi.Listener4.nameApi.computeApiApi(16.0F);

            private Entry() {
            }
        }

        public static final class Entry2 {
            public static final BrutalityFont apiApi = SFProApi.Listener4.nameApi.computeApiApi(40.0F);

            private Entry2() {
            }
        }
    }

    public interface Listener5 {
        NameApi nameApi = SFProApi.glyphFonts.computeNameApi("SF");

        public static final class Entry {
            public static final BrutalityFont apiApi = SFProApi.Listener5.nameApi.computeApiApi(14.0F);

            private Entry() {
            }
        }

        public static final class Entry10 {
            public static final BrutalityFont apiApi = SFProApi.Listener5.nameApi.computeApiApi(23.0F);

            private Entry10() {
            }
        }

        public static final class Entry11 {
            public static final BrutalityFont apiApi = SFProApi.Listener5.nameApi.computeApiApi(24.0F);

            private Entry11() {
            }
        }

        public static final class Entry12 {
            public static final BrutalityFont apiApi = SFProApi.Listener5.nameApi.computeApiApi(25.0F);

            private Entry12() {
            }
        }

        public static final class Entry13 {
            public static final BrutalityFont apiApi = SFProApi.Listener5.nameApi.computeApiApi(26.0F);

            private Entry13() {
            }
        }

        public static final class Entry14 {
            public static final BrutalityFont apiApi = SFProApi.Listener5.nameApi.computeApiApi(27.0F);

            private Entry14() {
            }
        }

        public static final class Entry15 {
            public static final BrutalityFont apiApi = SFProApi.Listener5.nameApi.computeApiApi(28.0F);

            private Entry15() {
            }
        }

        public static final class Entry16 {
            public static final BrutalityFont apiApi = SFProApi.Listener5.nameApi.computeApiApi(29.0F);

            private Entry16() {
            }
        }

        public static final class Entry17 {
            public static final BrutalityFont apiApi = SFProApi.Listener5.nameApi.computeApiApi(30.0F);

            private Entry17() {
            }
        }

        public static final class Entry18 {
            public static final BrutalityFont apiApi = SFProApi.Listener5.nameApi.computeApiApi(31.0F);

            private Entry18() {
            }
        }

        public static final class Entry19 {
            public static final BrutalityFont apiApi = SFProApi.Listener5.nameApi.computeApiApi(45.0F);

            private Entry19() {
            }
        }

        public static final class Entry2 {
            public static final BrutalityFont apiApi = SFProApi.Listener5.nameApi.computeApiApi(15.0F);

            private Entry2() {
            }
        }

        public static final class Entry3 {
            public static final BrutalityFont apiApi = SFProApi.Listener5.nameApi.computeApiApi(16.0F);

            private Entry3() {
            }
        }

        public static final class Entry4 {
            public static final BrutalityFont apiApi = SFProApi.Listener5.nameApi.computeApiApi(17.0F);

            private Entry4() {
            }
        }

        public static final class Entry5 {
            public static final BrutalityFont apiApi = SFProApi.Listener5.nameApi.computeApiApi(18.0F);

            private Entry5() {
            }
        }

        public static final class Entry6 {
            public static final BrutalityFont apiApi = SFProApi.Listener5.nameApi.computeApiApi(19.0F);

            private Entry6() {
            }
        }

        public static final class Entry7 {
            public static final BrutalityFont apiApi = SFProApi.Listener5.nameApi.computeApiApi(20.0F);

            private Entry7() {
            }
        }

        public static final class Entry8 {
            public static final BrutalityFont apiApi = SFProApi.Listener5.nameApi.computeApiApi(21.0F);

            private Entry8() {
            }
        }

        public static final class Entry9 {
            public static final BrutalityFont apiApi = SFProApi.Listener5.nameApi.computeApiApi(22.0F);

            private Entry9() {
            }
        }
    }

    public interface Listener6 {
        NameApi nameApi = SFProApi.glyphFonts.computeNameApi("SFBOLD");

        public static final class Entry {
            public static final BrutalityFont apiApi = SFProApi.Listener6.nameApi.computeApiApi(12.0F);

            private Entry() {
            }
        }

        public static final class Entry2 {
            public static final BrutalityFont apiApi = SFProApi.Listener6.nameApi.computeApiApi(16.0F);

            private Entry2() {
            }
        }

        public static final class Entry3 {
            public static final BrutalityFont apiApi = SFProApi.Listener6.nameApi.computeApiApi(18.0F);

            private Entry3() {
            }
        }

        public static final class Entry4 {
            public static final BrutalityFont apiApi = SFProApi.Listener6.nameApi.computeApiApi(20.0F);

            private Entry4() {
            }
        }

        public static final class Entry5 {
            public static final BrutalityFont apiApi = SFProApi.Listener6.nameApi.computeApiApi(22.0F);

            private Entry5() {
            }
        }

        public static final class Entry6 {
            public static final BrutalityFont apiApi = SFProApi.Listener6.nameApi.computeApiApi(26.0F);

            private Entry6() {
            }
        }

        public static final class Entry7 {
            public static final BrutalityFont apiApi = SFProApi.Listener6.nameApi.computeApiApi(28.0F);

            private Entry7() {
            }
        }

        public static final class Entry8 {
            public static final BrutalityFont apiApi = SFProApi.Listener6.nameApi.computeApiApi(35.0F);

            private Entry8() {
            }
        }
    }

    public interface Listener7 {
        NameApi nameApi = SFProApi.glyphFonts.computeNameApi("SFREGULAR");

        public static final class Entry {
            public static final BrutalityFont apiApi = SFProApi.Listener7.nameApi.computeApiApi(10.0F);

            private Entry() {
            }
        }

        public static final class Entry2 {
            public static final BrutalityFont apiApi = SFProApi.Listener7.nameApi.computeApiApi(12.0F);

            private Entry2() {
            }
        }

        public static final class Entry3 {
            public static final BrutalityFont apiApi = SFProApi.Listener7.nameApi.computeApiApi(16.0F);

            private Entry3() {
            }
        }

        public static final class Entry4 {
            public static final BrutalityFont apiApi = SFProApi.Listener7.nameApi.computeApiApi(17.0F);

            private Entry4() {
            }
        }

        public static final class Entry5 {
            public static final BrutalityFont apiApi = SFProApi.Listener7.nameApi.computeApiApi(18.0F);

            private Entry5() {
            }
        }

        public static final class Entry6 {
            public static final BrutalityFont apiApi = SFProApi.Listener7.nameApi.computeApiApi(19.0F);

            private Entry6() {
            }
        }

        public static final class Entry7 {
            public static final BrutalityFont apiApi = SFProApi.Listener7.nameApi.computeApiApi(20.0F);

            private Entry7() {
            }
        }

        public static final class Entry8 {
            public static final BrutalityFont apiApi = SFProApi.Listener7.nameApi.computeApiApi(21.0F);

            private Entry8() {
            }
        }

        public static final class Entry9 {
            public static final BrutalityFont apiApi = SFProApi.Listener7.nameApi.computeApiApi(28.0F);

            private Entry9() {
            }
        }
    }

    public interface Listener8 {
        NameApi nameApi = SFProApi.glyphFonts.computeNameApi("SF-Pro");

        public static final class Entry {
            public static final BrutalityFont apiApi = SFProApi.Listener8.nameApi.computeApiApi(21.0F);

            private Entry() {
            }
        }
    }
}
