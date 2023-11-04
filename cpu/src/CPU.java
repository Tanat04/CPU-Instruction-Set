import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class CPU {
    private int[] registers = new int[8]; // 8 general-purpose registers
    private int totalCycles = 0; // Total clock cycles
    private int totalInstructionsProcessed = 0; // Total number of instructions processed
    private Map<String, Integer> registerMapping = new HashMap<>();
    private Scanner scanner;

    public CPU() {
        Arrays.fill(registers, 0);

        // Initialize register mapping
        registerMapping.put("r0", 0);
        registerMapping.put("r1", 1);
        registerMapping.put("r2", 2);
        registerMapping.put("r3", 3);
        registerMapping.put("r4", 4);
        registerMapping.put("r5", 5);
        registerMapping.put("r6", 6);
        registerMapping.put("r7", 7);

        scanner = new Scanner(System.in);
    }

    public void execute() {
        while (true) {
            System.out.print("Enter instruction, 'end' to stop: ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("end")) {
                break;
            }

            String[] instruction = input.split("\\s+");
            if (isValidInstruction(instruction)) {
                String opcode = instruction[0];

                // Assign cycle counts based on instruction type
                int cycleCount = 1; // Default cycle count

                if (opcode.equals("add") || opcode.equals("sub")) {
                    cycleCount = 2; // Adjust cycle count for add and sub instructions
                } else if (opcode.equals("mul") || opcode.equals("div")) {
                    cycleCount = 4; // Adjust cycle count for mul and div instructions
                }

                // Increment the total cycle count
                totalCycles += cycleCount;

                if (opcode.equals("mov")) {
                    int destReg = registerMapping.get(instruction[1]);
                    String operand = instruction[2];
                    if (operand.matches("\\d+")) {
                        registers[destReg] = Integer.parseInt(operand);
                    } else {
                        int srcReg = registerMapping.get(operand);
                        registers[destReg] = registers[srcReg];
                    }
                } else if (opcode.equals("add")) {
                    int destReg = registerMapping.get(instruction[1]);
                    int srcReg1 = registerMapping.get(instruction[2]);
                    int srcVal2;
                    if (instruction[3].matches("\\d+")) {
                        srcVal2 = Integer.parseInt(instruction[3]);
                    } else {
                        int srcReg2 = registerMapping.get(instruction[3]);
                        srcVal2 = registers[srcReg2];
                    }
                    registers[destReg] = registers[srcReg1] + srcVal2;
                } else if (opcode.equals("sub")) {
                    int destReg = registerMapping.get(instruction[1]);
                    int srcReg1 = registerMapping.get(instruction[2]);
                    int srcVal2;
                    if (instruction[3].matches("\\d+")) {
                        srcVal2 = Integer.parseInt(instruction[3]);
                        registers[destReg] = registers[srcReg1] - srcVal2;
                    } else {
                        int srcReg2 = registerMapping.get(instruction[3]);
                        registers[destReg] = registers[srcReg1] - registers[srcReg2];
                    }
                } else if (opcode.equals("mul")) {
                    int destReg = registerMapping.get(instruction[1]);
                    int srcReg1 = registerMapping.get(instruction[2]);
                    int srcVal2;
                    if (instruction[3].matches("\\d+")) {
                        srcVal2 = Integer.parseInt(instruction[3]);
                        registers[destReg] = registers[srcReg1] * srcVal2;
                    } else {
                        int srcReg2 = registerMapping.get(instruction[3]);
                        registers[destReg] = registers[srcReg1] * registers[srcReg2];
                    }
                } else if (opcode.equals("div")) {
                    int destReg = registerMapping.get(instruction[1]);
                    int srcReg1 = registerMapping.get(instruction[2]);
                    int srcVal2;

                    if (instruction[3].matches("\\d+")) {
                        srcVal2 = Integer.parseInt(instruction[3]);
                    } else {
                        int srcReg2 = registerMapping.get(instruction[3]);
                        srcVal2 = registers[srcReg2];
                    }

                    if (srcVal2 != 0) {
                        registers[destReg] = registers[srcReg1] / srcVal2;
                    } else {
                        System.out.println("Error: Division by zero!");
                    }
                } else if (opcode.equals("end")) {
                    break; // Exit the program
                }

                // Print the decoded form, instruction meaning, and encoded form
                String decodedForm = getReadableInstruction(instruction);
                String encodedForm = encodeInstruction(instruction);
                System.out.println("Decoded Form: " + decodedForm);
                System.out.println("Encoded Form (Binary): " + encodedForm);

                // pc++;

                System.out.println("Registers: " + Arrays.toString(registers));
                System.out.println();
                totalInstructionsProcessed++;
            } else {
                System.out.println("Invalid instruction. Please try again.\n");
            }
        }
        // Calculate CPI
        double cpi = (double) totalCycles / totalInstructionsProcessed;
        System.out.println("CPI: " + cpi);
        // System.out.println(totalCycles);
        // System.out.println(totalInstructionsProcessed);
        scanner.close();
    }

    private boolean isValidInstruction(String[] instruction) {
        if (instruction.length < 1) {
            return false; // An empty instruction is invalid
        }

        String opcode = instruction[0];

        // Check if the opcode is valid (add, sub, mul, div, mov, or end)
        if (!Arrays.asList("add", "sub", "mul", "div", "mov", "end").contains(opcode)) {
            return false;
        }

        // Additional validation logic specific to your instructions, if needed

        return true; // If all checks pass, the instruction is considered valid
    }

    private String getReadableInstruction(String[] instruction) {
        String opcode = instruction[0];

        if (opcode.equals("mov")) {
            String destReg = "R" + registerMapping.get(instruction[1]);
            String operand = instruction[2];
            if (operand.matches("\\d+")) {
                // Integer operand
                return "Move " + operand + " to " + destReg;
            } else {
                // Register operand
                String srcReg = "R" + registerMapping.get(operand);
                return "Move " + srcReg + " to " + destReg;
            }
        } else if (opcode.equals("add")) {
            String destReg = "R" + registerMapping.get(instruction[1]);
            String srcReg1 = "R" + registerMapping.get(instruction[2]);
            String srcReg2 = (instruction[3].matches("\\d+")) ? instruction[3]
                    : "R" + registerMapping.get(instruction[3]);
            if (srcReg2.equals("R" + registerMapping.get(instruction[1]))) {
                return "Add 1 to " + destReg;
            } else {
                return "Add " + srcReg1 + " and " + srcReg2 + " to " + destReg;
            }
        } else if (opcode.equals("sub")) {
            String destReg = "R" + registerMapping.get(instruction[1]);
            String srcReg1 = "R" + registerMapping.get(instruction[2]);
            String srcReg2 = (instruction[3].matches("\\d+")) ? instruction[3]
                    : "R" + registerMapping.get(instruction[3]);
            if (srcReg2.equals("R" + registerMapping.get(instruction[1]))) {
                return "Subtract 1 from " + destReg;
            } else {
                return "Subtract " + srcReg2 + " from " + srcReg1 + " and store in " + destReg;
            }
        } else if (opcode.equals("mul")) {
            String destReg = "R" + registerMapping.get(instruction[1]);
            String srcReg1 = "R" + registerMapping.get(instruction[2]);
            String srcReg2 = (instruction[3].matches("\\d+")) ? instruction[3]
                    : "R" + registerMapping.get(instruction[3]);
            return "Multiply " + srcReg1 + " by " + srcReg2 + " and store in " + destReg;
        } else if (opcode.equals("div")) {
            String destReg = "R" + registerMapping.get(instruction[1]);
            String srcReg1 = "R" + registerMapping.get(instruction[2]);
            String srcReg2 = (instruction[3].matches("\\d+")) ? instruction[3]
                    : "R" + registerMapping.get(instruction[3]);
            return "Divide " + srcReg1 + " by " + srcReg2 + " and store in " + destReg;
        } else if (opcode.equals("end")) {
            return "End of program";
        }
        return "";
    }

    public String encodeInstruction(String[] instruction) {
        String opcode = instruction[0];

        if (opcode.equals("mov")) {
            int destReg = registerMapping.get(instruction[1]);
            String operand = instruction[2];
            if (operand.matches("\\d+")) {
                // Integer operand
                int operandValue = Integer.parseInt(operand);
                String operandBinary = intToBinaryString(operandValue, 21); // 21-bit block for integer
                registers[destReg] = operandValue;
                return "000000 " + intToBinaryString(destReg, 5) + " " + operandBinary;
            } else {
                // Register operand
                int srcReg = registerMapping.get(operand);
                String operandBinary = intToBinaryString(srcReg, 5) + "0000000000000000"; // 5 bits for the src register
                registers[destReg] = registers[srcReg];
                return "000000 " + intToBinaryString(destReg, 5) + " " + operandBinary;
            }
        } else if (opcode.equals("add")) {
            int destReg = registerMapping.get(instruction[1]);
            int srcReg1 = registerMapping.get(instruction[2]);
            String srcVal2;
            if (instruction[3].matches("\\d+")) {
                // Integer operand
                int operandValue = Integer.parseInt(instruction[3]);
                srcVal2 = intToBinaryString(operandValue, 16);
            } else {
                // Register operand
                int srcReg2 = registerMapping.get(instruction[3]);
                srcVal2 = intToBinaryString(srcReg2, 5) + "00000000000"; // 5 bits for the src register
            }
            return "000001 " + intToBinaryString(destReg, 5) + " " + intToBinaryString(srcReg1, 5) + " " + srcVal2;
        } else if (opcode.equals("sub")) {
            int destReg = registerMapping.get(instruction[1]);
            int srcReg1 = registerMapping.get(instruction[2]);
            String srcVal2;
            if (instruction[3].matches("\\d+")) {
                // Integer operand
                int operandValue = Integer.parseInt(instruction[3]);
                srcVal2 = intToBinaryString(operandValue, 16);
            } else {
                // Register operand
                int srcReg2 = registerMapping.get(instruction[3]);
                srcVal2 = intToBinaryString(srcReg2, 5) + "00000000000"; // 5 bits for the src register
            }
            return "000010 " + intToBinaryString(destReg, 5) + " " + intToBinaryString(srcReg1, 5) + " " + srcVal2;
        } else if (opcode.equals("mul")) {
            int destReg = registerMapping.get(instruction[1]);
            int srcReg1 = registerMapping.get(instruction[2]);
            String srcVal2;
            if (instruction[3].matches("\\d+")) {
                // Integer operand
                int operandValue = Integer.parseInt(instruction[3]);
                srcVal2 = intToBinaryString(operandValue, 16);
            } else {
                // Register operand
                int srcReg2 = registerMapping.get(instruction[3]);
                srcVal2 = intToBinaryString(srcReg2, 5) + "00000000000"; // 5 bits for the src register
            }
            return "000011 " + intToBinaryString(destReg, 5) + " " + intToBinaryString(srcReg1, 5) + " " + srcVal2;
        } else if (opcode.equals("div")) {
            int destReg = registerMapping.get(instruction[1]);
            int srcReg1 = registerMapping.get(instruction[2]);
            String srcVal2;
            if (instruction[3].matches("\\d+")) {
                // Integer operand
                int operandValue = Integer.parseInt(instruction[3]);
                srcVal2 = intToBinaryString(operandValue, 16);
            } else {
                // Register operand
                int srcReg2 = registerMapping.get(instruction[3]);
                srcVal2 = intToBinaryString(srcReg2, 5) + "00000000000"; // 5 bits for the src register
            }
            return "000100 " + intToBinaryString(destReg, 5) + " " + intToBinaryString(srcReg1, 5) + " " + srcVal2;
        } else if (opcode.equals("end")) {
            return "000101" + "00000000000000000000000"; // Binary encoding for end
        }
        return "";
    }

    private String intToBinaryString(int value, int numBits) {
        String binary = Integer.toBinaryString(value);
        return String.format("%0" + numBits + "d", Integer.parseInt(binary));
    }

    public static void main(String[] args) {
        CPU cpu = new CPU();
        cpu.execute();
    }
}
