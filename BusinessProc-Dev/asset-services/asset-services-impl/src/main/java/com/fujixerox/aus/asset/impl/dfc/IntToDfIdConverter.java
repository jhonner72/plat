package com.fujixerox.aus.asset.impl.dfc;

import com.documentum.fc.common.DfId;
import com.documentum.fc.common.IDfId;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public final class IntToDfIdConverter {

    private static final int[] CHAR_TO_NUMBER = {
    /* 0 */-1, -1, -1, -1, -1, -1, -1, -1,
    /* 8 */-1, -1, -1, -1, -1, -1, -1, -1,
    /* 16 */-1, -1, -1, -1, -1, -1, -1, -1,
    /* 24 */-1, -1, -1, -1, -1, -1, -1, -1,
    /* 32 */-1, -1, -1, 48, 49, -1, -1, -1,
    /* 40 */-1, -1, -1, -1, -1, -1, -1, -1,
    /* 48 */0, 1, 2, 3, 4, 5, 6, 7,
    /* 56 */8, 9, -1, -1, -1, -1, -1, -1,
    /* 64 */-1, 32, 33, 34, 35, 36, 37, 38,
    /* 72 */39, 40, 41, 42, 43, 44, 45, 46,
    /* 80 */47, 50, 51, 52, 53, 54, 55, 56,
    /* 88 */57, 58, 59, -1, -1, -1, -1, -1,
    /* 96 */-1, 10, 11, 12, 13, 14, 15, 16,
    /* 104 */17, 18, 19, 20, 21, 22, 23, 24,
    /* 112 */25, 26, 27, 28, 29, 30, 31, 60,
    /* 120 */61, 62, 63, -1, -1, -1, -1, -1, };

    private static final char[] INT_TO_CHAR = {
    /* 0 */'0', '1', '2', '3', '4', '5', '6', '7',
    /* 8 */'8', '9', 'a', 'b', 'c', 'd', 'e', 'f',
    /* 16 */'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
    /* 24 */'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
    /* 32 */'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
    /* 40 */'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',
    /* 48 */'#', '$', 'Q', 'R', 'S', 'T', 'U', 'V',
    /* 56 */'W', 'X', 'Y', 'Z', 'w', 'x', 'y', 'z', };

    private IntToDfIdConverter() {
        super();
    }

    private static int mapCharToNumber(char character) {
        if (character > 127) {
            throw new IllegalArgumentException("Char '" + character
                    + "' exceeds 127");
        }
        int number = CHAR_TO_NUMBER[character];
        if (number == -1) {
            throw new IllegalArgumentException("No mapping for char '"
                    + character);
        }
        return number;
    }

    private static int pack(char high, char low) {
        int highInt = mapCharToNumber(high);
        int lowInt = mapCharToNumber(low);
        return ((highInt << 16) & 0x7FFF0000) | (lowInt & 0x00007FFF);
    }

    private static char[] unpack(int integer) {
        int high = (integer & 0x7FFF0000) >> 16;
        int low = integer & 0x00007FFF;
        return new char[] {INT_TO_CHAR[high], INT_TO_CHAR[low] };
    }

    public static int[] pack(IDfId objectId) {
        if (objectId == null || !objectId.isObjectId()) {
            throw new IllegalArgumentException("Wrong id: " + objectId);
        }
        int[] result = new int[4];
        String literal = objectId.getId();
        for (int i = 0; i < 4; i++) {
            result[i] = pack(literal.charAt(i * 2 + 8), literal
                    .charAt(i * 2 + 9));
        }
        return result;
    }

    public static IDfId unpack(int type, long docbaseId, int[] ints) {
        if (ints == null || ints.length != 4) {
            throw new IllegalArgumentException();
        }
        long accumulatedLowBits = 0;
        long accumulatedHighBits = 0;
        for (int i : ints) {
            for (char c : unpack(i)) {
                int newBits = mapCharToNumber(c);
                accumulatedLowBits = (accumulatedLowBits << 4) + (newBits % 16);
                accumulatedHighBits = (accumulatedHighBits << 2)
                        + (newBits / 16);
            }
        }
        long objectpart = (accumulatedHighBits << 32) + accumulatedLowBits;
        return new DfId(type, docbaseId, objectpart);
    }

}
