/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wrmsr.nativity;

import jnr.ffi.Pointer;
import jnr.ffi.Runtime;
import jnr.ffi.StructLayout;
import jnr.ffi.provider.jffi.ByteBufferMemoryIO;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

public class Elf
{

    public static final int EI_NIDENT = 16;

    public static abstract class ElfStructLayout
            extends StructLayout
    {

        protected ElfStructLayout(Runtime runtime)
        {
            super(runtime);
        }

        protected ElfStructLayout(Runtime runtime, int structSize)
        {
            super(runtime, structSize);
        }

        public class Elf32_Half
                extends Unsigned16
        {

            public Elf32_Half()
            {
            }

            public Elf32_Half(Offset offset)
            {
                super(offset);
            }
        }

        public class Elf64_Half
                extends Unsigned16
        {

            public Elf64_Half()
            {
            }

            public Elf64_Half(Offset offset)
            {
                super(offset);
            }
        }

        public class Elf32_Word
                extends Unsigned32
        {

            public Elf32_Word()
            {
            }

            public Elf32_Word(Offset offset)
            {
                super(offset);
            }
        }

        public class Elf32_Sword
                extends Signed32
        {

            public Elf32_Sword()
            {
            }

            public Elf32_Sword(Offset offset)
            {
                super(offset);
            }
        }

        public class Elf64_Word
                extends Unsigned32
        {

            public Elf64_Word()
            {
            }

            public Elf64_Word(Offset offset)
            {
                super(offset);
            }
        }

        public class Elf64_Sword
                extends Signed32
        {

            public Elf64_Sword()
            {
            }

            public Elf64_Sword(Offset offset)
            {
                super(offset);
            }
        }

        public class Elf32_Xword
                extends Unsigned64
        {

            public Elf32_Xword()
            {
            }

            public Elf32_Xword(Offset offset)
            {
                super(offset);
            }
        }

        public class Elf32_Sxword
                extends Signed64
        {

            public Elf32_Sxword()
            {
            }

            public Elf32_Sxword(Offset offset)
            {
                super(offset);
            }
        }

        public class Elf64_Xword
                extends Unsigned64
        {

            public Elf64_Xword()
            {
            }

            public Elf64_Xword(Offset offset)
            {
                super(offset);
            }
        }

        public class Elf64_Sxword
                extends Signed64
        {

            public Elf64_Sxword()
            {
            }

            public Elf64_Sxword(Offset offset)
            {
                super(offset);
            }
        }

        public class Elf32_Addr
                extends Unsigned32
        {

            public Elf32_Addr()
            {
            }

            public Elf32_Addr(Offset offset)
            {
                super(offset);
            }
        }

        public class Elf64_Addr
                extends Unsigned64
        {

            public Elf64_Addr()
            {
            }

            public Elf64_Addr(Offset offset)
            {
                super(offset);
            }
        }

        public class Elf32_Off
                extends Unsigned32
        {

            public Elf32_Off()
            {
            }

            public Elf32_Off(Offset offset)
            {
                super(offset);
            }
        }

        public class Elf64_Off
                extends Unsigned64
        {

            public Elf64_Off()
            {
            }

            public Elf64_Off(Offset offset)
            {
                super(offset);
            }
        }

        public class Elf32_Section
                extends Unsigned16
        {

            public Elf32_Section()
            {
            }

            public Elf32_Section(Offset offset)
            {
                super(offset);
            }
        }

        public class Elf64_Section
                extends Unsigned16
        {

            public Elf64_Section()
            {
            }

            public Elf64_Section(Offset offset)
            {
                super(offset);
            }
        }

        public class Elf32_Versym
                extends Elf32_Half
        {

            public Elf32_Versym()
            {
            }

            public Elf32_Versym(Offset offset)
            {
                super(offset);
            }
        }

        public class Elf64_Versym
                extends Elf64_Half
        {

            public Elf64_Versym()
            {
            }

            public Elf64_Versym(Offset offset)
            {
                super(offset);
            }
        }
    }

    public static class Elf32_Ehdr
            extends ElfStructLayout
    {

        public Elf32_Ehdr(jnr.ffi.Runtime runtime)
        {
            super(runtime);
        }

        public Elf32_Ehdr(Runtime runtime, int structSize)
        {
            super(runtime, structSize);
        }

        public final Unsigned8[] e_ident = array(new Unsigned8[EI_NIDENT]);	/* Magic number and  other info */
        public final Elf32_Half e_type = new Elf32_Half();			/* Object file type */
        public final Elf32_Half e_machine = new Elf32_Half();		/* Architecture */
        public final Elf32_Word e_version = new Elf32_Word();		/* Object file version */
        public final Elf32_Addr e_entry = new Elf32_Addr();		/* Entry point virtual address */
        public final Elf32_Off e_phoff = new Elf32_Off();		/* Program header table file offset */
        public final Elf32_Off e_shoff = new Elf32_Off();		/* Section header table file offset */
        public final Elf32_Word e_flags = new Elf32_Word();		/* Processor-specific flags */
        public final Elf32_Half e_ehsize = new Elf32_Half();		/* ELF header size in bytes */
        public final Elf32_Half e_phentsize = new Elf32_Half();		/* Program header table entry size */
        public final Elf32_Half e_phnum = new Elf32_Half();		/* Program header table entry count */
        public final Elf32_Half e_shentsize = new Elf32_Half();		/* Section header table entry size */
        public final Elf32_Half e_shnum = new Elf32_Half();		/* Section header table entry count */
        public final Elf32_Half e_shstrndx = new Elf32_Half();		/* Section header string table index */
    }

    public static class Elf64_Ehdr
            extends ElfStructLayout
    {

        public Elf64_Ehdr(Runtime runtime)
        {
            super(runtime);
        }

        public Elf64_Ehdr(Runtime runtime, int structSize)
        {
            super(runtime, structSize);
        }

        public final Unsigned8[] e_ident = array(new Unsigned8[EI_NIDENT]);	/* Magic number and  other info */
        public final Elf64_Half e_type = new Elf64_Half();			/* Object file type */
        public final Elf64_Half e_machine = new Elf64_Half();		/* Architecture */
        public final Elf64_Word e_version = new Elf64_Word();		/* Object file version */
        public final Elf64_Addr e_entry = new Elf64_Addr();		/* Entry point virtual address */
        public final Elf64_Off e_phoff = new Elf64_Off();		/* Program header table file offset */
        public final Elf64_Off e_shoff = new Elf64_Off();		/* Section header table file offset */
        public final Elf64_Word e_flags = new Elf64_Word();		/* Processor-specific flags */
        public final Elf64_Half e_ehsize = new Elf64_Half();		/* ELF header size in bytes */
        public final Elf64_Half e_phentsize = new Elf64_Half();		/* Program header table entry size */
        public final Elf64_Half e_phnum = new Elf64_Half();		/* Program header table entry count */
        public final Elf64_Half e_shentsize = new Elf64_Half();		/* Section header table entry size */
        public final Elf64_Half e_shnum = new Elf64_Half();		/* Section header table entry count */
        public final Elf64_Half e_shstrndx = new Elf64_Half();		/* Section header string table index */
    }

    public static class Elf32_Shdr
            extends ElfStructLayout
    {

        public Elf32_Shdr(Runtime runtime)
        {
            super(runtime);
        }

        public Elf32_Shdr(Runtime runtime, int structSize)
        {
            super(runtime, structSize);
        }

        public final Elf32_Word sh_name = new Elf32_Word();		/* Section name (string tbl index) */
        public final Elf32_Word sh_type = new Elf32_Word();		/* Section type */
        public final Elf32_Word sh_flags = new Elf32_Word();		/* Section flags */
        public final Elf32_Addr sh_addr = new Elf32_Addr();		/* Section virtual addr at execution */
        public final Elf32_Off sh_offset = new Elf32_Off();		/* Section file offset */
        public final Elf32_Word sh_size = new Elf32_Word();		/* Section size in bytes */
        public final Elf32_Word sh_link = new Elf32_Word();		/* Link to another section */
        public final Elf32_Word sh_info = new Elf32_Word();		/* Additional section information */
        public final Elf32_Word sh_addralign = new Elf32_Word();		/* Section alignment */
        public final Elf32_Word sh_entsize = new Elf32_Word();		/* Entry size if section holds table */
    }

    public static class Elf64_Shdr
            extends ElfStructLayout
    {

        public Elf64_Shdr(Runtime runtime)
        {
            super(runtime);
        }

        public Elf64_Shdr(Runtime runtime, int structSize)
        {
            super(runtime, structSize);
        }

        public final Elf64_Word sh_name = new Elf64_Word();		/* Section name (string tbl index) */
        public final Elf64_Word sh_type = new Elf64_Word();		/* Section type */
        public final Elf64_Xword sh_flags = new Elf64_Xword();		/* Section flags */
        public final Elf64_Addr sh_addr = new Elf64_Addr();		/* Section virtual addr at execution */
        public final Elf64_Off sh_offset = new Elf64_Off();		/* Section file offset */
        public final Elf64_Xword sh_size = new Elf64_Xword();		/* Section size in bytes */
        public final Elf64_Word sh_link = new Elf64_Word();		/* Link to another section */
        public final Elf64_Word sh_info = new Elf64_Word();		/* Additional section information */
        public final Elf64_Xword sh_addralign = new Elf64_Xword();		/* Section alignment */
        public final Elf64_Xword sh_entsize = new Elf64_Xword();		/* Entry size if section holds table */
    }

    public static class Elf32_Sym
            extends ElfStructLayout
    {

        public Elf32_Sym(Runtime runtime)
        {
            super(runtime);
        }

        public Elf32_Sym(Runtime runtime, int structSize)
        {
            super(runtime, structSize);
        }

        public final Elf32_Word st_name = new Elf32_Word();		/* Symbol name (string tbl index) */
        public final Elf32_Addr st_value = new Elf32_Addr();		/* Symbol value */
        public final Elf32_Word st_size = new Elf32_Word();		/* Symbol size */
        public final Unsigned8 st_info = new Unsigned8();		/* Symbol type and binding */
        public final Unsigned8 st_other = new Unsigned8();		/* Symbol visibility */
        public final Elf32_Section st_shndx = new Elf32_Section();		/* Section index */
    }

    public static class Elf64_Sym
            extends ElfStructLayout
    {

        public Elf64_Sym(Runtime runtime)
        {
            super(runtime);
        }

        public Elf64_Sym(Runtime runtime, int structSize)
        {
            super(runtime, structSize);
        }

        public final Elf64_Word st_name = new Elf64_Word();		/* Symbol name (string tbl index) */
        public final Unsigned8 st_info = new Unsigned8();		/* Symbol type and binding */
        public final Unsigned8 st_other = new Unsigned8();		/* Symbol visibility */
        public final Elf64_Section st_shndx = new Elf64_Section();		/* Section index */
        public final Elf64_Addr st_value = new Elf64_Addr();		/* Symbol value */
        public final Elf64_Xword st_size = new Elf64_Xword();		/* Symbol size */
    }

    public static class Elf32_Syminfo
            extends ElfStructLayout
    {

        public Elf32_Syminfo(Runtime runtime)
        {
            super(runtime);
        }

        public Elf32_Syminfo(Runtime runtime, int structSize)
        {
            super(runtime, structSize);
        }

        public final Elf32_Half si_boundto = new Elf32_Half();		/* Direct bindings, symbol bound to */
        public final Elf32_Half si_flags = new Elf32_Half();			/* Per symbol flags */
    }

    public static class Elf64_Syminfo
            extends ElfStructLayout
    {

        public Elf64_Syminfo(Runtime runtime)
        {
            super(runtime);
        }

        public Elf64_Syminfo(Runtime runtime, int structSize)
        {
            super(runtime, structSize);
        }

        public final Elf64_Half si_boundto = new Elf64_Half();		/* Direct bindings, symbol bound to */
        public final Elf64_Half si_flags = new Elf64_Half();			/* Per symbol flags */
    }

	/* Relocation table entry without addend (in section of type SHT_REL).  */

    public static class Elf32_Rel
            extends ElfStructLayout
    {

        public Elf32_Rel(Runtime runtime)
        {
            super(runtime);
        }

        public Elf32_Rel(Runtime runtime, int structSize)
        {
            super(runtime, structSize);
        }

        public final Elf32_Addr r_offset = new Elf32_Addr();		/* Address */
        public final Elf32_Word r_info = new Elf32_Word();			/* Relocation type and symbol index */
    }

	/* I have seen two different definitions of the Elf64_Rel and
       Elf64_Rela structures, so we'll leave them out until Novell (or
	   whoever) gets their act together.  */
    /* The following, at least, is used on Sparc v9, MIPS, and Alpha.  */

    public static class Elf64_Rel
            extends ElfStructLayout
    {

        public Elf64_Rel(Runtime runtime)
        {
            super(runtime);
        }

        public Elf64_Rel(Runtime runtime, int structSize)
        {
            super(runtime, structSize);
        }

        public final Elf64_Addr r_offset = new Elf64_Addr();		/* Address */
        public final Elf64_Xword r_info = new Elf64_Xword();			/* Relocation type and symbol index */
    }

	/* Relocation table entry with addend (in section of type SHT_RELA).  */

    public static class Elf32_Rela
            extends ElfStructLayout
    {

        public Elf32_Rela(Runtime runtime)
        {
            super(runtime);
        }

        public Elf32_Rela(Runtime runtime, int structSize)
        {
            super(runtime, structSize);
        }

        public final Elf32_Addr r_offset = new Elf32_Addr();		/* Address */
        public final Elf32_Word r_info = new Elf32_Word();			/* Relocation type and symbol index */
        public final Elf32_Sword r_addend = new Elf32_Sword();		/* Addend */
    }

    public static class Elf64_Rela
            extends ElfStructLayout
    {

        public Elf64_Rela(Runtime runtime)
        {
            super(runtime);
        }

        public Elf64_Rela(Runtime runtime, int structSize)
        {
            super(runtime, structSize);
        }

        public final Elf64_Addr r_offset = new Elf64_Addr();		/* Address */
        public final Elf64_Xword r_info = new Elf64_Xword();			/* Relocation type and symbol index */
        public final Elf64_Sxword r_addend = new Elf64_Sxword();		/* Addend */
    }

    public static class Elf32_Phdr
            extends ElfStructLayout
    {

        public Elf32_Phdr(Runtime runtime)
        {
            super(runtime);
        }

        public Elf32_Phdr(Runtime runtime, int structSize)
        {
            super(runtime, structSize);
        }

        public final Elf32_Word p_type = new Elf32_Word();			/* Segment type */
        public final Elf32_Off p_offset = new Elf32_Off();		/* Segment file offset */
        public final Elf32_Addr p_vaddr = new Elf32_Addr();		/* Segment virtual address */
        public final Elf32_Addr p_paddr = new Elf32_Addr();		/* Segment physical address */
        public final Elf32_Word p_filesz = new Elf32_Word();		/* Segment size in file */
        public final Elf32_Word p_memsz = new Elf32_Word();		/* Segment size in memory */
        public final Elf32_Word p_flags = new Elf32_Word();		/* Segment flags */
        public final Elf32_Word p_align = new Elf32_Word();		/* Segment alignment */
    }

    public static class Elf64_Phdr
            extends ElfStructLayout
    {

        public Elf64_Phdr(Runtime runtime)
        {
            super(runtime);
        }

        public Elf64_Phdr(Runtime runtime, int structSize)
        {
            super(runtime, structSize);
        }

        public final Elf64_Word p_type = new Elf64_Word();			/* Segment type */
        public final Elf64_Word p_flags = new Elf64_Word();		/* Segment flags */
        public final Elf64_Off p_offset = new Elf64_Off();		/* Segment file offset */
        public final Elf64_Addr p_vaddr = new Elf64_Addr();		/* Segment virtual address */
        public final Elf64_Addr p_paddr = new Elf64_Addr();		/* Segment physical address */
        public final Elf64_Xword p_filesz = new Elf64_Xword();		/* Segment size in file */
        public final Elf64_Xword p_memsz = new Elf64_Xword();		/* Segment size in memory */
        public final Elf64_Xword p_align = new Elf64_Xword();		/* Segment alignment */
    }

    public static class Elf32_Nhdr
            extends ElfStructLayout
    {

        public Elf32_Nhdr(Runtime runtime)
        {
            super(runtime);
        }

        public Elf32_Nhdr(Runtime runtime, int structSize)
        {
            super(runtime, structSize);
        }

        public final Elf32_Word n_namesz = new Elf32_Word();			/* Length of the note's name.  */
        public final Elf32_Word n_descsz = new Elf32_Word();			/* Length of the note's descriptor.  */
        public final Elf32_Word n_type = new Elf32_Word();			/* Type of the note.  */
    }

    public static class Elf64_Nhdr
            extends ElfStructLayout
    {

        public Elf64_Nhdr(Runtime runtime)
        {
            super(runtime);
        }

        public Elf64_Nhdr(Runtime runtime, int structSize)
        {
            super(runtime, structSize);
        }

        public final Elf64_Word n_namesz = new Elf64_Word();			/* Length of the note's name.  */
        public final Elf64_Word n_descsz = new Elf64_Word();			/* Length of the note's descriptor.  */
        public final Elf64_Word n_type = new Elf64_Word();			/* Type of the note.  */
    }

    public static class Elf32_Move
            extends ElfStructLayout
    {

        public Elf32_Move(Runtime runtime)
        {
            super(runtime);
        }

        public Elf32_Move(Runtime runtime, int structSize)
        {
            super(runtime, structSize);
        }

        public final Elf32_Xword m_value = new Elf32_Xword();		/* Symbol value.  */
        public final Elf32_Word m_info = new Elf32_Word();		/* Size and index.  */
        public final Elf32_Word m_poffset = new Elf32_Word();		/* Symbol offset.  */
        public final Elf32_Half m_repeat = new Elf32_Half();		/* Repeat count.  */
        public final Elf32_Half m_stride = new Elf32_Half();		/* Stride info.  */
    }

    public static class Elf64_Move
            extends ElfStructLayout
    {

        public Elf64_Move(Runtime runtime)
        {
            super(runtime);
        }

        public Elf64_Move(Runtime runtime, int structSize)
        {
            super(runtime, structSize);
        }

        public final Elf64_Xword m_value = new Elf64_Xword();		/* Symbol value.  */
        public final Elf64_Xword m_info = new Elf64_Xword();		/* Size and index.  */
        public final Elf64_Xword m_poffset = new Elf64_Xword();	/* Symbol offset.  */
        public final Elf64_Half m_repeat = new Elf64_Half();		/* Repeat count.  */
        public final Elf64_Half m_stride = new Elf64_Half();		/* Stride info.  */
    }

    public static class Elf_Options_Hw
            extends ElfStructLayout
    {

        public Elf_Options_Hw(Runtime runtime)
        {
            super(runtime);
        }

        public Elf_Options_Hw(Runtime runtime, int structSize)
        {
            super(runtime, structSize);
        }

        public final Elf32_Word hwp_flags1 = new Elf32_Word();	/* Extra flags.  */
        public final Elf32_Word hwp_flags2 = new Elf32_Word();	/* Extra flags.  */
    }

    public static class Elf32_Lib
            extends ElfStructLayout
    {

        public Elf32_Lib(Runtime runtime)
        {
            super(runtime);
        }

        public Elf32_Lib(Runtime runtime, int structSize)
        {
            super(runtime, structSize);
        }

        public final Elf32_Word l_name = new Elf32_Word();		/* Name (string table index) */
        public final Elf32_Word l_time_stamp = new Elf32_Word();	/* Timestamp */
        public final Elf32_Word l_checksum = new Elf32_Word();	/* Checksum */
        public final Elf32_Word l_version = new Elf32_Word();		/* Interface version */
        public final Elf32_Word l_flags = new Elf32_Word();		/* Flags */
    }

    public static class Elf64_Lib
            extends ElfStructLayout
    {

        public Elf64_Lib(Runtime runtime)
        {
            super(runtime);
        }

        public Elf64_Lib(Runtime runtime, int structSize)
        {
            super(runtime, structSize);
        }

        public final Elf64_Word l_name = new Elf64_Word();		/* Name (string table index) */
        public final Elf64_Word l_time_stamp = new Elf64_Word();	/* Timestamp */
        public final Elf64_Word l_checksum = new Elf64_Word();	/* Checksum */
        public final Elf64_Word l_version = new Elf64_Word();		/* Interface version */
        public final Elf64_Word l_flags = new Elf64_Word();		/* Flags */
    }

    public static class Elf32_Dyn
            extends ElfStructLayout
    {

        public Elf32_Dyn(Runtime runtime)
        {
            super(runtime);
        }

        public Elf32_Dyn(Runtime runtime, int structSize)
        {
            super(runtime, structSize);
        }

        public final Elf32_Sword d_tag = new Elf32_Sword();
        // union{
        public final Elf32_Sword d_val = new Elf32_Sword();
        //	public final Elf32_Addr	d_ptr = new Elf32_Addr();
        // } d_un;
    }

    public static class Elf64_Dyn
            extends ElfStructLayout
    {

        public Elf64_Dyn(Runtime runtime)
        {
            super(runtime);
        }

        public Elf64_Dyn(Runtime runtime, int structSize)
        {
            super(runtime, structSize);
        }

        public final Elf64_Sxword d_tag = new Elf64_Sxword();		/* entry tag value */
        // union {
        public final Elf64_Xword d_va = new Elf64_Xword();
        //	public final  Elf64_Addr d_ptr = new Elf64_Addr();
        // } d_un;
    }

    public static class Elf32_Verdef
            extends ElfStructLayout
    {

        public Elf32_Verdef(Runtime runtime)
        {
            super(runtime);
        }

        public Elf32_Verdef(Runtime runtime, int structSize)
        {
            super(runtime, structSize);
        }

        public final Elf32_Half vd_version = new Elf32_Half();		/* Version revision */
        public final Elf32_Half vd_flags = new Elf32_Half();		/* Version information */
        public final Elf32_Half vd_ndx = new Elf32_Half();			/* Version Index */
        public final Elf32_Half vd_cnt = new Elf32_Half();			/* Number of associated aux entries */
        public final Elf32_Word vd_hash = new Elf32_Word();		/* Version name hash value */
        public final Elf32_Word vd_aux = new Elf32_Word();			/* Offset in bytes to verdaux array */
        public final Elf32_Word vd_next = new Elf32_Word();		/* Offset in bytes to next verdef entry */
    }

    public static class Elf64_Verdef
            extends ElfStructLayout
    {

        public Elf64_Verdef(Runtime runtime)
        {
            super(runtime);
        }

        public Elf64_Verdef(Runtime runtime, int structSize)
        {
            super(runtime, structSize);
        }

        public final Elf64_Half vd_version = new Elf64_Half();		/* Version revision */
        public final Elf64_Half vd_flags = new Elf64_Half();		/* Version information */
        public final Elf64_Half vd_ndx = new Elf64_Half();			/* Version Index */
        public final Elf64_Half vd_cnt = new Elf64_Half();			/* Number of associated aux entries */
        public final Elf64_Word vd_hash = new Elf64_Word();		/* Version name hash value */
        public final Elf64_Word vd_aux = new Elf64_Word();			/* Offset in bytes to verdaux array */
        public final Elf64_Word vd_next = new Elf64_Word();		/* Offset in bytes to next verdef entry */
    }

    public static class Elf32_Verdaux
            extends ElfStructLayout
    {

        public Elf32_Verdaux(Runtime runtime)
        {
            super(runtime);
        }

        public Elf32_Verdaux(Runtime runtime, int structSize)
        {
            super(runtime, structSize);
        }

        public final Elf32_Word vda_name = new Elf32_Word();		/* Version or dependency names */
        public final Elf32_Word vda_next = new Elf32_Word();		/* Offset in bytes to next verdaux entry */
    }

    public static class Elf64_Verdaux
            extends ElfStructLayout
    {

        public Elf64_Verdaux(Runtime runtime)
        {
            super(runtime);
        }

        public Elf64_Verdaux(Runtime runtime, int structSize)
        {
            super(runtime, structSize);
        }

        public final Elf64_Word vda_name = new Elf64_Word();		/* Version or dependency names */
        public final Elf64_Word vda_next = new Elf64_Word();		/* Offset in bytes to next verdaux entry */
    }

    public static class Elf32_Verneed
            extends ElfStructLayout
    {

        public Elf32_Verneed(Runtime runtime)
        {
            super(runtime);
        }

        public Elf32_Verneed(Runtime runtime, int structSize)
        {
            super(runtime, structSize);
        }

        public final Elf32_Half vn_version = new Elf32_Half();		/* Version of structure */
        public final Elf32_Half vn_cnt = new Elf32_Half();			/* Number of associated aux entries */
        public final Elf32_Word vn_file = new Elf32_Word();		/* Offset of filename for this dependency */
        public final Elf32_Word vn_aux = new Elf32_Word();			/* Offset in bytes to vernaux array */
        public final Elf32_Word vn_next = new Elf32_Word();		/* Offset in bytes to next verneed entry */
    }

    public static class Elf64_Verneed
            extends ElfStructLayout
    {

        public Elf64_Verneed(Runtime runtime)
        {
            super(runtime);
        }

        public Elf64_Verneed(Runtime runtime, int structSize)
        {
            super(runtime, structSize);
        }

        public final Elf64_Half vn_version = new Elf64_Half();		/* Version of structure */
        public final Elf64_Half vn_cnt = new Elf64_Half();			/* Number of associated aux entries */
        public final Elf64_Word vn_file = new Elf64_Word();		/* Offset of filename for this dependency */
        public final Elf64_Word vn_aux = new Elf64_Word();			/* Offset in bytes to vernaux array */
        public final Elf64_Word vn_next = new Elf64_Word();		/* Offset in bytes to next verneed entry */
    }

    public static class Elf32_Vernaux
            extends ElfStructLayout
    {

        public Elf32_Vernaux(Runtime runtime)
        {
            super(runtime);
        }

        public Elf32_Vernaux(Runtime runtime, int structSize)
        {
            super(runtime, structSize);
        }

        public final Elf32_Word vna_hash = new Elf32_Word();		/* Hash value of dependency name */
        public final Elf32_Half vna_flags = new Elf32_Half();		/* Dependency specific information */
        public final Elf32_Half vna_other = new Elf32_Half();		/* Unused */
        public final Elf32_Word vna_name = new Elf32_Word();		/* Dependency name string offset */
        public final Elf32_Word vna_next = new Elf32_Word();		/* Offset in bytes to next vernaux entry */
    }

    public static class Elf64_Vernaux
            extends ElfStructLayout
    {

        public Elf64_Vernaux(Runtime runtime)
        {
            super(runtime);
        }

        public Elf64_Vernaux(Runtime runtime, int structSize)
        {
            super(runtime, structSize);
        }

        public final Elf64_Word vna_hash = new Elf64_Word();		/* Hash value of dependency name */
        public final Elf64_Half vna_flags = new Elf64_Half();		/* Dependency specific information */
        public final Elf64_Half vna_other = new Elf64_Half();		/* Unused */
        public final Elf64_Word vna_name = new Elf64_Word();		/* Dependency name string offset */
        public final Elf64_Word vna_next = new Elf64_Word();		/* Offset in bytes to next vernaux entry */
    }

    public static final int EI_MAG0 = 0;		/* File identification byte 0 index */
    public static final int ELFMAG0 = 0x7f;		/* Magic number byte 0 */

    public static final int EI_MAG1 = 1;		/* File identification byte 1 index */
    public static final int ELFMAG1 = 'E';		/* Magic number byte 1 */

    public static final int EI_MAG2 = 2;		/* File identification byte 2 index */
    public static final int ELFMAG2 = 'L';		/* Magic number byte 2 */

    public static final int EI_MAG3 = 3;		/* File identification byte 3 index */
    public static final int ELFMAG3 = 'F';		/* Magic number byte 3 */

    /* Conglomeration of the identification bytes, for easy testing as a word.  */
    // public static final int	ELFMAG =		"\177ELF";
    public static final int SELFMAG = 4;

    public static final int EI_CLASS = 4;		/* File class byte index */
    public static final int ELFCLASSNONE = 0;		/* Invalid class */
    public static final int ELFCLASS32 = 1;		/* 32-bit objects */
    public static final int ELFCLASS64 = 2;		/* 64-bit objects */
    public static final int ELFCLASSNUM = 3;

    public static final int EI_DATA = 5;		/* Data encoding byte index */
    public static final int ELFDATANONE = 0;		/* Invalid data encoding */
    public static final int ELFDATA2LSB = 1;		/* 2's complement, little endian */
    public static final int ELFDATA2MSB = 2;		/* 2's complement, big endian */
    public static final int ELFDATANUM = 3;

    public static final int EI_VERSION = 6;		/* File version byte index */
    /* Value must be EV_CURRENT */

    public static final int EI_OSABI = 7;		/* OS ABI identification */
    public static final int ELFOSABI_NONE = 0;	/* UNIX System V ABI */
    public static final int ELFOSABI_SYSV = 0;	/* Alias.  */
    public static final int ELFOSABI_HPUX = 1;	/* HP-UX */
    public static final int ELFOSABI_NETBSD = 2;	/* NetBSD.  */
    public static final int ELFOSABI_LINUX = 3;	/* Linux.  */
    public static final int ELFOSABI_SOLARIS = 6;	/* Sun Solaris.  */
    public static final int ELFOSABI_AIX = 7;	/* IBM AIX.  */
    public static final int ELFOSABI_IRIX = 8;	/* SGI Irix.  */
    public static final int ELFOSABI_FREEBSD = 9;	/* FreeBSD.  */
    public static final int ELFOSABI_TRU64 = 10;	/* Compaq TRU64 UNIX.  */
    public static final int ELFOSABI_MODESTO = 11;	/* Novell Modesto.  */
    public static final int ELFOSABI_OPENBSD = 12;	/* OpenBSD.  */
    public static final int ELFOSABI_ARM = 97;	/* ARM */
    public static final int ELFOSABI_STANDALONE = 255;	/* Standalone (embedded) application */

    public static final int EI_ABIVERSION = 8;		/* ABI version */

    public static final int EI_PAD = 9;		/* Byte index of padding bytes */

    /* Legal values for e_type (object file type).  */

    public static final int ET_NONE = 0;		/* No file type */
    public static final int ET_REL = 1;		/* Relocatable file */
    public static final int ET_EXEC = 2;		/* Executable file */
    public static final int ET_DYN = 3;		/* Shared object file */
    public static final int ET_CORE = 4;		/* Core file */
    public static final int ET_NUM = 5;		/* Number of defined types */
    public static final int ET_LOOS = 0xfe00;		/* OS-specific range start */
    public static final int ET_HIOS = 0xfeff;		/* OS-specific range end */
    public static final int ET_LOPROC = 0xff00;		/* Processor-specific range start */
    public static final int ET_HIPROC = 0xffff;		/* Processor-specific range end */

    /* Legal values for e_machine (architecture).  */

    public static final int EM_NONE = 0;		/* No machine */
    public static final int EM_M32 = 1;		/* AT&T WE 32100 */
    public static final int EM_SPARC = 2;		/* SUN SPARC */
    public static final int EM_386 = 3;		/* Intel 80386 */
    public static final int EM_68K = 4;		/* Motorola m68k family */
    public static final int EM_88K = 5;		/* Motorola m88k family */
    public static final int EM_860 = 7;		/* Intel 80860 */
    public static final int EM_MIPS = 8;		/* MIPS R3000 big-endian */
    public static final int EM_S370 = 9;		/* IBM System/370 */
    public static final int EM_MIPS_RS3_LE = 10;		/* MIPS R3000 little-endian */

    public static final int EM_PARISC = 15;		/* HPPA */
    public static final int EM_VPP500 = 17;		/* Fujitsu VPP500 */
    public static final int EM_SPARC32PLUS = 18;		/* Sun's "v8plus" */
    public static final int EM_960 = 19;		/* Intel 80960 */
    public static final int EM_PPC = 20;		/* PowerPC */
    public static final int EM_PPC64 = 21;		/* PowerPC 64-bit */
    public static final int EM_S390 = 22;		/* IBM S390 */

    public static final int EM_V800 = 36;		/* NEC V800 series */
    public static final int EM_FR20 = 37;		/* Fujitsu FR20 */
    public static final int EM_RH32 = 38;		/* TRW RH-32 */
    public static final int EM_RCE = 39;		/* Motorola RCE */
    public static final int EM_ARM = 40;		/* ARM */
    public static final int EM_FAKE_ALPHA = 41;		/* Digital Alpha */
    public static final int EM_SH = 42;		/* Hitachi SH */
    public static final int EM_SPARCV9 = 43;		/* SPARC v9 64-bit */
    public static final int EM_TRICORE = 44;		/* Siemens Tricore */
    public static final int EM_ARC = 45;		/* Argonaut RISC Core */
    public static final int EM_H8_300 = 46;		/* Hitachi H8/300 */
    public static final int EM_H8_300H = 47;		/* Hitachi H8/300H */
    public static final int EM_H8S = 48;		/* Hitachi H8S */
    public static final int EM_H8_500 = 49;		/* Hitachi H8/500 */
    public static final int EM_IA_64 = 50;		/* Intel Merced */
    public static final int EM_MIPS_X = 51;		/* Stanford MIPS-X */
    public static final int EM_COLDFIRE = 52;		/* Motorola Coldfire */
    public static final int EM_68HC12 = 53;		/* Motorola M68HC12 */
    public static final int EM_MMA = 54;		/* Fujitsu MMA Multimedia Accelerator*/
    public static final int EM_PCP = 55;		/* Siemens PCP */
    public static final int EM_NCPU = 56;		/* Sony nCPU embeeded RISC */
    public static final int EM_NDR1 = 57;		/* Denso NDR1 microprocessor */
    public static final int EM_STARCORE = 58;		/* Motorola Start*Core processor */
    public static final int EM_ME16 = 59;		/* Toyota ME16 processor */
    public static final int EM_ST100 = 60;		/* STMicroelectronic ST100 processor */
    public static final int EM_TINYJ = 61;		/* Advanced Logic Corp. Tinyj emb.fam*/
    public static final int EM_X86_64 = 62;		/* AMD x86-64 architecture */
    public static final int EM_PDSP = 63;		/* Sony DSP Processor */

    public static final int EM_FX66 = 66;		/* Siemens FX66 microcontroller */
    public static final int EM_ST9PLUS = 67;		/* STMicroelectronics ST9+ 8/16 mc */
    public static final int EM_ST7 = 68;		/* STmicroelectronics ST7 8 bit mc */
    public static final int EM_68HC16 = 69;		/* Motorola MC68HC16 microcontroller */
    public static final int EM_68HC11 = 70;		/* Motorola MC68HC11 microcontroller */
    public static final int EM_68HC08 = 71;		/* Motorola MC68HC08 microcontroller */
    public static final int EM_68HC05 = 72;		/* Motorola MC68HC05 microcontroller */
    public static final int EM_SVX = 73;		/* Silicon Graphics SVx */
    public static final int EM_ST19 = 74;		/* STMicroelectronics ST19 8 bit mc */
    public static final int EM_VAX = 75;		/* Digital VAX */
    public static final int EM_CRIS = 76;		/* Axis Communications 32-bit embedded processor */
    public static final int EM_JAVELIN = 77;		/* Infineon Technologies 32-bit embedded processor */
    public static final int EM_FIREPATH = 78;		/* Element 14 64-bit DSP Processor */
    public static final int EM_ZSP = 79;		/* LSI Logic 16-bit DSP Processor */
    public static final int EM_MMIX = 80;		/* Donald Knuth's educational 64-bit processor */
    public static final int EM_HUANY = 81;		/* Harvard University machine-independent object files */
    public static final int EM_PRISM = 82;		/* SiTera Prism */
    public static final int EM_AVR = 83;		/* Atmel AVR 8-bit microcontroller */
    public static final int EM_FR30 = 84;		/* Fujitsu FR30 */
    public static final int EM_D10V = 85;		/* Mitsubishi D10V */
    public static final int EM_D30V = 86;		/* Mitsubishi D30V */
    public static final int EM_V850 = 87;		/* NEC v850 */
    public static final int EM_M32R = 88;		/* Mitsubishi M32R */
    public static final int EM_MN10300 = 89;		/* Matsushita MN10300 */
    public static final int EM_MN10200 = 90;		/* Matsushita MN10200 */
    public static final int EM_PJ = 91;		/* picoJava */
    public static final int EM_OPENRISC = 92;		/* OpenRISC 32-bit embedded processor */
    public static final int EM_ARC_A5 = 93;		/* ARC Cores Tangent-A5 */
    public static final int EM_XTENSA = 94;		/* Tensilica Xtensa Architecture */
    public static final int EM_NUM = 95;

	/* If it is necessary to assign new unofficial EM_* values, please
    pick large random numbers (0x8523, 0xa7f2, etc.) to minimize the
	chances of collision with official or non-GNU unofficial values.  */

    public static final int EM_ALPHA = 0x9026;

	/* Legal values for e_version (version).  */

    public static final int EV_NONE = 0;		/* Invalid ELF version */
    public static final int EV_CURRENT = 1;		/* Current version */
    public static final int EV_NUM = 2;

    public static final int SHN_UNDEF = 0;		/* Undefined section */
    public static final int SHN_LORESERVE = 0xff00;		/* Start of reserved indices */
    public static final int SHN_LOPROC = 0xff00;		/* Start of processor-specific */
    public static final int SHN_BEFORE = 0xff00;		/* Order section before all others (Solaris).  */
    public static final int SHN_AFTER = 0xff01;		/* Order section after all others (Solaris).  */
    public static final int SHN_HIPROC = 0xff1f;		/* End of processor-specific */
    public static final int SHN_LOOS = 0xff20;		/* Start of OS-specific */
    public static final int SHN_HIOS = 0xff3f;		/* End of OS-specific */
    public static final int SHN_ABS = 0xfff1;		/* Associated symbol is absolute */
    public static final int SHN_COMMON = 0xfff2;		/* Associated symbol is common */
    public static final int SHN_XINDEX = 0xffff;		/* Index is in extra table.  */
    public static final int SHN_HIRESERVE = 0xffff;		/* End of reserved indices */

	/* Legal values for sh_type (section type).  */

    public static final int SHT_NULL = 0;		/* Section header table entry unused */
    public static final int SHT_PROGBITS = 1;		/* Program data */
    public static final int SHT_SYMTAB = 2;		/* Symbol table */
    public static final int SHT_STRTAB = 3;		/* String table */
    public static final int SHT_RELA = 4;		/* Relocation entries with addends */
    public static final int SHT_HASH = 5;		/* Symbol hash table */
    public static final int SHT_DYNAMIC = 6;		/* Dynamic linking information */
    public static final int SHT_NOTE = 7;		/* Notes */
    public static final int SHT_NOBITS = 8;		/* Program space with no data (bss) */
    public static final int SHT_REL = 9;		/* Relocation entries, no addends */
    public static final int SHT_SHLIB = 10;		/* Reserved */
    public static final int SHT_DYNSYM = 11;		/* Dynamic linker symbol table */
    public static final int SHT_INIT_ARRAY = 14;		/* Array of constructors */
    public static final int SHT_FINI_ARRAY = 15;		/* Array of destructors */
    public static final int SHT_PREINIT_ARRAY = 16;		/* Array of pre-constructors */
    public static final int SHT_GROUP = 17;		/* Section group */
    public static final int SHT_SYMTAB_SHNDX = 18;		/* Extended section indeces */
    public static final int SHT_NUM = 19;		/* Number of defined types.  */
    public static final int SHT_LOOS = 0x60000000;	/* Start OS-specific.  */
    public static final int SHT_GNU_ATTRIBUTES = 0x6ffffff5;	/* Object attributes.  */
    public static final int SHT_GNU_HASH = 0x6ffffff6;	/* GNU-style hash table.  */
    public static final int SHT_GNU_LIBLIST = 0x6ffffff7;	/* Prelink library list */
    public static final int SHT_CHECKSUM = 0x6ffffff8;	/* Checksum for DSO content.  */
    public static final int SHT_LOSUNW = 0x6ffffffa;	/* Sun-specific low bound.  */
    public static final int SHT_SUNW_move = 0x6ffffffa;
    public static final int SHT_SUNW_COMDAT = 0x6ffffffb;
    public static final int SHT_SUNW_syminfo = 0x6ffffffc;
    public static final int SHT_GNU_verdef = 0x6ffffffd;	/* Version definition section.  */
    public static final int SHT_GNU_verneed = 0x6ffffffe;	/* Version needs section.  */
    public static final int SHT_GNU_versym = 0x6fffffff;	/* Version symbol table.  */
    public static final int SHT_HISUNW = 0x6fffffff;	/* Sun-specific high bound.  */
    public static final int SHT_HIOS = 0x6fffffff;	/* End OS-specific type */
    public static final int SHT_LOPROC = 0x70000000;	/* Start of processor-specific */
    public static final int SHT_HIPROC = 0x7fffffff;	/* End of processor-specific */
    public static final int SHT_LOUSER = 0x80000000;	/* Start of application-specific */
    public static final int SHT_HIUSER = 0x8fffffff;	/* End of application-specific */

	/* Legal values for sh_flags (section flags).  */

    public static final int SHF_WRITE = (1 << 0);	/* Writable */
    public static final int SHF_ALLOC = (1 << 1);	/* Occupies memory during execution */
    public static final int SHF_EXECINSTR = (1 << 2);	/* Executable */
    public static final int SHF_MERGE = (1 << 4);	/* Might be merged */
    public static final int SHF_STRINGS = (1 << 5);	/* Contains nul-terminated strings */
    public static final int SHF_INFO_LINK = (1 << 6);	/* `sh_info' contains SHT index */
    public static final int SHF_LINK_ORDER = (1 << 7);	/* Preserve order after combining */
    public static final int SHF_OS_NONCONFORMING = (1 << 8);	/* Non-standard OS specific handling required */
    public static final int SHF_GROUP = (1 << 9);	/* Section is member of a group.  */
    public static final int SHF_TLS = (1 << 10);	/* Section hold thread-local data.  */
    public static final int SHF_MASKOS = 0x0ff00000;	/* OS-specific.  */
    public static final int SHF_MASKPROC = 0xf0000000;	/* Processor-specific */
    public static final int SHF_ORDERED = (1 << 30);	/* Special ordering requirement (Solaris).  */
    public static final int SHF_EXCLUDE = (1 << 31);	/* Section is excluded unless referenced or allocated (Solaris).*/

    /* Section group handling.  */
    public static final int GRP_COMDAT = 0x1;		/* Mark group as COMDAT.  */

    /* Possible values for si_boundto.  */
    public static final int SYMINFO_BT_SELF = 0xffff;	/* Symbol bound to self */
    public static final int SYMINFO_BT_PARENT = 0xfffe;	/* Symbol bound to parent */
    public static final int SYMINFO_BT_LOWRESERVE = 0xff00;	/* Beginning of reserved entries */

    /* Possible bitmasks for si_flags.  */
    public static final int SYMINFO_FLG_DIRECT = 0x0001;	/* Direct bound symbol */
    public static final int SYMINFO_FLG_PASSTHRU = 0x0002;	/* Pass-thru symbol for translator */
    public static final int SYMINFO_FLG_COPY = 0x0004;	/* Symbol is a copy-reloc */
    public static final int SYMINFO_FLG_LAZYLOAD = 0x0008;	/* Symbol bound to object to be lazy
					   loaded */
    /* Syminfo version values.  */
    public static final int SYMINFO_NONE = 0;
    public static final int SYMINFO_CURRENT = 1;
    public static final int SYMINFO_NUM = 2;

    /* Legal values for ST_BIND subfield of st_info (symbol binding).  */

    public static final int STB_LOCAL = 0;		/* Local symbol */
    public static final int STB_GLOBAL = 1;		/* Global symbol */
    public static final int STB_WEAK = 2;		/* Weak symbol */
    public static final int STB_NUM = 3;		/* Number of defined types.  */
    public static final int STB_LOOS = 10;		/* Start of OS-specific */
    public static final int STB_GNU_UNIQUE = 10;		/* Unique symbol.  */
    public static final int STB_HIOS = 12;		/* End of OS-specific */
    public static final int STB_LOPROC = 13;		/* Start of processor-specific */
    public static final int STB_HIPROC = 15;		/* End of processor-specific */

    /* Legal values for ST_TYPE subfield of st_info (symbol type).  */

    public static final int STT_NOTYPE = 0;		/* Symbol type is unspecified */
    public static final int STT_OBJECT = 1;		/* Symbol is a data object */
    public static final int STT_FUNC = 2;		/* Symbol is a code object */
    public static final int STT_SECTION = 3;		/* Symbol associated with a section */
    public static final int STT_FILE = 4;		/* Symbol's name is file name */
    public static final int STT_COMMON = 5;		/* Symbol is a common data object */
    public static final int STT_TLS = 6;		/* Symbol is thread-local data object*/
    public static final int STT_NUM = 7;		/* Number of defined types.  */
    public static final int STT_LOOS = 10;		/* Start of OS-specific */
    public static final int STT_GNU_IFUNC = 10;		/* Symbol is indirect code object */
    public static final int STT_HIOS = 12;		/* End of OS-specific */
    public static final int STT_LOPROC = 13;		/* Start of processor-specific */
    public static final int STT_HIPROC = 15;		/* End of processor-specific */

    /* Symbol table indices are found in the hash buckets and chain table
       of a symbol hash table section.  This special index value indicates
       the end of a chain, meaning no further symbols are found in that bucket.  */

    public static final int STN_UNDEF = 0;		/* End of a chain.  */

    /* Symbol visibility specification encoded in the st_other field.  */
    public static final int STV_DEFAULT = 0;		/* Default symbol visibility rules */
    public static final int STV_INTERNAL = 1;		/* Processor specific hidden class */
    public static final int STV_HIDDEN = 2;		/* Sym unavailable in other modules */
    public static final int STV_PROTECTED = 3;		/* Not preemptible, not exported */

    /* Legal values for p_type (segment type).  */

    public static final int PT_NULL = 0;		/* Program header table entry unused */
    public static final int PT_LOAD = 1;		/* Loadable program segment */
    public static final int PT_DYNAMIC = 2;		/* Dynamic linking information */
    public static final int PT_INTERP = 3;		/* Program interpreter */
    public static final int PT_NOTE = 4;		/* Auxiliary information */
    public static final int PT_SHLIB = 5;		/* Reserved */
    public static final int PT_PHDR = 6;		/* Entry for header table itself */
    public static final int PT_TLS = 7;		/* Thread-local storage segment */
    public static final int PT_NUM = 8;		/* Number of defined types */
    public static final int PT_LOOS = 0x60000000;	/* Start of OS-specific */
    public static final int PT_GNU_EH_FRAME = 0x6474e550;	/* GCC .eh_frame_hdr segment */
    public static final int PT_GNU_STACK = 0x6474e551;	/* Indicates stack executability */
    public static final int PT_GNU_RELRO = 0x6474e552;	/* Read-only after relocation */
    public static final int PT_LOSUNW = 0x6ffffffa;
    public static final int PT_SUNWBSS = 0x6ffffffa;	/* Sun Specific segment */
    public static final int PT_SUNWSTACK = 0x6ffffffb;	/* Stack segment */
    public static final int PT_HISUNW = 0x6fffffff;
    public static final int PT_HIOS = 0x6fffffff;	/* End of OS-specific */
    public static final int PT_LOPROC = 0x70000000;	/* Start of processor-specific */
    public static final int PT_HIPROC = 0x7fffffff;	/* End of processor-specific */

    /* Legal values for p_flags (segment flags).  */

    public static final int PF_X = (1 << 0);	/* Segment is executable */
    public static final int PF_W = (1 << 1);	/* Segment is writable */
    public static final int PF_R = (1 << 2);	/* Segment is readable */

    public static final int PF_MASKOS = 0x0ff00000;	/* OS-specific */
    public static final int PF_MASKPROC = 0xf0000000;	/* Processor-specific */

    /* Legal values for note segment descriptor types for core files. */

    public static final int NT_PRSTATUS = 1;		/* Contains copy of prstatus struct */
    public static final int NT_FPREGSET = 2;		/* Contains copy of fpregset struct */
    public static final int NT_PRPSINFO = 3;		/* Contains copy of prpsinfo struct */
    public static final int NT_PRXREG = 4;		/* Contains copy of prxregset struct */
    public static final int NT_TASKSTRUCT = 4;		/* Contains copy of task structure */
    public static final int NT_PLATFORM = 5;		/* String from sysinfo(SI_PLATFORM) */
    public static final int NT_AUXV = 6;		/* Contains copy of auxv array */
    public static final int NT_GWINDOWS = 7;		/* Contains copy of gwindows struct */
    public static final int NT_ASRS = 8;		/* Contains copy of asrset struct */
    public static final int NT_PSTATUS = 10;		/* Contains copy of pstatus struct */
    public static final int NT_PSINFO = 13;		/* Contains copy of psinfo struct */
    public static final int NT_PRCRED = 14;		/* Contains copy of prcred struct */
    public static final int NT_UTSNAME = 15;		/* Contains copy of utsname struct */
    public static final int NT_LWPSTATUS = 16;		/* Contains copy of lwpstatus struct */
    public static final int NT_LWPSINFO = 17;		/* Contains copy of lwpinfo struct */
    public static final int NT_PRFPXREG = 20;		/* Contains copy of fprxregset struct */
    public static final int NT_PRXFPREG = 0x46e62b7f;	/* Contains copy of user_fxsr_struct */
    public static final int NT_PPC_VMX = 0x100;		/* PowerPC Altivec/VMX registers */
    public static final int NT_PPC_SPE = 0x101;		/* PowerPC SPE/EVR registers */
    public static final int NT_PPC_VSX = 0x102;		/* PowerPC VSX registers */
    public static final int NT_386_TLS = 0x200;		/* i386 TLS slots (struct user_desc) */
    public static final int NT_386_IOPERM = 0x201;		/* x86 io permission bitmap (1=deny) */

    /* Legal values for the note segment descriptor types for object files.  */

    public static final int NT_VERSION = 1;		/* Contains a version string.  */

    /* Legal values for d_tag (dynamic entry type).  */

    public static final int DT_NULL = 0;		/* Marks end of dynamic section */
    public static final int DT_NEEDED = 1;		/* Name of needed library */
    public static final int DT_PLTRELSZ = 2;		/* Size in bytes of PLT relocs */
    public static final int DT_PLTGOT = 3;		/* Processor defined value */
    public static final int DT_HASH = 4;		/* Address of symbol hash table */
    public static final int DT_STRTAB = 5;		/* Address of string table */
    public static final int DT_SYMTAB = 6;		/* Address of symbol table */
    public static final int DT_RELA = 7;		/* Address of Rela relocs */
    public static final int DT_RELASZ = 8;		/* Total size of Rela relocs */
    public static final int DT_RELAENT = 9;		/* Size of one Rela reloc */
    public static final int DT_STRSZ = 10;		/* Size of string table */
    public static final int DT_SYMENT = 11;		/* Size of one symbol table entry */
    public static final int DT_INIT = 12;		/* Address of init function */
    public static final int DT_FINI = 13;		/* Address of termination function */
    public static final int DT_SONAME = 14;		/* Name of shared object */
    public static final int DT_RPATH = 15;		/* Library search path (deprecated) */
    public static final int DT_SYMBOLIC = 16;		/* Start symbol search here */
    public static final int DT_REL = 17;		/* Address of Rel relocs */
    public static final int DT_RELSZ = 18;		/* Total size of Rel relocs */
    public static final int DT_RELENT = 19;		/* Size of one Rel reloc */
    public static final int DT_PLTREL = 20;		/* Type of reloc in PLT */
    public static final int DT_DEBUG = 21;		/* For debugging; unspecified */
    public static final int DT_TEXTREL = 22;		/* Reloc might modify .text */
    public static final int DT_JMPREL = 23;		/* Address of PLT relocs */
    public static final int DT_BIND_NOW = 24;		/* Process relocations of object */
    public static final int DT_INIT_ARRAY = 25;		/* Array with addresses of init fct */
    public static final int DT_FINI_ARRAY = 26;		/* Array with addresses of fini fct */
    public static final int DT_INIT_ARRAYSZ = 27;		/* Size in bytes of DT_INIT_ARRAY */
    public static final int DT_FINI_ARRAYSZ = 28;		/* Size in bytes of DT_FINI_ARRAY */
    public static final int DT_RUNPATH = 29;		/* Library search path */
    public static final int DT_FLAGS = 30;		/* Flags for the object being loaded */
    public static final int DT_ENCODING = 32;		/* Start of encoded range */
    public static final int DT_PREINIT_ARRAY = 32;		/* Array with addresses of preinit fct*/
    public static final int DT_PREINIT_ARRAYSZ = 33;		/* size in bytes of DT_PREINIT_ARRAY */
    public static final int DT_NUM = 34;		/* Number used */
    public static final int DT_LOOS = 0x6000000d;	/* Start of OS-specific */
    public static final int DT_HIOS = 0x6ffff000;	/* End of OS-specific */
    public static final int DT_LOPROC = 0x70000000;	/* Start of processor-specific */
    public static final int DT_HIPROC = 0x7fffffff;	/* End of processor-specific */
    public static final int DT_MIPS_NUM = 0x35;
    public static final int DT_PROCNUM = DT_MIPS_NUM;	/* Most used by any processor */

    /* DT_* entries which fall between DT_VALRNGHI & DT_VALRNGLO use the
       Dyn.d_un.d_val field of the Elf*_Dyn structure.  This follows Sun's
       approach.  */
    public static final int DT_VALRNGLO = 0x6ffffd00;
    public static final int DT_GNU_PRELINKED = 0x6ffffdf5;	/* Prelinking timestamp */
    public static final int DT_GNU_CONFLICTSZ = 0x6ffffdf6;	/* Size of conflict section */
    public static final int DT_GNU_LIBLISTSZ = 0x6ffffdf7;	/* Size of library list */
    public static final int DT_CHECKSUM = 0x6ffffdf8;
    public static final int DT_PLTPADSZ = 0x6ffffdf9;
    public static final int DT_MOVEENT = 0x6ffffdfa;
    public static final int DT_MOVESZ = 0x6ffffdfb;
    public static final int DT_FEATURE_1 = 0x6ffffdfc;	/* Feature selection (DTF_*).  */
    public static final int DT_POSFLAG_1 = 0x6ffffdfd;	/* Flags for DT_* entries, effecting the following DT_* entry.  */
    public static final int DT_SYMINSZ = 0x6ffffdfe;	/* Size of syminfo table (in bytes) */
    public static final int DT_SYMINENT = 0x6ffffdff;	/* Entry size of syminfo */
    public static final int DT_VALRNGHI = 0x6ffffdff;
    // public static final int  DT_VALTAGIDX = (tag)	(DT_VALRNGHI - (tag));	/* Reverse order! */
    public static final int DT_VALNUM = 12;

    /* DT_* entries which fall between DT_ADDRRNGHI & DT_ADDRRNGLO use the
       Dyn.d_un.d_ptr field of the Elf*_Dyn structure.
       If any adjustment is made to the ELF object after it has been
       built these entries will need to be adjusted.  */
    public static final int DT_ADDRRNGLO = 0x6ffffe00;
    public static final int DT_GNU_HASH = 0x6ffffef5;	/* GNU-style hash table.  */
    public static final int DT_TLSDESC_PLT = 0x6ffffef6;
    public static final int DT_TLSDESC_GOT = 0x6ffffef7;
    public static final int DT_GNU_CONFLICT = 0x6ffffef8;	/* Start of conflict section */
    public static final int DT_GNU_LIBLIST = 0x6ffffef9;	/* Library list */
    public static final int DT_CONFIG = 0x6ffffefa;	/* Configuration information.  */
    public static final int DT_DEPAUDIT = 0x6ffffefb;	/* Dependency auditing.  */
    public static final int DT_AUDIT = 0x6ffffefc;	/* Object auditing.  */
    public static final int DT_PLTPAD = 0x6ffffefd;	/* PLT padding.  */
    public static final int DT_MOVETAB = 0x6ffffefe;	/* Move table.  */
    public static final int DT_SYMINFO = 0x6ffffeff;	/* Syminfo table.  */
    public static final int DT_ADDRRNGHI = 0x6ffffeff;
    // public static final int  DT_ADDRTAGIDX = (;tag)	(DT_ADDRRNGHI - (tag))	/* Reverse order! */
    public static final int DT_ADDRNUM = 11;

    /* The versioning entry types.  The next are defined as part of the
       GNU extension.  */
    public static final int DT_VERSYM = 0x6ffffff0;

    public static final int DT_RELACOUNT = 0x6ffffff9;
    public static final int DT_RELCOUNT = 0x6ffffffa;

    /* These were chosen by Sun.  */
    public static final int DT_FLAGS_1 = 0x6ffffffb;	/* State flags, see DF_1_* below.  */
    public static final int DT_VERDEF = 0x6ffffffc;	/* Address of version definition table */
    public static final int DT_VERDEFNUM = 0x6ffffffd;	/* Number of version definitions */
    public static final int DT_VERNEED = 0x6ffffffe;	/* Address of table with needed versions */
    public static final int DT_VERNEEDNUM = 0x6fffffff;	/* Number of needed versions */
    // DT_VERSIONTAGIDX(tag)	(DT_VERNEEDNUM - (tag))	/* Reverse order! */
    public static final int DT_VERSIONTAGNUM = 16;

    /* Sun added these machine-independent extensions in the "processor-specific"
       range.  Be compatible.  */
    public static final int DT_AUXILIARY = 0x7ffffffd;      /* Shared object to load before self */
    public static final int DT_FILTER = 0x7fffffff;      /* Shared object to get values from */
    // #define DT_EXTRATAGIDX(tag)	((Elf32_Word)-((Elf32_Sword) (tag) <<1>>1)-1)
    public static final int DT_EXTRANUM = 3;

    /* Values of `d_un.d_val' in the DT_FLAGS entry.  */
    public static final int DF_ORIGIN = 0x00000001;	/* Object may use DF_ORIGIN */
    public static final int DF_SYMBOLIC = 0x00000002;	/* Symbol resolutions starts here */
    public static final int DF_TEXTREL = 0x00000004;	/* Object contains text relocations */
    public static final int DF_BIND_NOW = 0x00000008;	/* No lazy binding for this object */
    public static final int DF_STATIC_TLS = 0x00000010;	/* Module uses the static TLS model */

    /* State flags selectable in the `d_un.d_val' element of the DT_FLAGS_1
       entry in the dynamic section.  */
    public static final int DF_1_NOW = 0x00000001;	/* Set RTLD_NOW for this object.  */
    public static final int DF_1_GLOBAL = 0x00000002;	/* Set RTLD_GLOBAL for this object.  */
    public static final int DF_1_GROUP = 0x00000004;	/* Set RTLD_GROUP for this object.  */
    public static final int DF_1_NODELETE = 0x00000008;	/* Set RTLD_NODELETE for this object.*/
    public static final int DF_1_LOADFLTR = 0x00000010;	/* Trigger filtee loading at runtime.*/
    public static final int DF_1_INITFIRST = 0x00000020;	/* Set RTLD_INITFIRST for this object*/
    public static final int DF_1_NOOPEN = 0x00000040;	/* Set RTLD_NOOPEN for this object.  */
    public static final int DF_1_ORIGIN = 0x00000080;	/* $ORIGIN must be handled.  */
    public static final int DF_1_DIRECT = 0x00000100;	/* Direct binding enabled.  */
    public static final int DF_1_TRANS = 0x00000200;
    public static final int DF_1_INTERPOSE = 0x00000400;	/* Object is used to interpose.  */
    public static final int DF_1_NODEFLIB = 0x00000800;	/* Ignore default lib search path.  */
    public static final int DF_1_NODUMP = 0x00001000;	/* Object can't be dldump'ed.  */
    public static final int DF_1_CONFALT = 0x00002000;	/* Configuration alternative created.*/
    public static final int DF_1_ENDFILTEE = 0x00004000;	/* Filtee terminates filters search. */
    public static final int DF_1_DISPRELDNE = 0x00008000;	/* Disp reloc applied at build time. */
    public static final int DF_1_DISPRELPND = 0x00010000;	/* Disp reloc applied at run-time.  */

    /* Flags for the feature selection in DT_FEATURE_1.  */
    public static final int DTF_1_PARINIT = 0x00000001;
    public static final int DTF_1_CONFEXP = 0x00000002;

    /* Flags in the DT_POSFLAG_1 entry effecting only the next DT_* entry.  */
    public static final int DF_P1_LAZYLOAD = 0x00000001;	/* Lazyload following object.  */
    public static final int DF_P1_GROUPPERM = 0x00000002;	/* Symbols from next object are not
    /* Legal values for vd_version (version revision).  */
    public static final int VER_DEF_NONE = 0;		/* No version */
    public static final int VER_DEF_CURRENT = 1;		/* Current version */
    public static final int VER_DEF_NUM = 2;		/* Given version number */

    /* Legal values for vd_flags (version information flags).  */
    public static final int VER_FLG_BASE = 0x1;		/* Version definition of file itself */
    public static final int VER_FLG_WEAK = 0x2;		/* Weak version identifier */

    /* Versym symbol index values.  */
    public static final int VER_NDX_LOCAL = 0;	/* Symbol is local.  */
    public static final int VER_NDX_GLOBAL = 1;	/* Symbol is global.  */
    public static final int VER_NDX_LORESERVE = 0xff00;	/* Beginning of reserved entries.  */
    public static final int VER_NDX_ELIMINATE = 0xff01;	/* Symbol is to be eliminated.  generally available.  */

    /* Legal values for a_type (entry type).  */

    public static final int AT_NULL = 0;		/* End of vector */
    public static final int AT_IGNORE = 1;		/* Entry should be ignored */
    public static final int AT_EXECFD = 2;		/* File descriptor of program */
    public static final int AT_PHDR = 3;		/* Program headers for program */
    public static final int AT_PHENT = 4;		/* Size of program header entry */
    public static final int AT_PHNUM = 5;		/* Number of program headers */
    public static final int AT_PAGESZ = 6;		/* System page size */
    public static final int AT_BASE = 7;		/* Base address of interpreter */
    public static final int AT_FLAGS = 8;		/* Flags */
    public static final int AT_ENTRY = 9;		/* Entry point of program */
    public static final int AT_NOTELF = 10;		/* Program is not ELF */
    public static final int AT_UID = 11;		/* Real uid */
    public static final int AT_EUID = 12;		/* Effective uid */
    public static final int AT_GID = 13;		/* Real gid */
    public static final int AT_EGID = 14;		/* Effective gid */
    public static final int AT_CLKTCK = 17;		/* Frequency of times() */

    /* Some more special a_type values describing the hardware.  */
    public static final int AT_PLATFORM = 15;		/* String identifying platform.  */
    public static final int AT_HWCAP = 16;		/* Machine dependent hints about processor capabilities.  */

    /* This entry gives some information about the FPU initialization
       performed by the kernel.  */
    public static final int AT_FPUCW = 18;		/* Used FPU control word.  */

    /* Cache block sizes.  */
    public static final int AT_DCACHEBSIZE = 19;		/* Data cache block size.  */
    public static final int AT_ICACHEBSIZE = 20;		/* Instruction cache block size.  */
    public static final int AT_UCACHEBSIZE = 21;		/* Unified cache block size.  */

    /* A special ignored value for PPC, used by the kernel to control the
       interpretation of the AUXV. Must be > 16.  */
    public static final int AT_IGNOREPPC = 22;		/* Entry should be ignored.  */
    public static final int AT_SECURE = 23;		/* Boolean, was exec setuid-like?  */
    public static final int AT_BASE_PLATFORM = 24;		/* String identifying real platforms.*/
    public static final int AT_RANDOM = 25;		/* Address of 16 random bytes.  */
    public static final int AT_EXECFN = 31;		/* Filename of executable.  */

    /* Pointer to the global system page used for system calls and other
       nice things.  */
    public static final int AT_SYSINFO = 32;
    public static final int AT_SYSINFO_EHDR = 33;

    /* Shapes of the caches.  Bits 0-3 contains associativity; bits 4-7 contains
       log2 of line size; mask those to get cache size.  */
    public static final int AT_L1I_CACHESHAPE = 34;
    public static final int AT_L1D_CACHESHAPE = 35;
    public static final int AT_L2_CACHESHAPE = 36;
    public static final int AT_L3_CACHESHAPE = 37;

    /* i386 relocs.  */

    public static final int R_386_NONE = 0;		/* No reloc */
    public static final int R_386_32 = 1;		/* Direct 32 bit  */
    public static final int R_386_PC32 = 2;		/* PC relative 32 bit */
    public static final int R_386_GOT32 = 3;		/* 32 bit GOT entry */
    public static final int R_386_PLT32 = 4;		/* 32 bit PLT address */
    public static final int R_386_COPY = 5;		/* Copy symbol at runtime */
    public static final int R_386_GLOB_DAT = 6;		/* Create GOT entry */
    public static final int R_386_JMP_SLOT = 7;		/* Create PLT entry */
    public static final int R_386_RELATIVE = 8;		/* Adjust by program base */
    public static final int R_386_GOTOFF = 9;		/* 32 bit offset to GOT */
    public static final int R_386_GOTPC = 10;		/* 32 bit PC relative offset to GOT */
    public static final int R_386_32PLT = 11;
    public static final int R_386_TLS_TPOFF = 14;		/* Offset in static TLS block */
    public static final int R_386_TLS_IE = 15;		/* Address of GOT entry for static TLS block offset */
    public static final int R_386_TLS_GOTIE = 16;		/* GOT entry for static TLS block offset */
    public static final int R_386_TLS_LE = 17;		/* Offset relative to static TLS block */
    public static final int R_386_TLS_GD = 18;		/* Direct 32 bit for GNU version of general dynamic thread local data */
    public static final int R_386_TLS_LDM = 19;		/* Direct 32 bit for GNU version of local dynamic thread local data in LE code */
    public static final int R_386_16 = 20;
    public static final int R_386_PC16 = 21;
    public static final int R_386_8 = 22;
    public static final int R_386_PC8 = 23;
    public static final int R_386_TLS_GD_32 = 24;		/* Direct 32 bit for general dynamic thread local data */
    public static final int R_386_TLS_GD_PUSH = 25;		/* Tag for pushl in GD TLS code */
    public static final int R_386_TLS_GD_CALL = 26;		/* Relocation for call to __tls_get_addr() */
    public static final int R_386_TLS_GD_POP = 27;		/* Tag for popl in GD TLS code */
    public static final int R_386_TLS_LDM_32 = 28;		/* Direct 32 bit for local dynamic thread local data in LE code */
    public static final int R_386_TLS_LDM_PUSH = 29;		/* Tag for pushl in LDM TLS code */
    public static final int R_386_TLS_LDM_CALL = 30;		/* Relocation for call to __tls_get_addr() in LDM code */
    public static final int R_386_TLS_LDM_POP = 31;		/* Tag for popl in LDM TLS code */
    public static final int R_386_TLS_LDO_32 = 32;		/* Offset relative to TLS block */
    public static final int R_386_TLS_IE_32 = 33;		/* GOT entry for negated static TLS block offset */
    public static final int R_386_TLS_LE_32 = 34;		/* Negated offset relative to static TLS block */
    public static final int R_386_TLS_DTPMOD32 = 35;		/* ID of module containing symbol */
    public static final int R_386_TLS_DTPOFF32 = 36;		/* Offset in TLS block */
    public static final int R_386_TLS_TPOFF32 = 37;		/* Negated offset in static TLS block */ /* 38? */
    public static final int R_386_TLS_GOTDESC = 39;		/* GOT offset for TLS descriptor.  */
    public static final int R_386_TLS_DESC_CALL = 40;		/* Marker of call through TLS descriptor for relaxation.  */
    public static final int R_386_TLS_DESC = 41;		/* TLS descriptor containing pointer to code and to argument, returning the TLS offset for the symbol.  */
    public static final int R_386_IRELATIVE = 42;		/* Adjust indirectly by program base */
    /* Keep this the last entry.  */
    public static final int R_386_NUM = 43;

    /* AMD x86-64 relocations.  */
    public static final int R_X86_64_NONE = 0;	/* No reloc */
    public static final int R_X86_64_64 = 1;	/* Direct 64 bit  */
    public static final int R_X86_64_PC32 = 2;	/* PC relative 32 bit signed */
    public static final int R_X86_64_GOT32 = 3;	/* 32 bit GOT entry */
    public static final int R_X86_64_PLT32 = 4;	/* 32 bit PLT address */
    public static final int R_X86_64_COPY = 5;	/* Copy symbol at runtime */
    public static final int R_X86_64_GLOB_DAT = 6;	/* Create GOT entry */
    public static final int R_X86_64_JUMP_SLOT = 7;	/* Create PLT entry */
    public static final int R_X86_64_RELATIVE = 8;	/* Adjust by program base */
    public static final int R_X86_64_GOTPCREL = 9;	/* 32 bit signed PC relative offset to GOT */
    public static final int R_X86_64_32 = 10;	/* Direct 32 bit zero extended */
    public static final int R_X86_64_32S = 11;	/* Direct 32 bit sign extended */
    public static final int R_X86_64_16 = 12;	/* Direct 16 bit zero extended */
    public static final int R_X86_64_PC16 = 13;	/* 16 bit sign extended pc relative */
    public static final int R_X86_64_8 = 14;	/* Direct 8 bit sign extended  */
    public static final int R_X86_64_PC8 = 15;	/* 8 bit sign extended pc relative */
    public static final int R_X86_64_DTPMOD64 = 16;	/* ID of module containing symbol */
    public static final int R_X86_64_DTPOFF64 = 17;	/* Offset in module's TLS block */
    public static final int R_X86_64_TPOFF64 = 18;	/* Offset in initial TLS block */
    public static final int R_X86_64_TLSGD = 19;	/* 32 bit signed PC relative offset to two GOT entries for GD symbol */
    public static final int R_X86_64_TLSLD = 20;	/* 32 bit signed PC relative offset to two GOT entries for LD symbol */
    public static final int R_X86_64_DTPOFF32 = 21;	/* Offset in TLS block */
    public static final int R_X86_64_GOTTPOFF = 22;	/* 32 bit signed PC relative offset to GOT entry for IE symbol */
    public static final int R_X86_64_TPOFF32 = 23;	/* Offset in initial TLS block */
    public static final int R_X86_64_PC64 = 24;	/* PC relative 64 bit */
    public static final int R_X86_64_GOTOFF64 = 25;	/* 64 bit offset to GOT */
    public static final int R_X86_64_GOTPC32 = 26;	/* 32 bit signed pc relative offset to GOT */

    /* 27 .. 33 */
    public static final int R_X86_64_GOTPC32_TLSDESC = 34;	/* GOT offset for TLS descriptor.  */
    public static final int R_X86_64_TLSDESC_CALL = 35;	/* Marker for call through TLS descriptor.  */
    public static final int R_X86_64_TLSDESC = 36;	/* TLS descriptor.  */
    public static final int R_X86_64_IRELATIVE = 37;	/* Adjust indirectly by program base */

    public static final int R_X86_64_NUM = 38;

    public static byte[] loadFileintoBuffer(String pathname)
    {
        File file = new File(pathname);
        try {
            try (FileInputStream input = new FileInputStream(file)) {
                byte[] buffer = new byte[(int) file.length()];

                int len = buffer.length;
                int offset = 0;
                int n = 0;
                while (offset < len && n >= 0) {
                    n = input.read(buffer, offset, len - offset);
                    offset += n;
                }
                return buffer;
            }
        }
        catch (Exception e) {
            return null;
        }
    }

    public static class Elf64File
    {

        private final Pointer ptr;
        private final Runtime runtime;
        private final Elf64_Ehdr ehdr;
        private final Elf64_Shdr shdr;

        public Elf64File(Pointer ptr)
        {
            this.ptr = ptr;
            runtime = ptr.getRuntime();
            ehdr = new Elf64_Ehdr(runtime);
            shdr = new Elf64_Shdr(runtime);
        }

        public static class Section
        {

            private final Pointer headerPtr;
            private final String name;

            public Pointer getHeaderPtr()
            {
                return headerPtr;
            }

            public String getName()
            {
                return name;
            }

            public Section(Pointer headerPtr, String name)
            {
                this.headerPtr = headerPtr;
                this.name = name;
            }

            @Override
            public String toString()
            {
                return "Section{" +
                        "headerPtr=" + headerPtr +
                        ", name='" + name + '\'' +
                        '}';
            }
        }

        public void validateHeader()
        {
            if (ehdr.e_ident[EI_MAG0].get(ptr) != ELFMAG0 ||
                    ehdr.e_ident[EI_MAG1].get(ptr) != ELFMAG1 ||
                    ehdr.e_ident[EI_MAG2].get(ptr) != ELFMAG2 ||
                    ehdr.e_ident[EI_MAG3].get(ptr) != ELFMAG3) {
                throw new IllegalStateException("Invalid magic");
            }

            if (ehdr.e_ident[EI_CLASS].get(ptr) != ELFCLASS64) {
                throw new IllegalStateException("Not 64-bit file");
            }
        }

        public List<Section> readSections()
        {
            int numSections = ehdr.e_shnum.get(ptr);
            List<Section> sections = new ArrayList<>(numSections);
            long sectionHeaderOffset = ehdr.e_shoff.get(ptr);
            Pointer strSectionHeaderPtr = ptr.slice(sectionHeaderOffset + (shdr.size() * (ehdr.e_shstrndx.get(ptr))));
            Pointer strSectionPointer = ptr.slice(shdr.sh_offset.get(strSectionHeaderPtr));
            for (int i = 0; i < numSections; ++i) {
                Pointer headerPtr = ptr.slice(sectionHeaderOffset + (shdr.size() * i));
                Pointer namePtr = strSectionPointer.slice(shdr.sh_name.get(headerPtr));
                String name = namePtr.getString(0);
                Section section = new Section(headerPtr, name);
                sections.add(section);
            }
            return sections;
        }
    }

    public static void main(String[] args)
            throws Exception
    {
        byte[] buf = loadFileintoBuffer("/Users/wtimoney/projects/morgoth/attic/blosc-module/blosc/blosc.so");
        Runtime runtime = Runtime.getSystemRuntime();
        ByteBuffer byteBuf = ByteBuffer.wrap(buf).order(ByteOrder.LITTLE_ENDIAN);
        Pointer ptr = new ByteBufferMemoryIO(runtime, byteBuf);
        // for (int i = 0; i < hdr.e_ident.length; i++)
        //     System.out.println(hdr.e_ident[i].get(ptr));
        Elf64File elf64File = new Elf64File(ptr);
        elf64File.validateHeader();
        List<Elf64File.Section> sections = elf64File.readSections();
        System.out.println(sections);
    }
}
